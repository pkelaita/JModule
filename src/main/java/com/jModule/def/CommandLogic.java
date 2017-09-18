package com.jModule.def;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Classes extending this class can define the logic for commands based on user
 * input, if given. You can also define the standard parameters for your command
 * logic. Subclasses of this class are taken as a parameter when instantiating a
 * command
 * 
 * @author Pierce Kelaita
 * @version 1.3.0
 *
 */
public abstract class CommandLogic {

	private String[] params;
	private ArrayList<Option> options = new ArrayList<>();

	public CommandLogic() {
	}

	public CommandLogic(String[] params) {
		this.params = params;
	}

	public ArrayList<Option> getOptions() {
		return options;
	}

	public String[] getParams() {
		return params;
	}

	/**
	 * Override this method to define the logic for a command.
	 * 
	 * @param args
	 *            Command-line arguments
	 */
	public abstract void execute(String[] args);

	/**
	 * Use this method in an if-statement as such: <blockquote>
	 * 
	 * <pre>
	 * <code>
	 * {@literal @}Override
	 * public void execute() {
	 * ...
	 * 	if (onOption('a')) {
	 * 		{@literal //} behavior with option 'a'
	 * 	}
	 * 	if (onOption('b')) {
	 * 		{@literal //} behavior with option 'b'
	 * 	}
	 * }
	 * </code>
	 * </pre>
	 * 
	 * </blockquote> to signify the execution of the command when different options
	 * are called in addition to the command's parameters.
	 * 
	 * 
	 * @param flag
	 *            The option's one-character reference (Example: "-a" or "-b")
	 * @return <code>true</code> if the option exists and is called either by its
	 *         flag or one of its references, <code>false</code> otherwise.
	 */
	public boolean onOption(char flag) {
		for (Option o : getOptions()) {
			if (o.isCalled(flag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a possible option to the command.
	 * 
	 * @param t
	 *            Default flag to call the option
	 * @return This instance of CommandLogic
	 */
	public CommandLogic addOption(Option t) {
		if (options.isEmpty()) {
			options.add(t);
			return this;
		}
		Set<Option> temp = new HashSet<Option>();
		for (Option alt : options) {
			if (t.equals(alt) == null) {
				temp.add(t);
			} else {
				throw new IllegalArgumentException("Duplicate references found: " + t.equals(alt));
			}
		}
		for (Option tog : temp) {
			options.add(tog);
		}
		return this;
	}
}
