package com.simpleCLI.def;

import java.util.*;

public class Command {

	private Set<String> possibleInput;
	private String[] params;
	private String name;
	private CommandLogic logic;

	public Command(String name, CommandLogic logic) {
		this.name = name;
		this.logic = logic;
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

	public void addPossibleInput(String input) {
		possibleInput.add(input);
	}

	public void execute(String[] args) {
		logic.runCommand(args);
	}
}