package com.rbs.cache.webcache;

public class Cookie {
	
	private String name;
	private String cookieName;
	private boolean cacheWithout = true;
	
	public Cookie(final String name, final String cookieName, final boolean cacheWithout) {
		this.name = name;
		this.cookieName = cookieName;
		this.cacheWithout = cacheWithout;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCookieName() {
		return cookieName;
	}
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}
	public boolean isCacheWithout() {
		return cacheWithout;
	}
	public void setCacheWithout(boolean cacheWithout) {
		this.cacheWithout = cacheWithout;
	}
}
