package com.rbs.cache.varnish;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Director extends AbstractVCLObject {
	
	private String retries = "5";
	private List<Backend> backends;
	private Strategy strategy = Strategy.ROUND_ROBIN;
	
	public Strategy getStrategy() {
		return strategy;
	}
	public void setStrategy(Strategy strategy) {
		if(strategy == null) {
			throw new NullPointerException();
		}
		this.strategy = strategy;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if(name == null) {
			throw new NullPointerException();
		}
		if(name.length() > 41) {
			name = name.substring(0, 40);
		}
		this.name = name;
	}
	
	public String getRetries() {
		return retries;
	}
	public void setRetries(String retries) {
		this.retries = retries;
	}
	
	public List<Backend> getBackends() {
		return backends;
	}
	public void setBackends(List<Backend> backends) {
		this.backends = backends;
	}
	
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter out = new LinuxPrintWriter(sw);

		sw.append("director ").append(name).append(" ").append(strategy.toString()).append(" {");
		out.println();
		
		for(Backend backend : backends) {
			out.println("\t{");
			out.println("\t\t." + backend);
			out.println("\t}");
		}

		out.println("}");
		
		try {
			return sw.toString();
		} finally {
			out.close();
		}
	}
}
