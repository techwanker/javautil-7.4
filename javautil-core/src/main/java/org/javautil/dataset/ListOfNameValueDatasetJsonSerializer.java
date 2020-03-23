package org.javautil.dataset;


import java.util.ArrayList;
import java.util.List;

import org.javautil.core.json.JsonSerializerGson;
import org.javautil.util.ListOfNameValue;
import org.javautil.util.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.internal.LinkedTreeMap;

public class ListOfNameValueDatasetJsonSerializer {
private transient final Logger logger = LoggerFactory.getLogger(getClass());

	  private DatasetMetadata meta;
		private ArrayList<NameValue> data;
		private transient ListOfNameValueDataset dataset;
		private 	JsonSerializerGson serializer = new JsonSerializerGson();
	
		
		public ListOfNameValueDatasetJsonSerializer(ListOfNameValueDataset dataset) {
			if (dataset == null) {
				throw new IllegalArgumentException("dataset is null");
			}
			this.dataset = dataset;
			this.meta = dataset.getMetadata();
			//this.data = dataset.super;
			data = new ListOfNameValue();
			data.addAll(dataset.getRows());
			logger.info("this{}", this.toString());
		}
		
		String toJson() {
			String retval = serializer.toJson(this);
			logger.info("data {}\n",data);
			logger.info("meta {}\n",meta );
			logger.info("retval:\n{}",retval);
			return retval;
		}
		
		
		String toJsonPretty() {
			
			String retval = serializer.toJsonPretty(this);
			logger.info("data\n{}",data);
			logger.info("meta\n{}",meta );
			logger.info("retval:\n{}",retval);
			return retval;
		}
		
		
//		String toJsonPretty2() {
//			JsonSerializerGson serializer = getPrettySerializer();
//		
//			String retval = dillon.toJsonPretty(dataset);
//			logger.info("retval:\n{}",this);
//			return retval;
//		}
//		
		public ListOfNameValueDataset getDataset2(String json) {
			ListOfNameValueDataset retval = (ListOfNameValueDataset) serializer.toObjectFromJson(json,ListOfNameValueDataset.class);
			logger.info("getDataset2\n {}",retval);
			return retval;
		}
	//	public DatasetMetaDataImpl
		public ListOfNameValueDataset getDataset(String json) {
			Object o = serializer.toObjectFromJson(json);
			
			logger.info("o is "+ o.getClass());
			logger.info("getDataset o\n{}",o);
			
			@SuppressWarnings("rawtypes")
			LinkedTreeMap map = (LinkedTreeMap) o;
			@SuppressWarnings("rawtypes")
			Object metaObject = map.get("meta");
			logger.info("metaObject is "+ metaObject.getClass());
			logger.info("metaObject{}\n",metaObject);
			//String metaJson = 
			ArrayList<ColumnMetadata> metaColumns = new ArrayList<ColumnMetadata>();
			DatasetMetadataImpl metadata = new DatasetMetadataImpl();
			@SuppressWarnings("rawtypes")
			LinkedTreeMap metaTreeMap = (LinkedTreeMap) metaObject;
			Object listObject = metaTreeMap.get("list");
			logger.info("listObject is "+ listObject.getClass());
			logger.info("listObject{}\n",listObject);
		//	logger.info("getDataset list\n{}",list);
	//		List list = (List) map.get(meta);
			
			ListOfNameValueDataset dataset = new ListOfNameValueDataset();
			
		//	DatasetMetaDataImpl meta = dillon.toO
//			dataset.setMetadata(metadata);
//			dataset.addAll()
			return dataset;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ListOfNameValueDatasetJsonSerializer meta=\n");
			builder.append(meta);
			builder.append("\ndata\n");
			builder.append(data);
			return builder.toString();
		}

}
