package com.rbs.cache.varnish.vcl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.rbs.cache.HttpMethod;
import com.rbs.cache.Site;
import com.rbs.cache.varnish.Director;
import com.rbs.cache.varnish.Probe;

public class VCL {
	
	private boolean printHitFlag = false;
	private Set<String> allowInvalidation = new HashSet<String>();
	
	//HEAD, PUT, TRACE, OPTIONS
	private Set<HttpMethod> allowedMethods = new HashSet<HttpMethod>();
	
	private String defaultTtl = "3h";
	private String defaultGrace = "20m";
	
	private boolean generateAutoExpires = true;
	
	private Set<Probe> probes = new HashSet<Probe>();
	private Set<Director> directors = new HashSet<Director>();	
	private Map<String, Site> sites = new HashMap<String, Site>();
	
	public VCL() {
		//Safe methods
		allowedMethods.add(HttpMethod.GET);
		allowedMethods.add(HttpMethod.HEAD);
		allowedMethods.add(HttpMethod.POST);
	}
	
	public void allowMethod(final HttpMethod method) {
		allowedMethods.add(method);
	}

	public boolean isGenerateAutoExpires() {
		return generateAutoExpires;
	}

	public void setGenerateAutoExpires(final boolean generateAutoExpires) {
		this.generateAutoExpires = generateAutoExpires;
	}

	public String getDefaultTtl() {
		return defaultTtl;
	}

	public void setDefaultTtl(final String defaultTtl) {
		this.defaultTtl = defaultTtl;
	}

	public String getDefaultGrace() {
		return defaultGrace;
	}

	public void setDefaultGrace(final String defaultGrace) {
		this.defaultGrace = defaultGrace;
	}

	public void grantInvalidation(final String cidr) {
		allowInvalidation.add(cidr);
	}
	
	public void revokeInvalidate(final String cidr) {
		allowInvalidation.remove(cidr);
	}

	public boolean isUsesProbe() {
		return probes != null && !probes.isEmpty();
	}

	public boolean isPrintHitFlag() {
		return printHitFlag;
	}

	public void setPrintHitFlag(boolean printHitFlag) {
		this.printHitFlag = printHitFlag;
	}

	public Set<Probe> getProbes() {
		return probes;
	}

	public void setProbes(Set<Probe> probes) {
		this.probes = probes;
	}
	
	public Set<Director> getDirectors() {
		return directors;
	}

	public void setDirectors(Set<Director> directors) {
		this.directors = directors;
	}

	public Map<String, Site> getSites() {
		return sites;
	}

	public void setSites(Map<String, Site> sites) {
		this.sites = sites;
	}
}