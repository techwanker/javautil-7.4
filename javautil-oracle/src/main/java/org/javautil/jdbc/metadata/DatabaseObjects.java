package org.javautil.jdbc.metadata;

import org.javautil.core.jdbc.metadata.DatabaseObjectType;

public interface DatabaseObjects {

	public String getOwner();

	public String getName();

	public DatabaseObjectType getObjectType();

	public DDL getSource();

}
