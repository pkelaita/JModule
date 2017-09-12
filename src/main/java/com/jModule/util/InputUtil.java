package com.jModule.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Utility class to process user input on *nix terminals. This class is
 * currently untested on Windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.2.0
 *
 */
public class InputUtil {

	private final static byte DELETE_NUMPAD = 8;
	private final static byte TAB = 9;
	private final static byte ENTER = 10;
	private final static byte ESCAPE = 27;
	private final static byte UP_SEQ = 65;
	private final static byte DOWN_SEQ = 66;
	private final static byte RIGHT_SEQ = 67;
	private final static byte LEFT_SEQ = 68;
	private final static byte DELETE = 127;

	private static boolean historyEnabled = false;
	private static boolean tabToggleEnabled = false;

	public static void setHistoryEnabled(boolean enabled) {
		historyEnabled = enabled;
	}

	public static void setTabToggleEnabled(boolean enabled) {
		tabToggleEnabled = enabled;
	}

	private static ArrayList<String> history = new ArrayList<>();

	public static ArrayList<String> getHistory() {
		return history;
	}

	public static void addHistory(String entry) {
		history.add(0, entry);
	}

	/**
	 * Converts a String to an ArrayList of chars
	 * 
	 * @param str
	 *            String to be converted
	 * @return str as ArrayList
	 */
	private static ArrayList<Character> stringToCharList(String str) {
		ArrayList<Character> result = new ArrayList<>();
		for (int i = 0; str != null && i < str.length(); i++) {
			result.add(str.charAt(i));
		}
		return result;
	}

	/**
	 * Converts an ArrayList of chars to a string
	 * 
	 * @param list
	 *            ArrayList to be converted
	 * @return list as String
	 */
	private static String charListToString(ArrayList<Character> list) {
		String result = "";
		for (char c : list) {
			result += c;
		}
		return result;
	}

	/**
	 * Clears the line using the optimal number of backspace characters based on
	 * previously given commands and the characters on the screen
	 * 
	 * @param resultChars
	 *            user-given characters currently on the CLI
	 * @param prompt
	 *            module Prompt
	 */
	public static void clearLine(ArrayList<Character> resultChars, String prompt) {
		int[] lengths = new int[history.size() + 1];
		for (int i = 0; i < history.size(); i++) {
			lengths[i] = history.get(i).length();
		}
		lengths[history.size()] = resultChars.size();
		int max = Integer.MIN_VALUE;
		for (int length : lengths) {
			if (length > max) {
				max = length;
			}
		}

		for (int i = 0; i <= max + prompt.length(); i++) {
			System.out.print("\b \b");
		}
		System.out.print(prompt);
	}

	/**
	 * Finds the line that was passed in at a certain history index, deletes the
	 * current line, and prints the prompt and the passed arguments at that history
	 * index.
	 * 
	 * @param histIndex
	 * @param numChars
	 * @param prompt
	 * @return arguments passed at selected history index
	 * @throws IOException
	 */
	private static ArrayList<Character> toggleHistory(int histIndex, String prompt, ArrayList<Character> resultChars)
			throws IOException {
		ArrayList<Character> result = new ArrayList<Character>();

		// get args at history index
		String histEntry = history.get(histIndex);
		result = stringToCharList(histEntry);

		// print prompt and args at selected index
		clearLine(resultChars, prompt);
		for (char c : result) {
			System.out.print(c);
		}

		return result;
	}

	/**
	 * Takes in user input and prints result of each character. Currently able to
	 * process all standard chars plus 'enter' and 'backspace' keystrokes.
	 * 
	 * @return result of user input
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String promptUserInput(ArrayList<String> commandReferences, String prompt)
			throws IOException, InterruptedException {
		ConsoleUtil.setTerminalRawInput();

		System.out.print(prompt);

		int histIndex = -1;
		int commandIndex = 0;

		boolean readNext = true;
		boolean byteSequence = false;
		boolean deleted;
		boolean replaced;

		String temp = null;
		ArrayList<Character> resultChars = new ArrayList<>();

		while (readNext) {
			deleted = false;
			replaced = false;

			byte curr = (byte) System.in.read();

			boolean toggling = true;
			while (toggling) {
				toggling = false;
				switch (curr) {
				case TAB:
					if (!tabToggleEnabled) {
						break;
					}
					// grab matching commands based on user input
					String prefix = charListToString(resultChars);
					ArrayList<String> matching = new ArrayList<>();
					for (String reference : commandReferences) {
						if (reference.startsWith(prefix)) {
							matching.add(reference);
						}
					}

					// toggle through matching commands, wait for non-tab key and process result
					while (matching.size() != 0) {
						ArrayList<Character> tempChars = stringToCharList(temp);
						clearLine(tempChars, prompt);

						if (commandIndex >= matching.size()) {
							commandIndex = 0;
						}
						temp = matching.get(commandIndex);
						commandIndex++;

						System.out.print(temp);
						byte next = (byte) System.in.read();

						resultChars.clear();
						if (next != TAB) {
							resultChars = stringToCharList(temp);
							toggling = true;
							curr = next;
							break;
						}
					}
					break;
				case ENTER:
					readNext = false;
					break;
				case DELETE:
				case DELETE_NUMPAD:
					deleted = true;
					break;
				case ESCAPE:
					byteSequence = true;
				}
			}

			if (byteSequence) {
				boolean endOfSequence = true;
				switch (curr) {
				case UP_SEQ:
					if (historyEnabled && histIndex < history.size() - 1) {
						histIndex++;
						resultChars = toggleHistory(histIndex, prompt, resultChars);
						replaced = true;
					}
					break;
				case DOWN_SEQ:
					if (historyEnabled && histIndex > 0) {
						histIndex--;
						resultChars = toggleHistory(histIndex, prompt, resultChars);
						replaced = true;
					}
					break;
				case RIGHT_SEQ:
				case LEFT_SEQ:
					// unused
					break;
				default:
					endOfSequence = false;
				}

				byteSequence = !endOfSequence;
				continue;
			}

			if (!replaced && curr != TAB) {
				if (!deleted) {
					resultChars.add((char) curr);
					System.out.print(Character.toString((char) curr));
				} else if (resultChars.size() > 0) {
					resultChars.remove(resultChars.size() - 1);
					System.out.print("\b \b");
				}
			}
		}

		ConsoleUtil.setTerminalRegularInput();
		return charListToString(resultChars);
	}
}