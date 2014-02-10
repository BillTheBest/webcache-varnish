package com.rbs.cache.webcache;

public class ExpirationRule {
	private final String name;
	private final String seconds;
	
	public ExpirationRule(final String name, final String seconds) {
		this.name = name;
		this.seconds = seconds;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSeconds() {
		return seconds;
	}
}
