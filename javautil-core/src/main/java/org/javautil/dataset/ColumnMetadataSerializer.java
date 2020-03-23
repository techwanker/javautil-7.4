package org.javautil.dataset;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;

public interface ColumnMetadataSerializer {

	void write(OutputStream os) throws IOException;

	ArrayList<ColumnMetadata> readAll() throws IOException;

	String asString();

	void write(Writer writer) throws IOException;

}
