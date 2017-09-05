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

		/*
		 * This loop emulates java GUI key watchers - each keystroke is processed as
		 * either a single byte or a sequence of bytes and is acted on separately.
		 * In order to do this, it is necessary to toggle to raw input mode for each
		 * kestroke.
		 */
		while (readNext) {
			ConsoleUtil.setTerminalRawInput();
			deleted = false;

			// read byte
			byte curr = (byte) System.in.read();
			switch (curr) {
			case '\n': // enter
				readNext = false;
				break;
			case 127: // backspace
			case 8: // delete
				deleted = true;
				break;
			case 27:
				byteSequence = true;
			}

			// check last byte of sequence for multi-byte characters
			if (byteSequence) {
				boolean endOfSequence = true;
				switch (curr) {
				case 65: // up arrow
				case 66: // down arrow
					// TODO implement logic to navigate history
					break;
				case 67: // right arrow (disabled)
				case 68: // left arrow (disabled)
					// TODO possibly implement logic to move the cursor
					break;
				default:
					endOfSequence = false;
				}

				byteSequence = !endOfSequence;
				ConsoleUtil.setTerminalRegularInput();
				continue;
			}

			// add or remove chars from list and print to CLI
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