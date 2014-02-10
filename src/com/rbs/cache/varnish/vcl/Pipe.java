package com.rbs.cache.varnish.vcl;

public class Pipe extends VCLMethod {

	@Override
	public ReturnType getReturnType() {
		return ReturnType.PIPE;
	}

	@Override
	protected String getAsString() {
		return getTemplate("pipe.vcl");
	}

}
