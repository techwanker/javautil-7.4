package org.javautil.oracle.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.javautil.core.jdbc.metadata.Column;
import org.javautil.dataset.ColumnAttributes;

/**
 * Contains a representation of the data persisted in a tuple of TAB_COLUMN .
 * This code was generated by com.javautil.JavaGen.BaseRowGenerator on Sun Oct
 * 19 10:13:00 EDT 2008
 */
public class OracleTableColumn extends Column implements ColumnAttributes {

	/** Container for the data persisted in DATA_TYPE_MOD. varchar2(3) */
	private String dataTypeMod = null;

	/** Container for the data persisted in DATA_TYPE_OWNER. varchar2(30) */
	private String dataTypeOwner = null;

	/** Container for the data persisted in DEFAULT_LENGTH. number(22) */
	private Integer defaultLength = null;

	/** Container for the data persisted in NUM_DISTINCT. number(22) */
	private Long numDistinct = null;

	/** Container for the data persisted in DENSITY. number(22) */
	private BigDecimal density = null;

	/** Container for the data persisted in NUM_NULLS. number(22) */
	private Long numNulls = null;

	/** Container for the data persisted in NUM_BUCKETS. number(22) */
	private Integer numBuckets = null;

	/** Container for the data persisted in LAST_ANALYZED. date */
	private Timestamp lastAnalyzed = null;

	/** Container for the data persisted in SAMPLE_SIZE. number(22) */
	private Long sampleSize = null;

	/** Container for the data persisted in CHARACTER_SET_NAME. varchar2(44) */
	private String characterSetName = null;

	/** Container for the data persisted in CHAR_COL_DECL_LENGTH. number(22) */
	private Integer charColDeclLength = null;

	/** Container for the data persisted in GLOBAL_STATS. varchar2(3) */
	private String globalStats = null;

	/** Container for the data persisted in USER_STATS. varchar2(3) */
	private String userStats = null;

	/** Container for the data persisted in AVG_COL_LEN. number(22) */
	private Integer avgColLen = null;

	/** Container for the data persisted in CHAR_LENGTH. number(22) */
	private Integer charLength = null;

	/** Container for the data persisted in CHAR_USED. varchar2(1) */
	private String charUsed = null;

	/** Container for the data persisted in V80_FMT_IMAGE. varchar2(3) */
	private String v80FmtImage = null;

	/** Container for the data persisted in DATA_UPGRADED. varchar2(3) */
	private String dataUpgraded = null;

	/** Container for the data persisted in HISTOGRAM. varchar2(15) */
	private String histogram = null;

	/**
	 * Accessor set method for dataTypeMod. No validation provided in base
	 * method.
	 */
	public void setDataTypeMod(String val) {
		dataTypeMod = val;
	}

	/** Accessor get method for dataTypeMod. */
	public String getDataTypeMod() {
		return dataTypeMod;
	}

	/**
	 * Accessor set method for dataTypeOwner. No validation provided in base
	 * method.
	 */
	public void setDataTypeOwner(String val) {
		dataTypeOwner = val;
	}

	/** Accessor get method for dataTypeOwner. */
	public String getDataTypeOwner() {
		return dataTypeOwner;
	}

	/**
	 * Accessor set method for defaultLength. No validation provided in base
	 * method.
	 */
	public void setDefaultLength(int val) {
		defaultLength = Integer.valueOf(val);
	}

	/** Accessor get method for defaultLength. */
	public Integer getDefaultLength() {
		return defaultLength;
	}

	/**
	 * Accessor set method for numDistinct. No validation provided in base
	 * method.
	 */
	public void setNumDistinct(long val) {
		numDistinct = new Long(val);
	}

	/** Accessor get method for numDistinct. */
	public Long getNumDistinct() {
		return numDistinct;
	}

	/**
	 * Accessor set method for density. No validation provided in base method.
	 */
	public void setDensity(BigDecimal val) {
		density = val;
	}

	/** Accessor get method for density. */
	public BigDecimal getDensity() {
		return density;
	}

	/**
	 * Accessor set method for numNulls. No validation provided in base method.
	 */
	public void setNumNulls(Long val) {
		numNulls = val;
	}

	/** Accessor get method for numNulls. */
	public Long getNumNulls() {
		return numNulls;
	}

	/**
	 * Accessor set method for numBuckets. No validation provided in base
	 * method.
	 */
	public void setNumBuckets(Integer val) {
		numBuckets = val;
	}

	/** Accessor get method for numBuckets. */
	public Integer getNumBuckets() {
		return numBuckets;
	}

	/**
	 * Accessor set method for lastAnalyzed. No validation provided in base
	 * method.
	 */
	public void setLastAnalyzed(Timestamp val) {
		lastAnalyzed = val;
	}

	/** Accessor get method for lastAnalyzed. */
	public Timestamp getLastAnalyzed() {
		return lastAnalyzed;
	}

	/**
	 * Accessor set method for sampleSize. No validation provided in base
	 * method.
	 */
	public void setSampleSize(Long val) {
		sampleSize = val;
	}

	/** Accessor get method for sampleSize. */
	public Long getSampleSize() {
		return sampleSize;
	}

	/**
	 * Accessor set method for characterSetName. No validation provided in base
	 * method.
	 */
	public void setCharacterSetName(String val) {
		characterSetName = val;
	}

	/** Accessor get method for characterSetName. */
	public String getCharacterSetName() {
		return characterSetName;
	}

	/**
	 * Accessor set method for charColDeclLength. No validation provided in base
	 * method.
	 */
	public void setCharColDeclLength(Integer val) {
		charColDeclLength = val;
	}

	/** Accessor get method for charColDeclLength. */
	public Integer getCharColDeclLength() {
		return charColDeclLength;
	}

	/**
	 * Accessor set method for globalStats. No validation provided in base
	 * method.
	 */
	public void setGlobalStats(String val) {
		globalStats = val;
	}

	/** Accessor get method for globalStats. */
	public String getGlobalStats() {
		return globalStats;
	}

	/**
	 * Accessor set method for userStats. No validation provided in base method.
	 */
	public void setUserStats(String val) {
		userStats = val;
	}

	/** Accessor get method for userStats. */
	public String getUserStats() {
		return userStats;
	}

	/**
	 * Accessor set method for avgColLen. No validation provided in base method.
	 */
	public void setAvgColLen(Integer val) {
		avgColLen = val;
	}

	/** Accessor get method for avgColLen. */
	public Integer getAvgColLen() {
		return avgColLen;
	}

	/**
	 * Accessor set method for charLength. No validation provided in base
	 * method.
	 */
	public void setCharLength(Integer val) {
		charLength = val;
	}

	/** Accessor get method for charLength. */
	public Integer getCharLength() {
		return charLength;
	}

	/**
	 * Accessor set method for charUsed. No validation provided in base method.
	 */
	public void setCharUsed(String val) {
		charUsed = val;
	}

	/** Accessor get method for charUsed. */
	public String getCharUsed() {
		return charUsed;
	}

	/**
	 * Accessor set method for v80FmtImage. No validation provided in base
	 * method.
	 */
	public void setV80FmtImage(String val) {
		v80FmtImage = val;
	}

	/** Accessor get method for v80FmtImage. */
	public String getV80FmtImage() {
		return v80FmtImage;
	}

	/**
	 * Accessor set method for dataUpgraded. No validation provided in base
	 * method.
	 */
	public void setDataUpgraded(String val) {
		dataUpgraded = val;
	}

	/** Accessor get method for dataUpgraded. */
	public String getDataUpgraded() {
		return dataUpgraded;
	}

	/**
	 * Accessor set method for histogram. No validation provided in base method.
	 */
	public void setHistogram(String val) {
		histogram = val;
	}

	/** Accessor get method for histogram. */
	public String getHistogram() {
		return histogram;
	}

}
