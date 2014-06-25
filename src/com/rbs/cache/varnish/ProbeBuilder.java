package com.rbs.cache.varnish;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProbeBuilder {
	private Map<String, Probe> probes = new HashMap<String, Probe>();
	
	private final Pattern pattern = Pattern.compile("HTTP/1\\.[10]\\s([0-9]{3}).*");
	
	private static final ProbeBuilder thisInstance;
	
	static {
		thisInstance = new ProbeBuilder();
	}
	
	public Probe getProbeFor(final String host, final String port) {
		Probe probe =  probes.get(Name.normalize(host, port, null));
		probe = createProbe(host, port, null);
		return probe;
	}
	
	public static final ProbeBuilder getInstance() {
		return thisInstance;
	}
	
	public Probe createProbe(String host, String port, String uri) {
		Probe probe = new Probe(host, port, null);
		
		if("443".equals(port)) {
			return null;
		}
		
		if("80".equals(port)) {
			port = "";
		} else {
			port = ":" + port;
		}
		
		if(uri != null && uri.length() > 1 && uri.charAt(0) != '/') {				
			uri = "/" + uri;
		}
		
		if("wp.it.com".equals(host)) {
			host = "wp.clicrbs.com.br";
		}
		
		if("it".equals(host)) {
			host = "zerohora.clicrbs.com.br";
		}
		
		if("it".equals(host)) {
			host = uri != null && uri.indexOf("temporeal") >= 0 ? "temporeal.clicrbs.com.br" : "zerohora.clicrbs.com.br";
		}
		
		if("s1.cdnrbs.com.br".equals(host)) {
			uri = "/js/csUDM_tag.js";
		}
		
		if(uri == null || uri.length() == 0) {
			uri = "/";
		} else {
			if(uri.indexOf("temporeal") >= 0) {
				uri = "/temporeal/js/floating-scoreboard.min.js";
				host = "temporeal.clicrbs.com.br";
			}
		}
		
		try {
			probe.setExpectedResponse(connect(host, port, uri));			
			probe.setRequest(
					"HEAD " + uri + " HTTP/1.1\"\n" +
					"\t\t\"Host: " + host + "\"\n" +
					"\t\t\"User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:21.0) Gecko/20100101 Firefox/21.0\"\n" +
					"\t\t\"Connection: close");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		probes.put(Name.normalize(host, port, uri), probe);
		
		return probe;
	}
	
	private final String connect(final String host, final String port, final String uri) throws MalformedURLException, IOException {
		HttpURLConnection.setFollowRedirects(false);
		
		HttpURLConnection con = (HttpURLConnection) new URL("http://" + host + port + uri).openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Lincolm Criando Probes)");
		con.connect();
		
		try {
			con.getInputStream();
			Matcher m = pattern.matcher(con.getHeaderField(null));
			m.matches();
			return m.group(1);
		} catch(IOException e) {
			return Integer.toString(con.getResponseCode());
		} finally {
			con.disconnect();
		}
	}
}
