package com.rbs.cache;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.rbs.cache.varnish.LinuxPrintWriter;

public class Pattern {
	
	private final Set<String> sites;
	
	public Pattern(final Set<String> sites) {
		this.sites = sites;
	}
	public String compile() {
		Map<String, Set<String>> sameHost = new TreeMap<String, Set<String>>();
		
		for(String site : sites) {
			int i = site.indexOf('/');
			String h = null;
			String u = null;
			if(i > 0) {
				h = site.substring(0, i);
				u = site.substring(i);
			} else {
				h = site;
			}
			
			Set<String> urls = sameHost.get(h);
			if(urls == null) {
				urls = new HashSet<String>();
				sameHost.put(h, urls);
			}
			
			if(u != null) {
				urls.add(u);//u == null ? site : u);
			}
		}
		
		StringWriter sw = new StringWriter();
		PrintWriter out = new LinuxPrintWriter(sw);
		
		int t = sameHost.size();
		int r = 0;
		for(Map.Entry<String, Set<String>> entry : sameHost.entrySet()) {
			if(r > 0) {
				out.print("\t\t");
			}
			if(entry.getValue().size() == 0) {
				out.print("(req.http.Host == \"" + entry.getKey() + "\")");				
			} else {
				int s = entry.getValue().size();
				int x = 0;
				
				out.print(" (req.http.Host == \"" + entry.getKey() + "\" && req.url ~ \"^(");
				for(String u : entry.getValue()) {
					out.print(u);
					if(x < s - 1) {
						out.print("|");
					}
					x++;
				}
				out.print(")\")");
				
			}
			if(r < t - 1) {
				out.print(" || ");
			}
			
			if(r < t - 1) {
				out.println();
			}
			r++;
		}
		
		try {
			return sw.toString();
		} finally {
			out.close();
		}
	}
}
