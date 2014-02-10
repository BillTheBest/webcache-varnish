package com.rbs.cache.webcache;

public abstract class Log {
	
	protected final String name;
	private int rollover = 3600; //each hour
	
	public Log(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int getRollover() {
		return rollover;
	}
	public void setRollover(final int rollover) {
		this.rollover = rollover;
	}
}
