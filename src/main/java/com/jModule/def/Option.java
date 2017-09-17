package com.jModule.def;

import java.util.ArrayList;

/**
 * Represents an optional toggle for a command.
 * 
 * @author Pierce Kelaita
 * @version 1.3.0
 */
public class Option {

	private String flag;
	private String description;
	private boolean active;
	private ArrayList<String> refs = new ArrayList<>();

	public Option(char flag, String description) {
		this.flag = "-" + flag;
		this.description = description;
		active = false;
	}

	public Option addReference(String reference) {
		refs.add("--" + reference);
		return this;
	}

	public ArrayList<String> getReferences() {
		return refs;
	}

	public String getFlag() {
		return flag;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isCalled(char flag) {
		return flag == this.flag.charAt(1) && active;
	}
	
	public boolean isReferencedBy(String arg) {
		if (arg.equals(flag)) {
			return true;
		}
		if (refs.contains(arg)) {
			return true;
		}
		return false;
	}
	
	public void activate() {
		active = true;
	}
	
	public void reset() {
		active = false;
	}

	public String equals(Option t) {
		if (t.getFlag().equals(flag)) {
			return flag;
		}
		for (String ref : t.getReferences()) {
			if (this.refs.contains(ref)) {
				return ref;
			}
		}
		return null;
	}

}
