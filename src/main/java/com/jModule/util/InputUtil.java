package com.jModule.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Utility class to process user input on *nix terminals. This class is
 * currently untested on Windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.1.0
 *
 */
public class InputUtil {

	private final static byte DELETE_NUMPAD = 8;
	private final static byte ENTER = 10;
	private final static byte ESCAPE = 27;
	private final static byte UP_SEQ = 65;
	private final static byte DOWN_SEQ = 66;
	private final static byte RIGHT_SEQ = 67;
	private final static byte LEFT_SEQ = 68;
	private final static byte DELETE = 127;

	private static ArrayList<String> history = new ArrayList<>();

	public static ArrayList<String> getHistory() {
		return history;
	}

	public static void addHistory(String entry) {
		history.add(0, entry);
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
	private static ArrayList<Character> toggleHistory(int histIndex, String prompt) throws IOException {
		ArrayList<Character> result = new ArrayList<Character>();

		// get args at history index
		String histEntry = history.get(histIndex);
		for (int i = 0; i < histEntry.length(); i++) {
			result.add(histEntry.charAt(i));
		}

		// print prompt and args at selected index
		for (int i = 0; i < 1000; i++) {
			System.out.print("\b \b");
		}
		System.out.print(prompt);
		for (char c : result) {
			System.out.print(c);
		}

		return result;
	}

	/**
	 * Takes in user input and prints result of each character. Currently able to
	 * process all standard chars plus 'enter' and 'backspace' keystrokes.
	 * 
	 * TODO process arrow keys, fix bug with arrow keys affecting CLI
	 * 
	 * @return result of user input
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String promptUserInput(String prompt, boolean historyEnabled)
			throws IOException, InterruptedException {

		System.out.print(prompt);

		int histIndex = -1;
		boolean readNext = true;
		boolean byteSequence = false;
		boolean deleted;
		boolean replaced;
		ArrayList<Character> resultChars = new ArrayList<>();

		while (readNext) {
			ConsoleUtil.setTerminalRawInput();
			deleted = false;
			replaced = false;

			byte curr = (byte) System.in.read();
			switch (curr) {
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

			if (byteSequence) {
				boolean endOfSequence = true;
				switch (curr) {
				case UP_SEQ:
					if (historyEnabled && histIndex < history.size() - 1) {
						histIndex++;
						resultChars = toggleHistory(histIndex, prompt);
						replaced = true;
					}
					break;
				case DOWN_SEQ:
					if (historyEnabled && histIndex > 0) {
						histIndex--;
						resultChars = toggleHistory(histIndex, prompt);
						replaced = true;
					}
					break;
				case RIGHT_SEQ:
				case LEFT_SEQ:
					// TODO
					break;
				default:
					endOfSequence = false;
				}

				byteSequence = !endOfSequence;
				ConsoleUtil.setTerminalRegularInput();
				continue;
			}

			if (!replaced) {
				if (!deleted) {
					resultChars.add((char) curr);
					System.out.print(Character.toString((char) curr));
				} else if (resultChars.size() > 0) {
					resultChars.remove(resultChars.size() - 1);
					System.out.print("\b \b");
				}
			}

			ConsoleUtil.setTerminalRegularInput();
		}

		String result = "";
		for (char c : resultChars) {
			result += c;
		}

		return result;
	}
}