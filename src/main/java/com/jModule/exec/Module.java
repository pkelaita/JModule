package com.jModule.exec;

import java.util.ArrayList;

import com.jModule.def.Command;

/**
 * Represents a module holding a group of possible commands relating to a
 * certain function
 * 
 * @author Pierce Kelaita
 * @version 1.2.0
 *
 */
public class Module {

	private ArrayList<Command> commands = new ArrayList<>();
	private String name;
	private String helpAppend;
	private String helpReset;

	/**
	 * Sets the name and the display promt that will come up on the CLI
	 * 
	 * @param name
	 */
	public Module(String name) {
		this.name = name.toLowerCase();
	}

	public String getName() {
		return name;
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}

	protected String getHelpAppend() {
		return helpAppend;
	}

	protected String getHelpReset() {
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
		this.helpAppend = this.helpAppend != null ? "\n" + append : append;
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

	/**
	 * Returns a list with the default reference of each command conatined within
	 * the module, plus 'help' and 'exit'
	 * 
	 * @return references
	 */
	public ArrayList<String> getReferences() {
		ArrayList<String> refs = new ArrayList<>();
		for (Command c : commands) {
			refs.add(c.getDefaultReference());
		}
		refs.add("help");
		refs.add("exit");
		return refs;
	}

}
