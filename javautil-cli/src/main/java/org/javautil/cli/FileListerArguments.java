package org.javautil.cli;

import java.io.File;
import java.util.List;

import org.javautil.commandline.CommandLineHandler;
import org.javautil.commandline.ParamType;
import org.javautil.commandline.annotations.MultiValue;
import org.javautil.commandline.annotations.Optional;

public class FileListerArguments {
	@Optional
	private File outputFile;
	
	@Optional
	private String dateFormat;
	
	@Optional 
	private String sourceName;
	
	@Optional
	private boolean noDigest = false;
	
	/**
	 * @return the noDigest
	 */
	public boolean isNoDigest() {
		return noDigest;
	}

	/**
	 * @param noDigest the noDigest to set
	 */
	public void setNoDigest(boolean noDigest) {
		this.noDigest = noDigest;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @param sourceName the sourceName to set
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	// todo is this really a director mask?
	@Optional 
    @MultiValue(type = ParamType.FILE)
	private List<File> directoryMask;

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public List<File> getDirectoryMask() {
		return directoryMask;
	}

	public void setDirectoryMask(List<File> directoryMask) {
		this.directoryMask = directoryMask;
	}
	
	public void parseArguments(String ... args) {
		new CommandLineHandler(this).evaluateArguments(args);
	}
}
