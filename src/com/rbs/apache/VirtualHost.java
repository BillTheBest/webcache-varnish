package com.rbs.apache;

import java.util.ArrayList;
import java.util.List;

import com.rbs.cache.RewriteRule;

public class VirtualHost {
	private String host;
	private String port;
	
	private List<RewriteRule> expirationRules = new ArrayList<RewriteRule>();

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public void addExpirationRule(final RewriteRule rewrite) {
		expirationRules.add(rewrite);
	}
	
	public List<RewriteRule> getExpirationRules() {
		return expirationRules;
	}
}
