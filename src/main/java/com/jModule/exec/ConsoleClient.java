package com.jmodule.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.jmodule.def.Command;
import com.jmodule.util.ConsoleUtil;
import com.jmodule.util.InputUtil;

/**
 * Builds and runs a multi-module command line interface.
 * 
 * Please note: This class takes user input by toggling the console between raw
 * input and regular input mode. As of version 1.0.2, this client only
 * officially supports *nix terminals. The client has not yet been tested on
 * windows terminals.
 * 
 * @author Pierce Kelaita
 * @version 1.3.1
 *
 */
public class ConsoleClient {

	private Module home;
	private String appname;
	private ArrayList<Module> modules = new ArrayList<>();
	private ArrayList<Thread> shutdownHooks = new ArrayList<>();

	// default values
	private String promptSeparator = "$";
	private String moduleSeparator = ": ";
	private String promptName = null;
	private boolean historyEnabled = false;
	private boolean historyIndexDisplay = false;

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
	 * Sets the character(s) that will come up at the end of the prompt on the CLI
	 * to separate the prompt from the user input. By default, this separator is
	 * "$".
	 * <P>
	 * The following examples show two prompts with the same settings but different
	 * prompt seprators:
	 * <P>
	 *
	 * <code>AppName: module$</code> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- separator is
	 * set to <code>"$"</code> (default setting) <br>
	 * <code>AppName: module></code> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- separator is
	 * set to <code>">"</code> <br>
	 * <code>AppName: module ></code> &nbsp;&nbsp;&nbsp;- separator is set to
	 * <code>" >"</code>
	 * 
	 * @param separator
	 */
	public void setPromptSeparator(String separator) {
		this.promptSeparator = separator;
	}

	/**
	 * Sets the character(s) that will be shown between the app name and the module
	 * in the prompt display. By default, this separator is ": " - a colon with a
	 * space after it. Be advised that this separator will only be displayed if the
	 * client has multiple modules.
	 * <P>
	 * The following examples show two prompts with the same settings but different
	 * module seprators:
	 * <P>
	 * 
	 * <code>AppName: module 0$</code> &nbsp;&nbsp;&nbsp;- separator is set to
	 * <code>": "</code> (default setting) <br>
	 * <code>AppName/ module 0$</code> &nbsp;&nbsp;&nbsp;- separator is set to
	 * <code>"/ "</code> <br>
	 * <code>AppName/module 0$</code> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- separator is
	 * set to <code>"/"</code>
	 * 
	 * @param separator
	 */
	public void setModuleSeparator(String separator) {
		this.moduleSeparator = separator;
	}

	/**
	 * Sets whether the client will log history of user commands. This value is set
	 * to false by default.
	 * 
	 * @param historyEnabled
	 *            if true, client will log command history
	 */
	public void enableHistoryLogging(boolean enabled) {
		this.historyEnabled = enabled;
		InputUtil.setHistoryEnabled(enabled);
	}

	/**
	 * Sets whether the client will display the history index in the prompt. This
	 * can only be enabled if history logging is enabled and is set to false by
	 * default.
	 * 
	 * @param enabled
	 *            if true, client will display history index in prompt
	 */
	public void enableHistoryIndexDisplay(boolean enabled) {
		if (!historyEnabled) {
			throw new IllegalArgumentException(
					"In order to enable history index display, console client must have history logging enabled!");
		} else {
			this.historyIndexDisplay = enabled;
		}
	}

	/**
	 * Sets whether the client will be able to toggle through command history using
	 * the tab key
	 * 
	 * @param historyEnabled
	 *            if true, client will log command history
	 */
	public void enableTabCompletion(boolean enabled) {
		InputUtil.setTabCompletionEnabled(enabled);
	}

	/**
	 * Sets whether the client will trigger the system's default alert sound when
	 * the user performs actions that cannot have a result. (For example, pressing
	 * delete when there are no input characters on the line)
	 * 
	 * @param enabled
	 *            if true, client will trigger system alerts
	 */
	public void enableAlerts(boolean enabled) {
		InputUtil.setAlertsEnabled(enabled);
	}

	/**
	 * Sets the app name that will show on the prompt display. By default, this
	 * value is null, and the prompt display shows the user-given app name with its
	 * spaces removed.
	 * 
	 * @param name
	 */
	public void setPromptDisplayName(String name) {
		this.promptName = name;
	}

	/**
	 * Adds a proccess to run when the app shuts down. Define your proccess by
	 * overriding the method run() in java.lang.Thread.
	 * 
	 * @param onShutdown
	 *            Thread containing process to run on shutdown
	 */
	public void addShutdownHook(Thread onShutdown) {
		shutdownHooks.add(onShutdown);
	}

	/**
	 * Returns a CLI prompt showing the app name and module name and takes in user
	 * input using the input utility class.
	 * 
	 * @param m
	 *            Current module
	 * @return user-given arguments
	 */
	private String getPrompt(Module m) {
		String standPrompt = promptName == null ? appname.replaceAll(" +", "") : promptName;

		if (modules.size() > 1) {
			standPrompt += moduleSeparator + m.getName();
		}

		String sep = promptSeparator;
		if (historyIndexDisplay) {
			sep = " " + InputUtil.getHistory().size() + sep;
		}
		standPrompt += sep + " ";

		return standPrompt;
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
		String message = "\n";
		message += modules.size() == 1 ? appname.toUpperCase() : m.getName().toUpperCase();
		message += " -- POSSIBLE COMMANDS";
		for (Command c : m.getCommands()) {
			message += "\n'" + c.getDefaultReference() + "'";
			message += "\n\t" + c.getDescription();
			message += "\n\t" + c.getUsage().replaceAll("\n", "\n\t");
		}
		String str = modules.size() > 1 ? "current module" : "application";
		message += "\n'help'";
		message += "\n\t" + "Displays the help page for the " + str;
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
	 * Entry point for user input after being parsed by InputUtil. Checks if
	 * commands are chained and executes accordingly.
	 * 
	 * @param m
	 *            Current module
	 * @param args
	 *            All arguments on the current line
	 * @return Output module of final argument
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private Module processUserInput(Module m, String[] args) throws IOException, InterruptedException {

		ArrayList<String[]> inputs = new ArrayList<>();
		ArrayList<String> execList = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			boolean last = i == args.length - 1;

			String arg = args[i];
			if (!arg.endsWith(";") && !last) {
				execList.add(arg);
			} else {
				arg = !last ? arg.substring(0, arg.length() - 1) : arg;
				execList.add(arg);
				String[] exec = new String[execList.size()];
				for (int j = 0; j < execList.size() && execList.get(j) != null; j++) {
					exec[j] = execList.get(j);
				}
				inputs.add(exec);
				execList.clear();
			}
		}

		for (String[] exec : inputs) {
			m = processSingleInput(m, exec);
		}

		return m;
	}

	/**
	 * Processes a single element of a chain of commands with respect to the current
	 * module.
	 * 
	 * @param m
	 *            Current module
	 * @param args
	 *            Arguments in single element of chain
	 * @return Output module
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private Module processSingleInput(Module m, String[] args) throws IOException, InterruptedException {

		String reference = args[0];

		// command-universal operations
		switch (reference) {
		case "":
			return m;
		case "help":
			printHelpMessage(m);
			return m;
		case "exit":
			ConsoleUtil.setTerminalRegularInput();
			System.exit(0);
		}

		for (Module switchTo : modules) {
			if (reference.equals(switchTo.getName()) && !reference.equals(m.getName())) {
				System.out.println("Switched to module '" + switchTo.getName() + "'\n");
				return switchTo;
			}
		}

		args = Arrays.copyOfRange(args, 1, args.length);
		ArrayList<Command> cmds = m.getCommands();

		for (Command cmd : cmds) {
			if (reference.equals(cmd.getDefaultReference()) || cmd.getReferences().contains(reference)) {
				cmd.run(args);
				return m;
			}
		}
		System.out
				.println("Command '" + reference + "' not recognized. Use the 'help' command for details on usage.\n");
		return m;
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

			String prompt = getPrompt(m);
			ArrayList<String> references = m.getReferences();
			for (Module other : modules) {
				if (!other.equals(m)) {
					references.add(other.getName());
				}
			}

			String result = InputUtil.promptUserInput(references, prompt);
			result = result.trim().replaceAll(" +", " ");
			if (historyEnabled && result.length() > 0) {
				InputUtil.addHistory(result);
			}

			String[] args = result.split(" ");
			return processUserInput(m, args);
		}
	}

	/**
	 * Adds a possible module to the client. The user can switch to this module by
	 * typing its name in the CLI
	 * 
	 * @param m
	 *            Module to add
	 */
	public void addModule(Module m) {
		modules.add(m);
	}

	/**
	 * Removes a possible module from the client.
	 * 
	 * @param m
	 *            Module to remove
	 */
	public void removeModule(Module m) {
		modules.remove(m);
	}

	/**
	 * Runs the console application accross all modules, starting from the home
	 * module
	 */
	public void runConsole() {
		for (Thread e : shutdownHooks) {
			Runtime.getRuntime().addShutdownHook(e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					ConsoleUtil.setTerminalRegularInput();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		try {
			Module m = runModule(home);
			while (true) { // main loop
				m = runModule(m);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}