package org.javautil.oracle.metadata;

import org.javautil.core.jdbc.metadata.DatabaseObjectType;
import org.javautil.jdbc.metadata.AbstractDatabaseObject;

public class PackageSpecification extends AbstractDatabaseObject {

	public PackageSpecification(String owner, String name) {
		super(owner, name, DatabaseObjectType.PACKAGE_SPECIFICATION);

	}

	@Override
	public DatabaseObjectType getDatabaseObjectType() {
		return DatabaseObjectType.PACKAGE_SPECIFICATION;
	}

}
