package com.jmodule.def;

/**
 * Represents a Command with an indefinite number of parameters. These commands
 * can be called from the command line with any number of parameters, including
 * zero.
 * 
 * @author Pierce Kelaita
 * @version 1.3.1
 */
public class IndefiniteCommand extends Command {

	public IndefiniteCommand(String name, String description, CommandLogic logic) {
		super(name, description, logic);
	}

}