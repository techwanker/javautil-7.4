package org.javautil.poi.workbook;

import java.io.File;

import org.javautil.commandline.annotations.Required;
import org.javautil.core.sql.Binds;

public class WorkbookWriterArguments {

	@Required
	private File definition;
	
	@Required
	private String dataSourceName;
	
	@Required
	private File outfile;

	public File getDefinition() {
		return definition;
	}

	public void setDefinition(File definition) {
		this.definition = definition;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public File getOutfile() {
		return outfile;
	}

	public void setOutfile(File outfile) {
		this.outfile = outfile;
	}

	public Binds getBinds() {
		throw new UnsupportedOperationException();
	}
	
	
	
	
	
}
