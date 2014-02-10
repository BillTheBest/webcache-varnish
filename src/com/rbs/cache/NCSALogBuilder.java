package com.rbs.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class NCSALogBuilder {
	
	@SuppressWarnings("unused")
	private final Pattern hostPattern = Pattern.compile("(?:([^\\.]+)\\.)?([a-z\\-A-Z0-9]+)(\\.com(?:\\.br)?)$");
	
	private java.util.regex.Pattern tokenizer = java.util.regex.Pattern.compile("\\.");
	
	private Map<Integer, Set<String>> names = new HashMap<Integer, Set<String>>();
	
	public String getNCSALog(final AccessLog log) {
		StringBuilder stb = new StringBuilder();
		
		for(String host : log.getHosts()) {
			if(stb.length() > 0) {
				stb.append("|");
			}
			stb.append(host);
		}
		
		stb.insert(0, "(");
		stb.append(")\"");
		
		stb.insert(0, " -m \"RxHeader:^Host: ");
		
		stb.insert(0, "/u00/varnish/bin/varnishncsa -a -w /u00/varnish/logs/" + log.getName());
		stb.append(" -F '%h - %u %t \"%m %U%q %H\" %s %b \"%{Referer}i\" \"%{User-Agent}i\" \"%{Cookie}i\"'");
		
		return stb.toString();
	}
	
	public String createNCSALog(final AccessLog log) {
		if(log == null) {
			throw new NullPointerException("log is null");
		}
		
		if(log.getHosts() == null || log.getHosts().isEmpty()) {
			throw new IllegalArgumentException("empty access log");
		}
		
		StringBuilder stb = new StringBuilder();
		stb.append("/u00/varnish/bin/varnishncsa -a -w /u00/varnish/logs/" + log.getName());
		stb.append(" -F '%h - %u %t \"%m %U%q %H\" %s %b \"%{Referer}i\" \"%{User-Agent}i\" \"%{Cookie}i\"'");
		
		stb.append(" -m \"RxHeader:^Host: ");
		
		String[] tokens = null;
		int length = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		for(String host : log.getHosts()) {
			tokens = tokenizer.split(host);
			length = tokens.length;
			
			for(int x=length - 1; x>=0; x--) {
				int key = length - 1 - x;
				Set<String> used = names.get(key);
				if(used == null) {
					used = new HashSet<String>();
					names.put(key, used);
				}
				used.add(tokens[x]);
				min = Math.min(key, min);
				max = Math.max(key, max);
			}
		}
				
		for(int z = max; z>=min; z--) {
			Set<String> values = names.get(z);
			
			stb.append("(");
			for(String s : values) {
				stb.append(s);
				stb.append("|");
			}
			stb.deleteCharAt(stb.length() - 1);			
			
			stb.append(")").append("\\.");
		}
		
		stb.delete(stb.length() - 2, stb.length()).append("\"");	
		
		return stb.toString();
	}
	
}
