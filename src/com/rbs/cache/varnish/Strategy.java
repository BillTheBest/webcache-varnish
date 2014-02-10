package com.rbs.cache.varnish;

public enum Strategy {
	ROUND_ROBIN, RANDOM, CLIENT, DNS;
	
	public String toString() {
		return name().toLowerCase().replace('_', '-');
	}
}
