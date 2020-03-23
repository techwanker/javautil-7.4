package org.javautil.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jjs@dbexperts.com
 * 
 */
public interface MutableDatasetMetadata extends DatasetMetadata {

	public void addColumn(ColumnMetadata column);

	public void addColumn(int index, ColumnMetadata column);

	@Override
	public ArrayList<ColumnMetadata> getColumnMetadata();

}
