package com.jModule.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Utility class to process user input on *nix terminals. This class is
 * currently untested on Windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.0.2
 *
 */
public class InputUtil {

	/**
	 * Takes in user input and prints result. For each input character, the terminal
	 * toggles to raw input mode and back to regular mode.
	 * 
	 * @return result of user input
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String readUserInput() throws IOException, InterruptedException {
		boolean readNext = true;
		boolean deleted;
		ArrayList<Character> resultChars = new ArrayList<>();
		String result = "";

		ConsoleUtil.setTerminalRawInput(); // raw input for terminal

		while (readNext) {
			deleted = false;
			result = "";
			char curr = (char) System.in.read();

			switch (curr) {
			case '\n': // enter
				readNext = false;
				break;
			case 127: // backsapce
			case 8: // delete
				deleted = true;
			}

			// add or remove chars from the arraylist
			if (!deleted) {
				resultChars.add(curr);
				System.out.print(Character.toString(curr));
			} else if (resultChars.size() > 0) {
				resultChars.remove(resultChars.size() - 1);
				System.out.print("\b \b");
			}
		}

		for (char c : resultChars) {
			result += c;
		}
		result = result.trim().replaceAll(" +", " ");
		ConsoleUtil.setTerminalRegularInput(); // regular input for terminal

		return result;
	}

}
