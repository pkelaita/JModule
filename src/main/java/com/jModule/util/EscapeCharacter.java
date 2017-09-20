package com.jmodule.util;

/**
 * Defines ASCII bytes corresponding to characters that have special uses in
 * InputUtil
 * 
 * @author Pierce Kelaita
 * @version 1.3.1
 */
final class EscapeCharacter {
	final static byte DELETE_NUMPAD = 8;
	final static byte TAB = 9;
	final static byte ENTER = 10;
	final static byte ESCAPE = 27;
	final static byte UP_SEQ = 65;
	final static byte DOWN_SEQ = 66;
	final static byte RIGHT_SEQ = 67;
	final static byte LEFT_SEQ = 68;
	final static byte DELETE = 127;

	private EscapeCharacter() {
		throw new AssertionError();
	}
}