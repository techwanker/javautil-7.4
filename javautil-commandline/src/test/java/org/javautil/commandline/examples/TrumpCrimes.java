package org.javautil.commandline.examples;

import org.javautil.commandline.CommandLineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrumpCrimes {

	private transient static final Logger logger = LoggerFactory.getLogger(TrumpCrimes.class);

	public TrumpCrimes(TrumpCrimesArguments args) {
		logger.info("crimes {}", args);
	}

	public static void main(String[] args)  {
		for (String arg :args) { 
			logger.info("arg " + arg);
		}
		TrumpCrimesArguments  arguments = new TrumpCrimesArguments();
		new CommandLineHandler(arguments).evaluateArguments(args);
		new TrumpCrimes(arguments);
		//invocation.process(arguments);
	}

}
