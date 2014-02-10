package com.rbs.cache;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

public class AccessLog {
	
	private final Set<String> hosts = new TreeSet<String>();
	private String name = null;
	private final  java.util.regex.Pattern isIpHost;
	
	public AccessLog() {
		isIpHost = java.util.regex.Pattern.compile("[0-9]{2,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getHosts() {
		return Collections.unmodifiableSet(hosts);
	}
	
	public void putHost(final String host) {
		if(host.indexOf('.') < 0) {
			return;
		}
		Matcher m = isIpHost.matcher(host);
		if(m.matches()) {
			return;
		}
		if(host.indexOf(':') > 0) {
			this.hosts.add(host.substring(0, host.indexOf(':')));
		} else {
			this.hosts.add(host);
		}
	}
}
