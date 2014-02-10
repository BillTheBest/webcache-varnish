package com.rbs.cache.varnish.vcl;

public class Init extends VCLMethod {

	@Override
	public ReturnType getReturnType() {
		return ReturnType.OK;
	}

	@Override
	protected String getAsString() {
		return null;
	}

}
