package com.jModule.exec;

import java.util.ArrayList;

import com.jModule.def.Command;;

public class Module {

	private ArrayList<Command> commands = new ArrayList<>();
	private String name;
	private String prompt;
	
	public Module (String name) {
		this.name = name;
		this.prompt = name.toLowerCase() + " $ ";
	}
	
	public String getPrompt() {
		return prompt;
	}
	
	public String getHelpMessage() {
		String message = name.toUpperCase() + " -- POSSIBLE COMMANDS";
		for (Command c : commands) {
			message += "\n'" + c.getName() + "'";
			message += "\n\t" + c.getDescription();
			message += "\n\t" + c.getUsage();
		}
		return message;
	}
	
	public void addCommand(Command c) {
		commands.add(c);
	}

}
