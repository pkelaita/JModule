package com.jModule.exec;

import java.util.ArrayList;

import com.jModule.def.Command;

/**
 * Represents a module holding a group of possible commands relating to a
 * certain function
 * 
 * @author Pierce Kelaita
 * @version 1.0.2
 *
 */
public class Module {

	private ArrayList<Command> commands = new ArrayList<>();
	private String name;
	private String prompt;
	private String helpAppend;
	private String helpReset;

	/**
	 * Sets the name and the display promt that will come up on the CLI
	 * 
	 * @param name
	 */
	public Module(String name) {
		this.name = name;
		this.prompt = name.toLowerCase() + " ";
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

	public String getHelpAppend() {
		return helpAppend;
	}

	public String getHelpReset() {
		return helpReset;
	}

	/**
	 * Adds to a list of commands that are only accessible when the user has
	 * switched to this module.
	 * 
	 * @param c
	 */
	public void addCommand(Command c) {
		commands.add(c);
	}

	/**
	 * Adds additional information to the module's help page
	 * 
	 * @param append
	 *            Information to append to help page
	 */
	public void appendHelpPage(String append) {
		if (this.helpAppend == null) {
			this.helpAppend = append;
		} else {
			this.helpAppend += "\n" + append;
		}
		if (this.helpReset != null) {
			this.helpReset += "\n" + append;
		}
	}

	/**
	 * Replaces the module's help page with a given String
	 * 
	 * @param reset
	 *            New help page to display
	 */
	public void resetHelpPage(String reset) {
		this.helpReset = reset;
		this.helpAppend = null;
	}

}
