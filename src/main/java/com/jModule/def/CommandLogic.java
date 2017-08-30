package com.jModule.def;

import java.util.ArrayList;

/**
 * In order to impliment logic into a command, you must write a class extending
 * CommandLogic specific to the given command. Inside this class, you will
 * define the command's logic. After instantiating the specific CommandLogic
 * subclass, you can add and access its parameters.
 * 
 * @author piercekelaita
 *
 */
public abstract class CommandLogic {

	private ArrayList<String> params;

	/**
	 * This class must be overwritten to define logic for a given command based on
	 * its arguments. This method can return an object based on the command logic.
	 * If you do not wish to have any returns, include a null return statement.
	 * 
	 * @param args
	 * @return result of logic
	 */
	public abstract Object runCommand(String[] args);

	public void setParams(ArrayList<String> params) {
		this.params = params;
	}

	public ArrayList<String> getParams() {
		return params;
	}
}
