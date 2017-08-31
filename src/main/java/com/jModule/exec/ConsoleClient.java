package com.jModule.exec;

import java.util.ArrayList;
import java.util.Arrays;

import com.jModule.def.Command;

/**
 * Builds and runs a multi-module command line interface.
 * 
 * Please note: This class takes user input via System.Console() so it cannot
 * run in a typical development environment
 * 
 * @author Pierce Kelaita
 * @version 1.0.0
 *
 */
public class ConsoleClient {

	private Module home;
	private String appname;
	private ArrayList<Module> modules = new ArrayList<>();

	/**
	 * Sets the name of the app and the starting module
	 * 
	 * @param appname
	 *            The name of the console application
	 * @param homeModule
	 *            The default module that is loaded when user first starts up
	 *            application
	 */
	public ConsoleClient(String appname, Module homeModule) {
		this.appname = appname;
		this.home = homeModule;
		modules.add(homeModule);
	}

	/**
	 * Prints a help page specific to a module. Help pages also reference other
	 * modules in the console client.
	 * 
	 * @param m
	 *            the module that the help page is specific to
	 */
	private void printHelpMessage(Module m) {
		
		// if the user has reset the help message, print that message
		if (m.getHelpReset() != null) {
			System.out.println(m.getHelpReset());
			return;
		}
		
		// generate standard help message
		String message = "\n" + m.getName().toUpperCase() + " -- POSSIBLE COMMANDS";
		for (Command c : m.getCommands()) {
			message += "\n'" + c.getDefaultReference() + "'";
			message += "\n\t" + c.getDescription();
			message += "\n\t" + c.getUsage().replaceAll("\n", "\n\t");
		}
		message += "\n'help'";
		message += "\n\t" + "Displays the help page for the current module.";
		message += "\n\tUsage: ~$ help\n";
		if (modules.size() > 1) {
			message += "\nType the name of another module to switch to that module:";
			for (Module other : modules) {
				if (!other.equals(m)) {
					message += "\n\t- " + "'" + other.getName() + "'";
				}
			}
		}
		message += "\n\nType 'exit' at any time to exit the program";
		message += "\n";
		
		// append message
		if (m.getHelpAppend() != null) {
			message += m.getHelpAppend() + "\n";
		}
		System.out.println(message);
	}

	/**
	 * Prints a prompt to the CLI showing the app name and module name and takes in
	 * user input
	 * 
	 * @param m
	 * @return user-given arguments
	 */
	private String[] prompt(Module m) {
		System.out.print(appname + ": " + m.getPrompt());
		String result = System.console().readLine();
		result = result.trim().replaceAll(" +", " ");
		return result.split(" ");
	}

	/**
	 * Finds the command in the module that matches user-given input, and executes
	 * that command with given arguments, if any
	 * 
	 * @param m
	 * @return
	 */
	private Module runModule(Module m) {
		while (true) {

			// prompt for input
			String[] args = prompt(m);
			String cmd = args[0];

			// command-universal operations
			switch (cmd) {
			case "":
				continue;
			case "help":
				printHelpMessage(m);
				continue;
			case "exit":
				System.exit(0);
			}

			// switch to another module
			for (Module switchTo : modules) {
				if (cmd.equals(switchTo.getName()) && !cmd.equals(m.getName())) {
					System.out.println("Switched to module '" + switchTo.getName() + "'\n");
					return switchTo;
				}
			}

			// grab arguments (if given) and find command matching given reference
			args = Arrays.copyOfRange(args, 1, args.length);
			boolean found = false;
			ArrayList<Command> cmds = m.getCommands();

			out: for (Command modCommand : cmds) {
				if (cmd.equals(modCommand.getDefaultReference())) {
					modCommand.execute(args);
					found = true;
					break;
				}
				// if no default references found, go through alternate references
				for (String alt : modCommand.getAltReferences()) {
					if (cmd.equals(alt)) {
						modCommand.execute(args);
						found = true;
						break out;
					}
				}
			}

			if (!found) {
				System.out.println(
						"Command '" + cmd + "' not recognized. Use the 'help' command for details on usage.\n");
			}
		}
	}

	public void addModule(Module m) {
		modules.add(m);
	}

	public void removeModule(Module m) {
		modules.remove(m);
	}

	/**
	 * Runs the console application accross all modules, starting from the home
	 * module
	 */
	public void runConsole() {
		printHelpMessage(home);
		Module m = runModule(home);
		while (true) {
			m = runModule(m);
		}
	}
}