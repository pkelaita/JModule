package com.jmodule.def;

import java.util.ArrayList;

/**
 * Represents an option that affects a command's behavior. These are, by nature,
 * optional and do not count as a parameter of a command. Each option is called
 * by a unique single-char flag, signified by a dash (for example, {@code -v}).
 * 
 * 
 * @author Pierce Kelaita
 * @version 1.3.1
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

	public ArrayList<String> getReferences() {
		return refs;
	}

	public String getFlag() {
		return flag;
	}

	public String getDescription() {
		return description;
	}

	protected boolean isCalled(char flag) {
		return flag == this.flag.charAt(1) && active;
	}

	protected boolean isReferencedBy(String arg) {
		if (arg.equals(flag)) {
			return true;
		}
		if (refs.contains(arg)) {
			return true;
		}
		return false;
	}

	protected void activate() {
		active = true;
	}

	protected void reset() {
		active = false;
	}

	protected String equals(Option t) {
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

	/**
	 * Adds a possible reference to the option. References are signaled by a double
	 * dash and can be multiple characters (for example, {@code --verbose}).
	 * 
	 * @param reference
	 *            String to reference option
	 * @return Instance of this option with added reference
	 */
	public Option addReference(String reference) {
		refs.add("--" + reference);
		return this;
	}

}
