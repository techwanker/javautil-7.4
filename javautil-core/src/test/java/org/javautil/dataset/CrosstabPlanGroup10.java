package org.javautil.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyVetoException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.DataSourceFactory;
import org.javautil.core.sql.SqlStatement;
import org.javautil.document.crosstab.CrosstabColumns;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jjs @ dbexperts
 * 
 */
public class CrosstabPlanGroup10 extends BaseTest {

	public Logger   logger = LoggerFactory.getLogger(getClass());

	SqlStatement    ss;
	CrosstabColumns ctc;
	Binds           binds  = new Binds();

	@Before
	public void before() throws PropertyVetoException, SQLException {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource datasource = dsf.getDatasource("aerodemo_postgres");
		Connection conn = datasource.getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute("set search_path to aerodemo");
		ss = new SqlStatement(conn,
		    "select \n" + " item_nbr,\n" + " item_cd,\n" + " org_nbr_cust,\n" + " org_cd_cust,\n" + " org_nbr_mfr,\n"
		        + " org_cd_mfr,\n" + " eff_dt_beg,\n" + " eff_dt_end,\n" + " mfr_restrict_id\n" + "from oe_cust_mfr_vw\n"
		        + "where item_nbr in (\n" + "   select item_nbr from aps_plan_grp where plan_grp_nbr = :plan_grp\n" + ")\n"
		        + "");
		final List<String> rowId = toList("org_cd_cust", "item_cd");
		final List<String> cellId = toList("mfr_restrict_id");
		ctc = new CrosstabColumns(rowId, "org_cd_mfr", cellId);
		binds.put("plan_grp", 10);
	}

	ListOfNameValueDataset dataset;

	@Ignore
	@Test
	public void getDatasetFromRDBMS() throws PropertyVetoException, SQLException {
		dataset = ss.getListOfNameValueDataSet(binds);
		logger.info("rows {}", dataset.size());
		logger.info(dataset.toString());
	};

	@Ignore
	@Test
	public void marshall() throws PropertyVetoException, SQLException, IOException {
		getDatasetFromRDBMS();
		FileOutputStream datasetOut = new FileOutputStream("target/approvedMfr.dataset.csv");
		FileOutputStream metaOut = new FileOutputStream("target/approvedMfr.metadata.csv");
		DatasetMarshaller dillon = new DatasetMarshaller();
		dillon.writeMetadata(dataset, metaOut);
		dillon.writeData(dataset, datasetOut);
		metaOut.close();
		datasetOut.close();
	}

	@Test
	public void crosstab() throws PropertyVetoException, SQLException {
		getDatasetFromRDBMS();
		final DatasetCrosstabber coke = new DatasetCrosstabber();
		coke.setCrosstabColumns(ctc);
		coke.setDataSet(dataset);

		final Dataset ds = coke.getDataSet();
		logger.info("crosstabbed:\n{}", ds.toString());

	}

	@Test
	public void crosstab2() throws SQLException {
		MatrixDataset ds = ss.getCrosstabbedDataset(binds, ctc);
		assertNotNull(ds);
		assertEquals(3, ds.getRowCount());
	}

	List<String> toList(final String... o) {
		final ArrayList<String> al = new ArrayList<String>(o.length);
		for (final String element : o) {
			al.add(element);
		}
		return al;
	}
}
