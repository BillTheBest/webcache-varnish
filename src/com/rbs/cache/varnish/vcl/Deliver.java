package com.rbs.cache.varnish.vcl;

public class Deliver extends VCLMethod {

	@Override
	public ReturnType getReturnType() {
		return ReturnType.DELIVER;
	}

	@Override
	protected String getAsString() {
		return getTemplate("deliver.vcl");
	}

}
