package com.rbs.cache.varnish;

import java.util.regex.Pattern;

public class Name {
	private static final Pattern underscore = Pattern.compile("[\\.\\-/]");
	
	public static final String normalize(final String host, final String port, final String uri) {
		StringBuilder stb = new StringBuilder();
		
		stb.append(underscore.matcher(host).replaceAll("_"));
		stb.append('_');
		
		if(port == null || port.length() < 1) {
			stb.append("80");
		} else {
			stb.append(port);
		}
		
		if(uri != null && uri.length() > 1) {
			stb.append(underscore.matcher(uri).replaceAll("_"));
		}
		
		return stb.toString();
	}
}
