package org.javautil.core.text;

public class BooleanText {

	static String values = "y| Y| yes| Yes| YES, true, True TRUE|false|False|FALSE |n|N|no|No|NO |on|On|ON|off|Off|OFF";

	public static boolean booleanValue(String in) {
		if (in == null) {
			throw new IllegalArgumentException("in is null");
		}
		Boolean retval = null;
		String upper = in.toUpperCase();
		if (upper.equals("Y") || upper.equals("YES") || upper.equals("TRUE") || upper.equals("ON")) {
			retval = true;
		} else {
			if (upper.equals("N") || upper.equals("No") || upper.equals("FALSE") || upper.equals("OFF")) {
				retval = false;
			}
			if (retval == null) {
				throw new IllegalArgumentException("must be one of " + values);
			}
		}
		return retval;
	}
}
