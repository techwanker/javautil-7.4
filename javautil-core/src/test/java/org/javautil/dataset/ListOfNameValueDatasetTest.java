package org.javautil.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.javautil.core.json.JsonSerializerGson;
import org.javautil.util.NameValue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ListOfNameValueDatasetTest {
private transient static final Logger logger = LoggerFactory.getLogger(ListOfNameValueDatasetTest.class);

	private ListOfNameValueDataset dataset = null;

	@Before
	public void before() throws Exception {
		dataset = getTestDataset();
	}

	@Test
	public void testGetRow() throws Exception {
		final DatasetIterator<Object> iterator = dataset.getDatasetIterator();
		assertTrue(iterator.next());
	}

	@Test
	public void testSingleColumnGet() throws Exception {
		final Map<String, Object> row = dataset.get(0);
		assertEquals("fifi", row.get("name"));
	}

	@Test
	public void testStringColumnIterator() throws Exception {
		final DatasetIterator iterator = dataset.getDatasetIterator();
		iterator.next();
		assertEquals("fifi", iterator.getObject(0));
		iterator.next();
		assertEquals("socks", iterator.getObject(0));
		iterator.next();
		assertEquals("dolce", iterator.getObject(0));
		iterator.next();
		assertEquals("chewy", iterator.getObject(0));
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testStringColumnIteratorByColumnName() throws Exception {
		final DatasetIterator iterator = dataset.getDatasetIterator();
		iterator.next();
		assertEquals("fifi", iterator.getObject("name"));
		iterator.next();
		assertEquals("socks", iterator.getObject("name"));
		iterator.next();
		assertEquals("dolce", iterator.getObject("name"));
		iterator.next();
		assertEquals("chewy", iterator.getObject("name"));
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testIntegerColumnIterator() throws Exception {
		final DatasetIterator iterator = dataset.getDatasetIterator();
		iterator.next();
		assertEquals(3, iterator.getObject(2));
		iterator.next();
		assertEquals(14, iterator.getObject(2));
		iterator.next();
		assertEquals(2, iterator.getObject(2));
		iterator.next();
		assertEquals(6, iterator.getObject(2));
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDoubleColumnIterator() throws Exception {
		final DatasetIterator iterator = dataset.getDatasetIterator();
		iterator.next();
		assertEquals(11.25, iterator.getObject(1));
		iterator.next();
		assertEquals(13.5, iterator.getObject(1));
		iterator.next();
		assertEquals(6.125, iterator.getObject(1));
		iterator.next();
		assertEquals(65.3333, iterator.getObject(1));
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testDatasetConstructor() throws Exception {
		dataset = new ListOfNameValueDataset(dataset);
		testGetRow();
		testSingleColumnGet();
		testStringColumnIterator();
		testStringColumnIteratorByColumnName();
	}

	@Test
	public void testDatasetMetadataConstructor() throws Exception {
		dataset = new ListOfNameValueDataset(dataset, dataset.getMetadata());
		testGetRow();
		testSingleColumnGet();
		testStringColumnIterator();
		testStringColumnIteratorByColumnName();
	}


	// TODO this is a ridiculous dataset get one from vendimg
	private static ListOfNameValueDataset getTestDataset() throws ParseException {
		final DatasetMetadataImpl meta = new DatasetMetadataImpl();
		meta.addColumn(new ColumnMetadata().withColumnName("name").withColumnIndex( 0).withDataType( DataType.STRING)); //.wit; // 32, null, null, null));
		meta.addColumn(new ColumnMetadata().withColumnName("weight").withColumnIndex( 1).withDataType( DataType.DOUBLE));  // 9, 9, null, null, null, null));
		meta.addColumn(new ColumnMetadata().withColumnName("age").withColumnIndex( 2).withDataType( DataType.INTEGER));
		meta.addColumn(new ColumnMetadata().withColumnName("birthday").withColumnIndex( 3).withDataType( DataType.DATE)); 
		final ListOfNameValueDataset dataset = new ListOfNameValueDataset(meta);
		NameValue fifi = new NameValue();
		fifi.put("name","fifi");
		fifi.put("weight",11.25);
		fifi.put("age",3);
		fifi.put("birthday",toDate("01/09/2005"));
		dataset.add(fifi);
		NameValue socks = new NameValue();
		socks.put("name","socks");
		socks.put("weight",13.5);
		socks.put("age",14);
		socks.put("birthday",toDate("02/21/1994"));
		dataset.add(socks);
		//dataset.addRow(toList("dolce", 6.125, 2, toDate("08/02/2006")));
		NameValue dolce  = new NameValue();
		dolce.put("name","dolce");
		dolce.put("weight",6.125);
		dolce.put("age",2);
		dolce.put("birthday",toDate("08/02/2006"));
		dataset.add(dolce);
		//dataset.addRow(toList("chewy", 65.33333, 6, toDate("08/02/2002")));
		NameValue chewy = new NameValue();
		chewy.put("name","chewy");
		chewy.put("weight",65.3333);
		chewy.put("age",6);
		chewy.put("birthday",toDate("08/02/2002"));
		dataset.add(chewy);
		logger.info("ListOfNameValueDataset:\n{}",dataset);
		return dataset;
	}

	public static Date toDate(final String date) throws ParseException {
		return new SimpleDateFormat("MM/dd/yyyy").parse(date);
	}

	public static ArrayList<Object> toList(final Object... objects) {
		final ArrayList<Object> list = new ArrayList<Object>();
		for (final Object object : objects) {
			list.add(object);
		}
		return list;
	}

	@Ignore
	@Test
	public void testJsonSerialRoundTrip() throws ParseException {
		JsonSerializerGson serializer = new JsonSerializerGson();
		String json = serializer.toJson(dataset);
		assertNotNull(dataset.getMetadata());
		logger.info("json:\n{}",json);
		Object lazareth =  serializer.toObjectFromJson(json, ListOfNameValueDataset.class);
		logger.info("lazareth.class {} ", lazareth.getClass());
		assertTrue(lazareth instanceof ListOfNameValueDataset);
		ListOfNameValueDataset rizzen = (ListOfNameValueDataset) lazareth;
		assert(dataset.equals(rizzen));
	}
	
	@Ignore
	@Test
	public void testSerialRoundTrip() throws ParseException {
		ListOfNameValueDatasetJsonSerializer serializer = new ListOfNameValueDatasetJsonSerializer(dataset); 
		String json = serializer.toJsonPretty();
		assertNotNull(dataset.getMetadata());
		logger.info("json:\n{}",json);
		Object lazareth =  serializer.getDataset(json);
		logger.info("lazareth.class {} ", lazareth.getClass());
		assertTrue(lazareth instanceof ListOfNameValueDataset);
		ListOfNameValueDataset rizzen = (ListOfNameValueDataset) lazareth;
		assert(dataset.equals(rizzen));
	}
	
	@Ignore // TODO
	@Test
	public void testSerialRoundTrip2() throws ParseException {
		ListOfNameValueDatasetJsonSerializer serializer = new ListOfNameValueDatasetJsonSerializer(dataset); 
		String json = serializer.toJsonPretty();
		logger.info("testSerialRoundTrip2 json:\n{}",json);
		ListOfNameValueDataset lazareth = serializer.getDataset2(json);
		logger.info("lazareth\n {}",lazareth);
	}
}
