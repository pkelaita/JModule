package com.jModule.def;

import java.util.ArrayList;

/**
 * Classes extending this class can define the logic for commands based on user
 * input, if given. You can also define the standard parameters for your command
 * logic. Subclasses of this class are taken as a parameter when instantiating a
 * command
 * 
 * @author Pierce Kelaita
 * @version 1.0.0
 *
 */
public abstract class CommandLogic {

	private ArrayList<String> params;

	/**
	 * Overwrite this class to define the logic for a command.
	 * 
	 * @param args
	 * @return result of logic
	 */
	public abstract void runCommand(String[] args);

	public void setParams(ArrayList<String> params) {
		this.params = params;
	}

	public ArrayList<String> getParams() {
		return params;
	}
}
