package com.rbs.cache.varnish.vcl;

import java.io.IOException;
import java.io.InputStream;

public abstract class VCLMethod {
	
	protected String name;
	
	protected StringBuilder stb = new StringBuilder();
	
	public abstract ReturnType getReturnType();
	
	protected abstract String getAsString();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static String getTemplate(final String name) {
		StringBuilder stb = new StringBuilder();
		InputStream in = null;
		int c = 0;
		try {
			in = VCLMethod.class.getResourceAsStream("/META-INF/templates/" + name);
			while((c = in.read()) != -1) {
				if(c == '\r') {
					continue;
				}
				stb.append((char) c);
			}
		} catch (Exception e) {
			//no need
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return stb.toString();
	}
	
	public enum ReturnType {DELIVER, HASH, FETCH, PASS, LOOKUP, PIPE, HIT_FOR_PASS, MISS, OK};
}
