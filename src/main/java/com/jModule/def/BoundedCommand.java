package com.jModule.def;

public class BoundedCommand extends IndefCommand {
	
	private int min = -1;
	private int max = -1;

	public BoundedCommand(String name, String description, CommandLogic logic, int min) {
		super(name, description, logic);
		if (min < 0) {
			throw new IllegalArgumentException("[" + min + "," + '\u221e' + ") is not a valid parameter range!");
		}
		super.setMin(min);
		super.setMax(Integer.MAX_VALUE);
		this.min = min;
		this.max = Integer.MAX_VALUE;
	}

	public BoundedCommand(String name, String description, CommandLogic logic, int min, int max) {
		super(name, description, logic);
		if (min < 0 || min >= max) {
			throw new IllegalArgumentException("[" + min + "," + max + "] is not a valid parameter range!");
		}
		super.setMin(min);
		super.setMax(max);
		this.min = min;
		this.max = max;
	}
	
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
