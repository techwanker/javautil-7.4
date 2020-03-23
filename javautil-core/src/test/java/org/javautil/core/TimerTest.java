package org.javautil.core;

import static org.junit.Assert.assertTrue;

import org.javautil.core.misc.Timer;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerTest {

	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	@Ignore // TODO fails on AWS
	@Test 
	public void lap() throws InterruptedException {
		Timer t = new Timer();
		long lap1 = t.getElapsedMicrosLap();
		long lap2 = t.getElapsedMicrosLap();
		logger.info("1 {} 2 {}", lap1, lap2);
		Thread.sleep(5);
		long lap3 = t.getElapsedMicrosLap();
		assertTrue(lap3 - lap2 > 5000);
		assertTrue(lap3 - lap2 < 15000);
		logger.info("1 {} 2 {} 3 {}", lap1, lap2, lap3);
		long lap4 = t.getElapsedMicrosLap();
		logger.info("1 {} 2 {} 3 {} 4 {}", lap1, lap2, lap3, lap4);
	}

}
