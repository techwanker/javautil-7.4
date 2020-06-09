package org.javautil.io;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResourceHelperTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String resourceName = "/ddl/oracle/logger_tables.sr.sql";
	
	//@Test
	public void getResource() throws IOException {
		File f  = ResourceHelper.getResourceAsFile(this, "ddl/oracle/logger_tables.sr.sql");
		assertNotNull(f);
		System.out.println("f " + f) ;
		FileInputStream fis = new FileInputStream(f);
		assertNotNull(fis);
		fis.close();
	}
	
	@Test 
	public void getResourceStream() throws IOException {
		InputStream  is = getClass().getResourceAsStream(resourceName);
		assertNotNull(is);
		String text = IOUtils.readStringFromStream(is);
		logger.info("text is {}", text);
		assertNotNull(text);
		is.close();
		
	}

}
