package org.javautil.dataset.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javautil.file.FileHelper;
import org.javautil.io.FileUtil;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

	final String                   tmpDir = "./target/tmp";

	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	@Before
	public void before() {
		File tmpDirFile = new File(tmpDir);
		tmpDirFile.mkdirs();
	}

	void showFile(File f) throws IOException {
		String contents = FileUtil.getAsString(f.getPath());
		logger.info("file {} contents:\n{}", f.getPath(), contents);
	}

	void checkMatch(File fileExpected, File fileActual) throws IOException {
		int contentsMatch = FileHelper.fileContentsMatch(fileExpected, fileActual);
		if (contentsMatch != 0) {
			logger.info("expectedFile: {} actualFile {}", fileExpected.getPath(), fileActual.getPath());
			showFile(fileExpected);
			showFile(fileActual);
		}
		assertEquals(0, contentsMatch);
	}

	public List<Object> arrayToList(Object[] array) {
		ArrayList<Object> retval = new ArrayList<>();
		List<Object> list = Arrays.asList(array);
		retval.addAll(list);
		return retval;
	}
}
