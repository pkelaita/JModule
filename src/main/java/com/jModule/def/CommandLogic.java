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
	 * Overwrite this class to define the logic for a command. If you want, the
	 * command can return data to the class it's called in. If not, make sure to
	 * add a null return statement.
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
