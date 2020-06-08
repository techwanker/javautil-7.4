package com.pacificdataservices.pdssr;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.javautil.core.sql.Binds;
import org.javautil.core.sql.Dialect;
import org.javautil.core.sql.SqlRunner;
import org.javautil.core.sql.SqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeedSalesReportingDatabase {
	private Connection connection;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Dialect dialect = Dialect.POSTGRES;

	private SqlStatement orgStmt = new SqlStatement(
			"insert into org (org_id, org_cd, org_nm) "
			+ "values (org_id_seq.nextval,:ORG_CD, :ORG_NM)");
	// private SqlStatement orgStmt = new SqlStatement("insert into org (org_cd,
	// org_nm) values (:ORG_CD, :ORG_NM)");

	private SqlStatement mfrStmt = new SqlStatement("insert into org_mfr( org_id,cds_mfr_id) "
			+ "select org_id, :CDS_MFR_ID " + "from   org " + "where org_cd = :ORG_CD"); // TODO externalize

	private SqlStatement orgDistrib = new SqlStatement(
			// "insert into org_distrib(org_id) " + "select org_id " + "from org " + "where
			// org_cd = %(ORG_CD)s");
			"insert into org_distrib(org_id,distrib_id) " + "select org_id, :DISTRIB_ID " + "from org "
					+ "where org_cd = :ORG_CD");

	private SqlStatement orgDatafeed = new SqlStatement(
			"insert into org_datafeed(org_id) " + "select org_id " + "from org " + "where org_cd = :ORG_CD");

	ArrayList<String[]> mfrs = new ArrayList<String[]>();

	public SeedSalesReportingDatabase(Connection connection) throws SQLException {
		this.connection = connection;
		// TODO exernalize file
		orgStmt.setConnection(connection);
		mfrStmt.setConnection(connection);
		orgDistrib.setConnection(connection);
		orgDatafeed.setConnection(connection);
		mfrs.add(new String[] { "0000000020", "F-L", "Frito-Lay" });
		mfrs.add(new String[] { "0000000030", "GM", "General Mills" });
		mfrs.add(new String[] { "0000000040", "HVEND", "Hershey Vending" });
		mfrs.add(new String[] { "0000000050", "HFUND", "Hershey Fund Raising" });
		mfrs.add(new String[] { "0000000055", "HCONC", "Hershey Concession" });
		mfrs.add(new String[] { "0000000060", "SNYDERS", "Snyder's of Hanover" });
		mfrs.add(new String[] { "0000000080", "KELLOGG", "Kellogg, Keebler" });
		mfrs.add(new String[] { "0000000115", "KARS", "Kar Nut Product (Kar's)" });
		mfrs.add(new String[] { "0000000135", "MARS", "Mars Chocolate " });
		mfrs.add(new String[] { "0000000145", "POORE", "Inventure Group (Poore Brothers)" });
		mfrs.add(new String[] { "0000000150", "WOW", "WOW Foods" });
		mfrs.add(new String[] { "0000000160", "CADBURY", "Cadbury Adam USA, LLC" });
		mfrs.add(new String[] { "0000000170", "MONOGRAM", "Monogram Food" });
		mfrs.add(new String[] { "0000000185", "JUSTBORN", "Just Born" });
		mfrs.add(new String[] { "0000000190", "HOSTESS", "Hostess, Dolly Madison" });
		mfrs.add(new String[] { "0000000210", "SARALEE", "Sara Lee" });
		mfrs.add(new String[] { "0000000005", "ExoticTx", "Exotic Texas" });

	}

	void seedManufacturers() throws SQLException, IOException {

		for (String[] mfr : mfrs) {
			Binds binds = new Binds();
			binds.put("CDS_MFR_ID", mfr[0]);
			binds.put("ORG_CD", mfr[1]);
			binds.put("ORG_NM", mfr[2]);

			orgStmt.setTrace(true);

			orgStmt.execute(binds);
			mfrStmt.execute(binds);
//			orgStmt.execute(connection, dialect, binds);
//			mfrStmt.execute(connection, dialect, binds);
		}
	}

	void seedDistributor() throws SQLException {
		Binds binds = new Binds();
		binds.put("ORG_CD", "EXOTICTX");
		binds.put("ORG_NM", "Texas Exotic Foods");
		binds.put("DISTRIB_ID", "0000000001");
		orgStmt.execute(binds);
		orgDistrib.execute(binds);
		orgDatafeed.execute(binds);
//		orgStmt.execute(connection, dialect, binds);
//		orgDistrib.execute(connection, dialect, binds);
//		orgDatafeed.execute(connection, dialect, binds);
	}

	InputStream getResource(Class callingClass, String resourceName) {
		logger.info("resourceName: " + resourceName);
		URL url = callingClass.getResource(resourceName);
		logger.info("resourceName " + resourceName + " url " + url);
		InputStream is = this.getClass().getResourceAsStream(resourceName);
		if (is == null) {
			throw new IllegalStateException("resource " + resourceName + " failed to load");

		}
		return is;
	}

	public void seedProduct() throws SQLException, IOException {
		SqlRunner runner;
		InputStream productPopIs;
		productPopIs = getResource(this.getClass(), "pop_product.sql");
		logger.info("about to populate product table");
		runner = new SqlRunner(productPopIs);
		runner.setConnection(connection).setShowError(true).setContinueOnError(false);
		// runner = new SqlRunner(productPopIs, connection, dialect, false, true, false,
		// 0);
		runner.process();

	}

	public void process() throws SQLException, IOException {
		seedManufacturers();
		seedDistributor();
		seedProduct();
	}

}