package com.jModule.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Utility class to process user input on *nix terminals. This class is
 * currently untested on Windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.2.2
 *
 */
public class InputUtil {

	private static int position = 1;
	private static boolean historyEnabled = false;
	private static boolean tabCompletionEnabled = false;
	private static String ALERT = "";
	private static ArrayList<String> history = new ArrayList<>();

	public static void setHistoryEnabled(boolean enabled) {
		historyEnabled = enabled;
	}

	public static void setTabCompletionEnabled(boolean enabled) {
		tabCompletionEnabled = enabled;
	}
	
	public static void setAlertsEnabled(boolean enabled) {
		ALERT = enabled ? "\007" : "";
	}

	public static ArrayList<String> getHistory() {
		return history;
	}

	public static void addHistory(String entry) {
		history.add(0, entry);
	}

	private static ArrayList<Character> stringToCharList(String str) {
		ArrayList<Character> result = new ArrayList<>();
		for (int i = 0; str != null && i < str.length(); i++) {
			result.add(str.charAt(i));
		}
		return result;
	}

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
	 * @param cursorDiff
	 *            the distance from the position of the cursor to the end of the
	 *            String printed to the CLI
	 */
	private static void clearLine(ArrayList<Character> resultChars, String prompt, int cursorDiff) {
		// TODO make runtime suck less
		for (int i = 0; i < cursorDiff; i++) {
			resultChars.add(' ');
		}
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
		String histEntry = histIndex == -1 ? charListToString(resultChars) : history.get(histIndex);
		result = stringToCharList(histEntry);

		// print prompt and args at selected index
		clearLine(resultChars, prompt, 0);
		for (char c : result) {
			System.out.print(c);
		}

		return result;
	}

	/**
	 * Moves the cursor right or left based on the key pressed and the characters
	 * already printed to the CLI
	 * 
	 * @param isLeft
	 *            if true, move cursor left
	 * @param resultChars
	 *            characters printed to screen
	 */
	private static void moveCursor(boolean isLeft, ArrayList<Character> resultChars) {
		String out;
		if (isLeft) {
			out = position == 1 ? ALERT : "\b";
			position -= out == ALERT ? 0 : 1;
		} else {
			out = position == resultChars.size() + 1 ? ALERT : resultChars.get(position - 1).toString();
			position += out == ALERT ? 0 : 1;
		}
		System.out.print(out);
	}

	/**
	 * Moves the cursor back without affecting the global variable 'position'
	 * 
	 * @param numChars
	 *            how far to move the cursor back
	 */
	private static void moveCursorBack(int numChars) {
		for (int i = 0; i < numChars; i++) {
			System.out.print("\b");
		}
	}

	/**
	 * Takes in user input and prints result of each character. Currently able to
	 * process all standard chars plus 'enter' and 'backspace' keystrokes.
	 * 
	 * @param commandReferences
	 *            possible references that can be toggled with the 'tab' key
	 * @param prompt
	 *            the first String to come up on the CLI to prompt the user for
	 *            input
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
		ArrayList<Character> resultChars = new ArrayList<>(); // what will get printed and executed
		ArrayList<Character> currentChars = new ArrayList<>(); // used for toggling back to unentered characters

		while (readNext) {
			int numDeleted = 0;
			deleted = false;
			replaced = false;

			byte curr = (byte) System.in.read();

			boolean toggling = true;
			while (toggling) {
				toggling = false;
				switch (curr) {
				case EscapeCharacter.TAB:
					if (!tabCompletionEnabled) {
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
					if (matching.isEmpty()) {
						System.out.print(ALERT);
					}

					// toggle through matching commands, wait for non-tab key and process result
					while (matching.size() != 0) {
						ArrayList<Character> tempChars = stringToCharList(temp);
						clearLine(tempChars, prompt, 0);

						if (commandIndex >= matching.size()) {
							commandIndex = 0;
						}
						temp = matching.get(commandIndex);
						commandIndex++;

						System.out.print(temp);
						byte next = (byte) System.in.read();

						if (next != EscapeCharacter.TAB) {
							resultChars.clear();
							currentChars.clear();
							resultChars = stringToCharList(temp);
							currentChars = stringToCharList(temp);
							position = resultChars.size() + 1;
							toggling = true;
							curr = next;
							break;
						}
					}
					break;
				case EscapeCharacter.ENTER:
					readNext = false;
					break;
				case EscapeCharacter.DELETE_NUMPAD:
				case EscapeCharacter.DELETE:
					numDeleted++;
					deleted = true;
					if (!currentChars.isEmpty()) {
						currentChars.remove(currentChars.size() - 1);
					}
					break;
				case EscapeCharacter.ESCAPE:
					byteSequence = true;
					break;
				default:
					if (!byteSequence) {
						currentChars.add((char) curr);
					}
				}
			}

			if (byteSequence) {
				boolean endOfSequence = true;
				switch (curr) {
				case EscapeCharacter.UP_SEQ:
					if (!historyEnabled) {
						break;
					}
					if (histIndex < history.size() - 1) {
						histIndex++;
						resultChars = toggleHistory(histIndex, prompt, currentChars);
						position = resultChars.size() + 1;
						replaced = true;
						break;
					}
					System.out.print(ALERT);
					break;
				case EscapeCharacter.DOWN_SEQ:
					if (!historyEnabled) {
						break;
					}
					if (historyEnabled && histIndex > -1) {
						histIndex--;
						resultChars = toggleHistory(histIndex, prompt, currentChars);
						position = resultChars.size() + 1;
						replaced = true;
						break;
					}
					System.out.print(ALERT);
					break;
				case EscapeCharacter.RIGHT_SEQ:
					moveCursor(false, resultChars);
					break;
				case EscapeCharacter.LEFT_SEQ:
					moveCursor(true, resultChars);
					break;
				default:
					endOfSequence = false;
				}

				byteSequence = !endOfSequence;
				continue;
			}

			position = readNext ? position : 1;
			boolean insertMode = position - 1 != resultChars.size();

			// process result, set result and print to CLI accordingly. TODO make less ugly
			if (!replaced && curr != EscapeCharacter.TAB && readNext) {

				if (!deleted) {
					resultChars.add(position - 1, (char) curr);
					position = !readNext ? 1 : position + 1;
					System.out.print(Character.toString((char) curr));

					if (insertMode) {
						clearLine(resultChars, prompt, numDeleted);
						System.out.print(charListToString(resultChars));
						moveCursorBack(resultChars.size() + 1 - position);
					}

				} else if (resultChars.size() > 0 && position > 1) {
					resultChars.remove(position - 2);
					position--;
					if (insertMode) {

						ArrayList<Character> buffer = new ArrayList<>();
						for (char c : resultChars) {
							buffer.add(c);
						}
						for (int i = 0; i < resultChars.size() - position + 1; i++) {
							buffer.add(' ');
							System.out.print(" ");
						}

						clearLine(buffer, prompt, resultChars.size() + 1 - position);
						System.out.print(charListToString(resultChars));
						moveCursorBack(resultChars.size() + 1 - position);
						System.out.print(resultChars.get(position - 1) + "\b");

					} else {
						System.out.print("\b \b");
					}
				} else {
					System.out.print(ALERT);
				}
			}
		}

		System.out.println();
		ConsoleUtil.setTerminalRegularInput();
		return charListToString(resultChars);
	}

	private InputUtil() {
		throw new AssertionError();
	}
}