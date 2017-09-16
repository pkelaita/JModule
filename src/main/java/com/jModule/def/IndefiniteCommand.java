package com.jModule.def;

public class IndefCommand extends Command {
	
	private String paramSummary = null;

	public IndefCommand(String name, String description, CommandLogic logic) {
		super(name, description, logic);
	}
	
	public IndefCommand setParamSummary(String summary) {
		this.paramSummary = summary;
		return this;
	}

	@Override
	public String getUsage() {
		String summary = paramSummary == null ? " <Parameters> ..." : paramSummary;
		
		if (getUsageReset() != null) {
			return getUsageReset();
		}

		// generate standard usage info
		String usage = "Usage: ~$ " + getDefaultReference() + summary;
		
		if (getReferences() != null) {
			for (String reference : getReferences()) {
				usage += "\n       OR " + reference + " ~";
			}
		}

		// append info
		if (getUsageAppend() != null) {
			usage += "\n" + getUsageAppend();
		}
		return usage;
	}

}