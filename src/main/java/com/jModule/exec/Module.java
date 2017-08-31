package com.jModule.exec;

import java.util.ArrayList;

import com.jModule.def.Command;

/**
 * Represents a module holding a group of possible commands relating to a
 * certain function
 * 
 * @author Pierce Kelaita
 * @version 1.0.0
 *
 */
public class Module {

	private ArrayList<Command> commands = new ArrayList<>();
	private String name;
	private String prompt;

	/**
	 * Sets the name and the display promt that will come up on the CLI
	 * 
	 * @param name
	 */
	public Module(String name) {
		this.name = name;
		this.prompt = name.toLowerCase() + " $ ";
	}
	
	public String getName() {
		return name;
	}

	public String getPrompt() {
		return prompt;
	}
	
	public ArrayList<Command> getCommands() {
		return commands;
	}

	/**
	 * Returns a help page with descriptions and usages specific to each command
	 * 
	 * @return
	 */
	public String getHelpMessage() {
		String message = "\n" + name.toUpperCase() + " -- POSSIBLE COMMANDS";
		for (Command c : commands) {
			message += "\n'" + c.getDefaultReference() + "'";
			message += "\n\t" + c.getDescription();
			message += "\n\t" + c.getUsage();
		}
		message += "\n'help'";
		message += "\n\t" + "Displays the help page for the current module.";
		message += "\n\tUsage: ~$ help\n";
		return message;
	}

	public void addCommand(Command c) {
		commands.add(c);
	}

}
