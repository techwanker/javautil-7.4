package org.javautil.commandline;

import org.junit.After;
import org.junit.Before;

public abstract class NoExitUnitTest  {

	@Before
	public void before() throws Exception {
		System.setSecurityManager(new NoExitSecurityManager());
	}

	@After
	public void after() throws Exception {
		System.setSecurityManager(null);
	}

}
