package com.rbs.cache.varnish;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Probe extends AbstractVCLObject implements Comparable<Probe> {
	private String url;
	private String request;
	private String interval = "1s";
	private String timeout = "0.5s"; //how many time to wait to finish the probe request
	private String window = "9"; //how many of the latest polls to check backend is healthy
	private String threshold = "3"; //how many of the latests from window to determine if backend is healthy
	private String expectedResponse = "200";
	
	public Probe(final String host, final String port, final String uri) {
		name = "hc_" + Name.normalize(host, port, uri);
	}

	public String getExpectedResponse() {
		return expectedResponse;
	}

	public void setExpectedResponse(String expectedResponse) {
		this.expectedResponse = expectedResponse;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(final String request) {
		this.request = request;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		if(interval != null) {
			this.interval = interval + "s";
		} else {
			this.interval = interval;
		}
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getWindow() {
		return window;
	}

	public void setWindow(String window) {
		this.window = window;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
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
		Probe other = (Probe) obj;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw) {
			public void println(final String text) {
				print(text);
				print('\n');
			}
		};

		sw.append("probe ").append(getName()).append(" {");
		out.println();
		if(request != null) {
			out.println("\t.request = \"" + request + "\";");
		} else {
			out.println("\t.url = \"" + url + "\";");
		}
		out.println("\t.interval = " + interval + ";");
		out.println("\t.timeout = " + timeout + ";");
		out.println("\t.window = " + window + ";");
		out.println("\t.threshold = " + threshold + ";");
		out.println("\t.expected_response = " + expectedResponse + ";");
		out.println("}");

		return sw.toString();
	}

	public int compareTo(final Probe other) {
		return name.compareTo(other.name);
	}
}
