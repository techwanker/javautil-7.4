package org.javautil.core.workbook;

import org.javautil.core.text.StringBuilderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BindDefinition {

	private String                 bindTypeName;

	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	public BindDefinition(String bindTypeName) {
		super();
		this.bindTypeName = bindTypeName;
	}

	public String getBindTypeName() {
		return bindTypeName;
	}

	public void setBindTypeName(String bindTypeName) {
		this.bindTypeName = bindTypeName;
	}

	@Override
	public String toString() {
		StringBuilderHelper b = new StringBuilderHelper("ColumnMetadata");
		b.append("bindTypeName", bindTypeName);
		String retval = b.toString();
		logger.debug("toString:\n{}", retval);
		return b.toString();
	}
}
