package com.rbs.cache.varnish;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Backend {
	
	private String name;
	private String host;
	private String port;
	private Probe probe;
	private String weight;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
	public Probe getProbe() {
		return probe;
	}
	public void setProbe(Probe probe) {
		this.probe = probe;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Backend other = (Backend) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter out = new LinuxPrintWriter(sw);

		out.println("backend = {");		
		out.println("\t\t\t.host = \"" + host + "\";");
		out.println("\t\t\t.port = \"" + port + "\";");
		if(probe != null) {
			out.println("\t\t\t.probe = " + probe.getName() + ";");
		}
		out.println("\t\t}");

		try {
			return sw.toString();
		} finally {
			out.close();
		}
	}

}
