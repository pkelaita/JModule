package com.jModule.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jModule.def.Command;
import com.jModule.util.ConsoleUtil;
import com.jModule.util.InputUtil;

/**
 * Builds and runs a multi-module command line interface.
 * 
 * Please note: This class takes user input by toggling the console between raw
 * input and regular input mode. As of version 1.0.2, this client only
 * officially supports *nix terminals. The client has not yet been tested on
 * windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.0.3
 *
 */
public class ConsoleClient {

	private Module home;
	private String appname;
	private ArrayList<Module> modules = new ArrayList<>();
	private boolean historyEnabled = false;

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
	 * Sets whether the client will log history of user commands.
	 * 
	 * @param enable
	 *            if true, client will log command history
	 */
	public void setHistoryLoggingEnabled(boolean enable) {
		this.historyEnabled = enable;
	}

	/**
	 * Prints a help page specific to a module. Help pages also reference other
	 * modules in the console client.
	 * 
	 * @param m
	 *            the module that the help page is specific to
	 */
	private void printHelpMessage(Module m) {

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
	 * user input using the input utility class.
	 * 
	 * @param m
	 *            Current module
	 * @return user-given arguments
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String[] prompt(Module m) throws IOException, InterruptedException {

		String standPrompt = appname + ": " + m.getPrompt();
		standPrompt += !historyEnabled ? "$ " : InputUtil.getHistory().size() + "$ ";

		String result = InputUtil.promptUserInput(standPrompt, historyEnabled);
		result = result.trim().replaceAll(" +", " ");

		return result.split(" ");
	}

	/**
	 * Finds the command in the module that matches user-given input, and executes
	 * that command with given arguments, if any
	 * 
	 * @param m
	 *            Current module
	 * @return Either current module or module user switches to
	 * @throws InterruptedException
	 * @throws IOExceoption
	 */
	private Module runModule(Module m) throws InterruptedException, IOException {
		while (true) {

			String[] args = null;
			args = prompt(m);
			String reference = args[0];

			// command-universal operations
			switch (reference) {
			case "":
				continue;
			case "help":
				printHelpMessage(m);
				continue;
			case "exit":
				ConsoleUtil.setTerminalRegularInput();
				System.exit(0);
			}
			InputUtil.addHistory(args);

			for (Module switchTo : modules) {
				if (reference.equals(switchTo.getName()) && !reference.equals(m.getName())) {
					System.out.println("Switched to module '" + switchTo.getName() + "'\n");
					return switchTo;
				}
			}

			args = Arrays.copyOfRange(args, 1, args.length);
			ArrayList<Command> cmds = m.getCommands();

			for (Command cmd : cmds) {
				if (reference.equals(cmd.getDefaultReference()) || cmd.getAltReferences().contains(reference)) {
					cmd.execute(args);
					return m;
				}
			}
			System.out.println(
					"Command '" + reference + "' not recognized. Use the 'help' command for details on usage.\n");
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
		try {
			printHelpMessage(home);
			Module m = runModule(home);
			while (true) {
				m = runModule(m);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}