package com.rbs.cache;

public enum HttpMethod {
	
	GET(true), HEAD(true), POST(false), PUT(false), DELETE(false), TRACE(false), OPTIONS(false), CONNECT(false);
	
	private boolean cacheable = true;
	
	public boolean isCacheable() {
		return cacheable;
	}

	private HttpMethod(final boolean cacheable) {
		this.cacheable = cacheable;
	}
}
