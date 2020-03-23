package org.javautil.commandline.examples;

import java.util.Collection;

import org.javautil.commandline.annotations.Optional;
import org.javautil.commandline.annotations.Required;

public class TrumpCrimesArguments {

	   @Optional
     private int criminalCounts;
     
     @Required
     private Collection<String> crimes;
}
