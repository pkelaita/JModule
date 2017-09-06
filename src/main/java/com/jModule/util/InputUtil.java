package com.jModule.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Utility class to process user input on *nix terminals. This class is
 * currently untested on Windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.0.3
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

	private static ArrayList<String[]> history = new ArrayList<>();

	public static ArrayList<String[]> getHistory() {
		return history;
	}

	public static void addHistory(String[] entry) {
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
	 */
	private static ArrayList<Character> toggleHistory(int histIndex, int numChars, String prompt) {
		ArrayList<Character> result = new ArrayList<Character>();

		// get args at history index
		String[] histEntry = history.get(histIndex);
		for (String cmd : histEntry) {
			for (int i = 0; i < cmd.length(); i++) {
				result.add(cmd.charAt(i));
				numChars++;
			}
			result.add(' ');
			numChars++;
		}
		result.remove(result.size() - 1);

		// count and delete chars on line
		if (histIndex > 0 && history.size() > 1) {
			String last = "";
			for (String cmd : history.get(histIndex - 1)) {
				last += cmd + " ";
			}
			last = last.substring(0, last.length() - 1);
			numChars += last.length();
		}
		for (int i = 0; i < numChars; i++) {
			System.out.print("\b \b");
		}

		// print prompt and args at selected index
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
		
		int numChars = prompt.length();
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
						resultChars = toggleHistory(histIndex, numChars, prompt);
						replaced = true;
					}
					break;
				case DOWN_SEQ:
					if (historyEnabled && histIndex > 0) {
						histIndex--;
						resultChars = toggleHistory(histIndex, numChars, prompt);
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
					numChars++;
					System.out.print(Character.toString((char) curr));
				} else if (resultChars.size() > 0) {
					resultChars.remove(resultChars.size() - 1);
					numChars--;
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