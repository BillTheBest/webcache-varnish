package com.rbs.cache;

public class RewriteRule {
	private String pattern;
	private String cacheTime;
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(String cacheTime) {
		this.cacheTime = cacheTime;
	}

	public RewriteRule(final String pattern, final String cacheTime) {
		this.pattern = pattern;
		this.cacheTime = cacheTime;
	}
	
	public RewriteRule(final String pattern) {
		this.pattern = pattern;
	}
}
