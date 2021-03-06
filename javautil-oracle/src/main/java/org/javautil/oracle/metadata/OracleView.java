package org.javautil.oracle.metadata;

/**
 * 
 * @author jjs
 * 
 */
public class OracleView {

	/** Container for the data persisted in OWNER. varchar2(30) */
	private String owner = null;

	/** Container for the data persisted in VIEW_NAME. varchar2(30) */
	private String viewName = null;

	/** Container for the data persisted in TEXT_LENGTH. number(22) */
	private Long textLength = null;

	/** Container for the data persisted in TEXT. varchar2(4000) */
	private String text = null;

	/** Container for the data persisted in TYPE_TEXT_LENGTH. number(22) */
	private Long typeTextLength = null;

	/** Container for the data persisted in TYPE_TEXT. varchar2(4000) */
	private String typeText = null;

	/** Container for the data persisted in OID_TEXT_LENGTH. number(22) */
	private Long oidTextLength = null;

	/** Container for the data persisted in OID_TEXT. varchar2(4000) */
	private String oidText = null;

	/** Container for the data persisted in VIEW_TYPE_OWNER. varchar2(30) */
	private String viewTypeOwner = null;

	/** Container for the data persisted in VIEW_TYPE. varchar2(30) */
	private String viewType = null;

	/** Container for the data persisted in SUPERVIEW_NAME. varchar2(30) */
	private String superviewName = null;

	/**
	 * Accessor set method for owner. No validation provided in base method.
	 */
	public void setOwner(final String val) {
		owner = val;
	}

	/** Accessor get method for owner. */
	public String getOwner() {
		return owner;
	}

	/**
	 * Accessor set method for viewName. No validation provided in base method.
	 */
	public void setViewName(final String val) {
		viewName = val;
	}

	/** Accessor get method for viewName. */
	public String getViewName() {
		return viewName;
	}

	/**
	 * Accessor set method for textLength. No validation provided in base
	 * method.
	 */
	public void setTextLength(final Long val) {
		textLength = val;
	}

	/** Accessor get method for textLength. */
	public Long getTextLength() {
		return textLength;
	}

	/**
	 * Accessor set method for text. No validation provided in base method.
	 */
	public void setText(final String val) {
		text = val;
	}

	/** Accessor get method for text. */
	public String getText() {
		return text;
	}

	/**
	 * Accessor set method for typeTextLength. No validation provided in base
	 * method.
	 */
	public void setTypeTextLength(final Long val) {
		typeTextLength = val;
	}

	/** Accessor get method for typeTextLength. */
	public Long getTypeTextLength() {
		return typeTextLength;
	}

	/**
	 * Accessor set method for typeText. No validation provided in base method.
	 */
	public void setTypeText(final String val) {
		typeText = val;
	}

	/** Accessor get method for typeText. */
	public String getTypeText() {
		return typeText;
	}

	/**
	 * Accessor set method for oidTextLength. No validation provided in base
	 * method.
	 */
	public void setOidTextLength(final Long val) {
		oidTextLength = val;
	}

	/** Accessor get method for oidTextLength. */
	public Long getOidTextLength() {
		return oidTextLength;
	}

	/**
	 * Accessor set method for oidText. No validation provided in base method.
	 */
	public void setOidText(final String val) {
		oidText = val;
	}

	/** Accessor get method for oidText. */
	public String getOidText() {
		return oidText;
	}

	/**
	 * Accessor set method for viewTypeOwner. No validation provided in base
	 * method.
	 */
	public void setViewTypeOwner(final String val) {
		viewTypeOwner = val;
	}

	/** Accessor get method for viewTypeOwner. */
	public String getViewTypeOwner() {
		return viewTypeOwner;
	}

	/**
	 * Accessor set method for viewType. No validation provided in base method.
	 */
	public void setViewType(final String val) {
		viewType = val;
	}

	/** Accessor get method for viewType. */
	public String getViewType() {
		return viewType;
	}

	/**
	 * Accessor set method for superviewName. No validation provided in base
	 * method.
	 */
	public void setSuperviewName(final String val) {
		superviewName = val;
	}

	/** Accessor get method for superviewName. */
	public String getSuperviewName() {
		return superviewName;
	}

}
