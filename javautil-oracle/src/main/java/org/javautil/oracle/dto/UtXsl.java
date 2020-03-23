package org.javautil.oracle.dto;

public class UtXsl {

	private long utXslNbr;
	private String fileName;
	private String stylesheet;

	public String getFileName() {
		return fileName;
	}

	public long getUtXslNbr() {
		return utXslNbr;
	}

	/**
	 * @return the xslScript
	 */
	public String getXslScript() {
		return stylesheet;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @param xslScript
	 *            the xslScript to set
	 */
	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	public void setUtXslNbr(long utXsl) {
		this.utXslNbr = utXsl;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("utXslNbr: ");
		b.append(utXslNbr);
		b.append("\n");
		b.append("fileName: ");
		b.append(fileName);
		b.append("\n");
		b.append("styleSheet: \n");
		b.append(stylesheet);
		return b.toString();

	}
}
