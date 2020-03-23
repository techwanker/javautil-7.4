package org.javautil.commandline.annotations;

import java.util.Collection;

import org.javautil.commandline.ParamType;

public class BigArgumentsBean {

	@Required
	private Boolean bean;

	@Optional
	@MultiValue(type = ParamType.BOOLEAN)
	private Collection<Boolean> flags;

	
}
