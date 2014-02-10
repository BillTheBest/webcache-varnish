package com.rbs.cache;

import java.util.HashSet;
import java.util.Set;

import com.rbs.cache.varnish.Director;

public class Site {
	private String name;
	private String port;
	private String url;
	
	private Set<String> aliases;
	
	private Director director = null;
	
	private AccessLog log = new AccessLog();
	
	private String group = null;
	
	private Set<String> urisForHitForPass = new HashSet<String>();
	
	public void putHitForPass(final String uri) {
		urisForHitForPass.add(uri);
	}
	
	public Set<String> getUrisForHitForPass() {
		return urisForHitForPass;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public AccessLog getLog() {
		return log;
	}

	public void setLog(final AccessLog log) {
		this.log = log;
	}

	public Director getDirector() {
		return director;
	}

	public void setDirector(Director director) {
		this.director = director;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<String> getAliases() {
		if(aliases == null) {
			aliases = new HashSet<String>();
		}
		return aliases;
	}

	public void setAliases(Set<String> aliases) {		
		this.aliases = aliases;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Site other = (Site) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
