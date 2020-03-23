package org.javautil.core.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timer {
	private static final Logger logger    = LoggerFactory.getLogger(Timer.class);
	private final long          startTime = System.nanoTime();
	private long                endTime;
	private long                lapStartTime = startTime;
	private String              name      = "";
	private String              info      = "";
	private Class<?>            callingClass;
	private long                runningTime = 0;
  private long                runStartTime = 0;
	public Timer() {
	}

	public Timer(String name) {
		this.name = name;
	}

	public Timer(Class<?> callingClass, String name) {
		this.callingClass = callingClass;
		this.name = name;
	}

	public Timer(String name, String info) {
		this.name = name;
		this.info = info;
	}

	public Timer(Class<?> callingClass, String name, String info) {
		this.callingClass = callingClass;
		this.name = name;
		this.info = info;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getElapsedMillis() {
		return (System.nanoTime() - startTime) / 1000000;
	}

public long getElapsedMicros() {
		long to = endTime == 0 ? System.nanoTime() : endTime;
		return (to - startTime) / 1000;
	}
	
public long getElapsedMicrosLap() {
	  long now = System.nanoTime();
		long retval =  (now - lapStartTime) / 1000;
		lapStartTime = now;
		return retval;
	}

	public void logElapsed() {
		logElapsed("");
	}

	public void logElapsed(String additional) {
		final long elapsedMillis = (System.nanoTime() - startTime) / 1000000;
		logger.info(name + ": elapsed millis " + elapsedMillis + " info: " + info + " " + additional);
	}

	@Override
	public String toString() {
		return "Timer [startTime=" + startTime + ", name=" + name + ", info=" + info + "]";
	}

	public void resume() {
		runStartTime = System.nanoTime();
	}
	
	public void pause() {
		long now = System.nanoTime();
		if (runStartTime == 0) {
			logger.error("no previous resume");
		}
		runningTime += now - runStartTime;
		runStartTime = 0;
	}

	public long getRunningMicros() {
		return runningTime / 1000;
	}
	public void stop() {
		endTime = System.nanoTime();
	}

	/**
	 * @return the callingClass
	 */
	public Class<?> getCallingClass() {
		return callingClass;
	}

	public String getInfo() {
		return info;
	}
}
