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
	public static String promptUserInput(String prompt) throws IOException, InterruptedException {
		System.out.print(prompt);
		boolean readNext = true;
		boolean byteSequence = false;
		boolean deleted;
		ArrayList<Character> resultChars = new ArrayList<>();

		while (readNext) {
			ConsoleUtil.setTerminalRawInput();
			deleted = false;

			byte curr = (byte) System.in.read();
			switch (curr) {
			case '\n': // enter
				readNext = false;
				break;
			case 127: // delete
			case 8: // delete (num pad)
				deleted = true;
				break;
			case 27: // esc
				byteSequence = true;
			}

			if (byteSequence) {
				boolean endOfSequence = true;
				switch (curr) {
				case 65: // ↑
				case 66: // ↓
					// TODO implement logic to navigate history
					break;
				case 67: // →
				case 68: // ← 
					// TODO possibly implement logic to move the cursor left or right
					break;
				default:
					endOfSequence = false;
				}

				byteSequence = !endOfSequence;
				ConsoleUtil.setTerminalRegularInput();
				continue;
			}

			if (!deleted) {
				resultChars.add((char) curr);
				System.out.print(Character.toString((char) curr));
			} else if (resultChars.size() > 0) {
				resultChars.remove(resultChars.size() - 1);
				System.out.print("\b \b");
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