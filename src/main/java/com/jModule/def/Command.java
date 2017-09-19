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
 * @version 1.3.0
 *
 */
public class Command {

	private ArrayList<String> references = new ArrayList<>();
	private ArrayList<Option> options = new ArrayList<>();
	private String[] params;
	private String name;
	private String description;
	private String defaultReference;
	private String usageAppend;
	private String usageReset;
	private CommandLogic logic;

	private int min = -1;
	private int max = -1;

	protected void setMin(int min) {
		this.min = min;
	}

	protected void setMax(int max) {
		this.max = max;
	}

	/**
	 * Sets the name of the command function (for example, "find" or "grep") and
	 * inherits parameters from command logic, if any. Sets the default reference
	 * based on the command's name
	 * 
	 * @param name
	 *            Command name
	 * @param description
	 *            Command description
	 * @param logic
	 *            Command logic
	 */
	public Command(String name, String description, CommandLogic logic) {
		this.name = name;
		this.description = description;
		this.logic = logic;
		this.defaultReference = name.toLowerCase().replaceAll(" ", "");
		this.params = logic.getParams();
		this.options = logic.getOptions();
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

	public ArrayList<String> getReferences() {
		return references;
	}

	protected String getUsageReset() {
		return usageReset;
	}

	protected String getUsageAppend() {
		return usageAppend;
	}

	private boolean activateExistingOption(String reference) {
		for (Option o : options) {
			if (o.isReferencedBy(reference)) {
				o.activate();
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a possible reference to the command
	 * 
	 * @param reference
	 */
	public void addReference(String reference) {
		references.add(reference);
	}

	/**
	 * Gives the correct usage in a standard, user-friendly format
	 * 
	 * @return usage statment
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
		for (String reference : references) {
			usage += "\n       OR " + reference;
			if (params != null) {
				usage += " ~";
			}
		}
		if (!options.isEmpty()) {
			usage += "\nOptions:";
			for (Option t : options) {
				usage += "\n\t" + t.getFlag();
				for (String reference : t.getReferences()) {
					usage += ", " + reference;
				}
				usage += ": " + t.getDescription();
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
	 *            Command-line arguments
	 */
	public void run(String[] args) {
		ArrayList<String> paramsPassedList = new ArrayList<>();
		boolean illegalOptions = false;
		for (String arg : args) {
			if (!arg.equals(";") && arg.length() > 0) { // weird bug fix
				if (!arg.startsWith("-")) {
					paramsPassedList.add(arg);
				} else {
					if (arg.charAt(1) != '-') {
						for (int i = 1; i < arg.length(); i++) {
							if (!activateExistingOption("-" + arg.charAt(i))) {
								illegalOptions = true;
							}
						}
					} else if (!activateExistingOption(arg)) {
						illegalOptions = true;
					}
				}
			}
		}

		String[] paramsPassed = new String[paramsPassedList.size()];
		for (int i = 0; i < paramsPassed.length; i++) {
			paramsPassed[i] = paramsPassedList.get(i);
		}

		int paramNum = params != null ? params.length : 0;
		boolean illegalDefNum = paramNum != paramsPassedList.size() && !(this instanceof IndefiniteCommand);
		boolean illegalBoundNum = (paramsPassedList.size() < min || paramsPassedList.size() > max)
				&& this instanceof BoundedCommand;

		if (illegalDefNum || illegalBoundNum || illegalOptions) {
			System.out.println(getUsage() + "\n");
		} else {
			logic.execute(paramsPassed);
		}
		for (Option o : options) {
			o.reset();
		}
	}
}