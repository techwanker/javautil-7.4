package org.javautil.core.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import javax.sql.DataSource;

import org.yaml.snakeyaml.Yaml;

public class DataSources {

	private String                   yamlFileName;

	Map<String, Map<String, Object>> connParmMap;

	public DataSources() {
		String homeDir = System.getProperty("user.home");
		yamlFileName = homeDir + "/connections_java.yaml";

		try {
			loadFile();
		} catch (FileNotFoundException e) {
			File f = new File(yamlFileName);
			throw new RuntimeException(f.getAbsolutePath() + " does not exist");
		}
	}

	public DataSources(String yamlFile) throws FileNotFoundException {
		this.yamlFileName = yamlFile;
		loadFile();
	}

	@SuppressWarnings("unchecked")
	void loadFile() throws FileNotFoundException {

		Yaml yaml = new Yaml();
		File f = new File(yamlFileName);
		if (!f.exists()) {
			throw new FileNotFoundException("no such file:\n" + yamlFileName + "\n" + f.getAbsolutePath());
		}
		InputStream ios = new FileInputStream(new File(yamlFileName));
		connParmMap = (Map<String, Map<String, Object>>) yaml.load(ios);
	}

	public DataSource getDataSource(String name) {
		return getDataSourceHelper(name).getDataSource();
	}

	public DataSourceHelper getDataSourceHelper(String name) {
		Map<String, Object> parms = connParmMap.get(name);
		if (parms == null) {
			throw new IllegalArgumentException("No such datasource '" + name + "'");
		}
		DataSource ds = DataSourceFactory.getDatasource(parms);
		DataSourceHelper dsh = new DataSourceHelper(ds, parms);
		return dsh;
	}
}
