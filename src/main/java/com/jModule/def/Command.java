package com.jModule.def;

import java.util.ArrayList;

/**
 * Represents a possible command. A command can have multiple references, or
 * ways to call it from the command line. For example, a "find" command that can
 * be accessed by typing 'find' or just 'fd' on the command line would have 'fd'
 * as an alternate reference. A command can also have any number of defined
 * parameters and command-specific logic to process the given parameters.
 * <P>
 * A command's logic and possible parameters are defined by writing a logic
 * class extending CommandLogic. For more information about CommandLogic,
 * consult the CommandLogic documentation
 * 
 * @author Pierce Kelaita
 * @version 1.2.2
 *
 */
public class Command {

	private ArrayList<String> references = new ArrayList<>();
	private String[] params;
	private String name;
	private String description;
	private String defaultReference;
	private String usageAppend;
	private String usageReset;
	private CommandLogic logic;

	/**
	 * Sets the name of the command function (for example, "find" or "grep") and
	 * inherits parameters from command logic, if any. Sets the default reference
	 * based on the command's name
	 * 
	 * @param name
	 * @param logic
	 */
	public Command(String name, String description, CommandLogic logic) {
		this.name = name;
		this.description = description;
		this.logic = logic;
		this.defaultReference = name.toLowerCase().replaceAll(" ", "");
		this.params = logic.getParams();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultReference() {
		return defaultReference;
	}

	public ArrayList<String> getAltReferences() {
		return references;
	}

	public void addReference(String reference) {
		references.add(reference);
	}

	/**
	 * Gives the correct usage in a standard, user-friendly format
	 * 
	 * @return
	 */
	public String getUsage() {
		if (usageReset != null) {
			return usageReset;
		}

		// generate standard usage info
		String usage = "Usage: ~$ " + defaultReference;
		if (params != null) {
			for (String param : params) {
				usage += " " + "<" + param + ">";
			}
		}
		if (references != null) {
			for (String reference : references) {
				usage += "\n       OR " + reference;
				if (params != null) {
					usage += " ~";
				}
			}
		}

		// append info
		if (usageAppend != null) {
			usage += "\n" + usageAppend;
		}
		return usage;
	}

	/**
	 * Adds additional information to the command's usage info
	 * 
	 * @param append
	 *            Information to append to usage
	 */
	public void appendUsage(String append) {
		this.usageAppend = this.usageAppend != null ? "\n" + append : append;
		if (this.usageReset != null) {
			this.usageReset += "\n" + append;
		}
	}

	/**
	 * Replaces the command's usage info with a given String
	 * 
	 * @param reset
	 *            New usage info to display
	 */
	public void resetUsage(String reset) {
		this.usageReset = reset;
		this.usageAppend = null;
	}

	/**
	 * Runs the command logic with the given arguments
	 * 
	 * @param args
	 */
	public void execute(String[] args) {
		int paramNum = params != null ? params.length : 0;
		if (paramNum == args.length) {
			logic.runCommand(args);
			return;
		}
		System.out.println(getUsage() + "\n");
	}
}