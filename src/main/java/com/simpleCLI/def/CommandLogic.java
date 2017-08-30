package com.simpleCLI.def;

import java.util.ArrayList;

public abstract class CommandLogic {
	
	private ArrayList<String> params;
	
	public abstract void runCommand(String[] args);
	
	public void setParams(ArrayList<String> params) {
		this.params = params;
	}
	
	public ArrayList<String> getParams() {
		return params;
	}
}
