package com.jModule.def;

import java.util.*;

/**
 * Represents a possible command. A command can have multiple references, or
 * ways to call it from the command line. For example, a "find" command that can
 * be accessed by typing 'find' or just 'fd' on the command line. A command can
 * also have any number of defined parameters and command-specific logic to
 * process the given parameters.
 * 
 * A command's logic and possible parameters are defined by writing a logic
 * class extending CommandLogic. For more information about CommandLogic,
 * consult the CommandLogic documentation
 * 
 * @author Pierce Kelaita
 * @version 1.0.0
 *
 */
public class Command {

	private ArrayList<String> references = new ArrayList<>();
	private String[] params;
	private String name;
	private String description;
	private String defaultReference;
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

		List<String> paramList = logic.getParams();
		if (paramList != null) {
			params = new String[paramList.size()];
			for (int i = 0; i < paramList.size(); i++) {
				params[i] = paramList.get(i);
			}
		}
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
		String usage = "Usage: ~$ " + defaultReference;
		if (params != null) {
			for (String param : params) {
				usage += " " + "<" + param + ">";
			}
		}
		if (references != null) {
			for (String reference : references) {
				usage += "\n     - OR " + reference;
				if (params != null) {
					usage += " ~";
				}
			}
		}
		return usage;
	}

	/**
	 * Runs the command logic with the given arguments
	 * 
	 * @param args
	 * @return any data returned by the command logic, if any
	 */
	public Object execute(String[] args) {
		if (args.length == logic.getParams().size()) {
			return logic.runCommand(args);
		} else {
			System.out.println("\n" + getUsage() + "\n");
			return null;
		}
	}
}