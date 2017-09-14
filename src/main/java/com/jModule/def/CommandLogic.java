package com.jModule.def;

/**
 * Classes extending this class can define the logic for commands based on user
 * input, if given. You can also define the standard parameters for your command
 * logic. Subclasses of this class are taken as a parameter when instantiating a
 * command
 * 
 * @author Pierce Kelaita
 * @version 1.2.2
 *
 */
public abstract class CommandLogic {

	private String[] params;

	public CommandLogic() {
	}

	public CommandLogic(String[] params) {
		this.params = params;
	}

	/**
	 * Overwrite this class to define the logic for a command.
	 * 
	 * @param args
	 * @return result of logic
	 */
	public abstract void execute(String[] args);

	public String[] getParams() {
		return params;
	}
}
