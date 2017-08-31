package com.jModule.exec;

import java.util.ArrayList;
import java.util.Arrays;

import com.jModule.def.Command;

import java.io.Console;

public class ConsoleClient {

	private Module home;
	private String appname;
	private ArrayList<Module> modules = new ArrayList<>();

	public ConsoleClient(String appname, Module homeModule) {
		this.appname = appname;
		this.home = homeModule;
		modules.add(homeModule);
	}

	private String[] prompt(Module m) {
		System.out.print(appname + ": " + m.getPrompt());
		Console c = System.console();
		String result = c.readLine();
		result = result.trim().replaceAll(" +", " ");
		return result.split(" ");
	}

	public void addModule(Module m) {
		modules.add(m);
	}

	public Module runModule(Module m) {
		while (true) {

			String[] args = prompt(m);
			String cmd = args[0];
			switch (cmd) {
			case "":
				continue;
			case "help":
				System.out.println(m.getHelpMessage());
				continue;
			case "exit":
				System.exit(0);
			}

			for (Module switchTo : modules) {
				if (cmd.equals(switchTo.getName()) && !cmd.equals(m.getName())) {
					System.out.println("Switched to module '" + switchTo.getName() + "'\n");
					return switchTo;
				}
			}

			args = Arrays.copyOfRange(args, 1, args.length);
			boolean found = false;
			ArrayList<Command> cmds = m.getCommands();

			for (Command modCommand : cmds) {
				if (cmd.equals(modCommand.getDefaultReference())) {
					modCommand.execute(args);
					found = true;
					break;
				}
				for (String alt : modCommand.getAltReferences()) {
					if (cmd.equals(alt)) {
						modCommand.execute(args);
						found = true;
						break;
					}
				}
			}
			if (!found) {
				System.out.println(
						"Command '" + cmd + "' not recognized. Use the 'help' command for details on usage.\n");
			}
		}
	}
	
	public void runConsole() {
		
		Module m = runModule(home);
		while (true) {
			m = runModule(m);
		}
	}
}