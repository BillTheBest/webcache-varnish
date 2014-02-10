package com.rbs.cache.webcache;

import java.util.HashMap;
import java.util.Map;

public class Calypso {
	
	private int invalidationPort = 4001;
	private int httpPort = 80;
	
	private Map<String, ExpirationRule> expirationsRule = new HashMap<String, ExpirationRule>();
	private Map<String, Cookie> cookies = new HashMap<String, Cookie>();
	
	public int getInvalidationPort() {
		return invalidationPort;
	}
	public void setInvalidationPort(final int invalidationPort) {
		this.invalidationPort = invalidationPort;
	}
	
	public int getHttpPort() {
		return httpPort;
	}
	public void setHttpPort(final int httpPort) {
		this.httpPort = httpPort;
	}
	
	public void addExpirationRule(final String name, final String amount) {
		expirationsRule.put(name, new ExpirationRule(name, amount));
	}
	
	public ExpirationRule getExpirationRule(final String name) {
		return expirationsRule.get(name);
	}
	
	public void addCookie(final String name, final String cookieName, final String cacheWithout) {
		cookies.put(name, new Cookie(name, cookieName, "YES".equals(cacheWithout)));
	}
}
