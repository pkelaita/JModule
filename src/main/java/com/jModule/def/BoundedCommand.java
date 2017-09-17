package com.jModule.def;

/**
 * Represents a command with a certain minimum and maximum number of parameters.
 * This subclass of IndefCommand can either be instantiated with (name,
 * description, logic, min) to leave the maximum bound unspecified or (name,
 * description, logic, min, max) to define both bounds of the range.
 * 
 * @author Pierce Kelaita
 * @version 1.3.0
 */
public class BoundedCommand extends IndefiniteCommand {

	private int min = -1;
	private int max = -1;

	/**
	 * Construcs a bounded command with a minimum number of parameters
	 * 
	 * @param name
	 *            Command name
	 * @param description
	 *            Command description
	 * @param logic
	 *            Command logic
	 * @param min
	 *            Minimum number of parameters
	 */
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

	/**
	 * Construcs a bounded command with a minimum and maximum number of parameters
	 * 
	 * @param name
	 *            Command name
	 * @param description
	 *            Command description
	 * @param logic
	 *            Command logic
	 * @param min
	 *            Minimum number of parameters
	 * @param max
	 *            Maximum number of paramters
	 */
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

	protected int getMin() {
		return min;
	}

	protected int getMax() {
		return max;
	}
}
