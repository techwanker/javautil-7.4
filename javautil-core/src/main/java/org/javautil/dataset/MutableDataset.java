package org.javautil.dataset;

import java.util.ArrayList;

import org.javautil.dataset.filter.MutableDatasetFilter;
import org.javautil.dataset.math.CollectionMathOperation;

public interface MutableDataset extends Dataset {

	public void addColumn(ColumnMetadata columnMeta);

	// todo resolve if we want this, I believe this should be handled by the
	// renderer
	public void addFooter(int columnIndex, CollectionMathOperation footerMathOper);

	public void appendToRow(Integer rownum, Object[] values);

	public void applyFilters(MutableDatasetFilter... filters);

	public void applySorts(SortColumn... sorts);

	public void appendFooter();

	void appendToRow(Integer rownum, final ArrayList<Object> values);

	public void appendRow(ArrayList<Object> list);

}
