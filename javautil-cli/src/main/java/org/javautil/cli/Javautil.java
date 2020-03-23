package org.javautil.cli;

import java.lang.reflect.InvocationTargetException;

//https://docs.oracle.com/javase/tutorial/reflect/member/methodInvocation.html
//public class InvokeMain {
//    public static void main(String... args) {
//	try {
//	    Class<?> c = Class.forName(args[0]);
//	    Class[] argTypes = new Class[] { String[].class };
//	    Method main = c.getDeclaredMethod("main", argTypes);
//  	    String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
//	    System.out.format("invoking %s.main()%n", c.getName());
//	    main.invoke(null, (Object)mainArgs);
//
//        // production code should handle these exceptions more gracefully
//	} catch (ClassNotFoundException x) {
//	    x.printStackTrace();
//	} catch (NoSuchMethodException x) {
//	    x.printStackTrace();
//	} catch (IllegalAccessException x) {
//	    x.printStackTrace();
//	} catch (InvocationTargetException x) {
//	    x.printStackTrace();
//	}
//    }
//}
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// https://docs.oracle.com/javase/tutorial/reflect/member/methodInvocation.html
public class Javautil {

	private static Map<String, String> association = new HashMap<String, String>() {
		{
			put("filelister", "org.javautil.cli.FileLister");
			put("csvunload" , "org.javautil.cli.SqlCsvExporterCli");
		}
	};
	private static Logger logger = LoggerFactory.getLogger(Javautil.class);

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException {
		if (args.length < 1) {
			throw new IllegalArgumentException("program name required");
		}
		String classAlias = args[0];
		String className = association.get(association);
		if (className == null) {
			throw new IllegalArgumentException("no association found for name: " + classAlias);
		}
		Class<?> mainClass = Class.forName(className);
		Class<?>[] argTypes = new Class[] { String[].class };
		Method main = mainClass.getDeclaredMethod("main", argTypes);
		String[] mainArgs = Arrays.copyOfRange(args, 1, args.length);
		String message = String.format("invoking %s with %d arguments '%s'", className, mainArgs.length, mainArgs);
		System.out.println(message);

		main.invoke(null, (Object) mainArgs);

		// Class<?> mainClass = Class.forName(className);
		// String[] classArgs = shift(args);
		// Method main;
		// try {
		// String [] stringArray;
		// main = getMain(mainClass);
		// } catch (SecurityException e) {
		// throw new RuntimeException(e);
		// }
		// catch (NoSuchMethodException e) {
		// Method[] methods = mainClass.getMethods();
		// StringBuilder sb = new StringBuilder();
		// for (Method method : methods) {
		// sb.append(method);
		// sb.append("\n");
		// }
		// throw new RuntimeException("no appropriate main known methods are " +
		// sb.toString());
		// }
//		String message = String.format("invoking %s with %d arguments '%s'", className, classArgs.length, classArgs);
//		logger.info(message);
//		main.invoke(classArgs);
	}

//	static String[] shift(String[] args) {
//		logger.info("args length: " + args.length);
//		String[] retval = new String[args.length - 1];
//		for (int i = 0; i < (args.length - 1); i++) {
//			retval[i] = args[i + 1];
//		}
//		return retval;
//	}
//
//	static Method getMain(Class clazz) {
//		Method[] methods = clazz.getMethods();
//		Method retval = null;
//		// TODO this might be the wrong one
//		for (Method method : methods) {
//			if (method.getName().equals("main")) {
//				retval = method;
//				break;
//			}
//		}
//		return retval;
//	}

}
