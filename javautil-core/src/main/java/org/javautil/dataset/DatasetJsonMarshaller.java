package org.javautil.dataset;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class DatasetJsonMarshaller {

	private List<ColumnMetadata>    meta;
	private List<ArrayList<Object>> data;

	public DatasetJsonMarshaller(MatrixDataset dataset) {
		this.meta = dataset.getMetadata().getColumnMetadata();
		this.data = dataset.getCells();
	}

	// TODO have to deal with dates
	public String toJson() {
		Gson gson = new Gson();
		String retval = gson.toJson(this);
		return retval;

	}
}
