package com.dbexperts.oracle.DatabaseMetaData;

/**
  * Contains a temporal representation of the data persisted in a tuple of ALL_COL_COMMENTS
.
  * This code was generated by com.javautil.JavaGen.BaseRowGenerator on Mon Oct 20 16:50:49 EDT 2008
  */
public class ColumnComment
 {
   /** Container for the data persisted in OWNER. varchar2(30) */
     private String owner = null;

   /** Container for the data persisted in TABLE_NAME. varchar2(30) */
     private String tableName = null;

   /** Container for the data persisted in COLUMN_NAME. varchar2(30) */
     private String columnName = null;

   /** Container for the data persisted in COMMENTS. varchar2(4000) */
     private String comments = null;

    /** Accessor set method for owner.
     No validation provided in base method. */
    public void setOwner(String val) {
        owner = val;
    }

    /** Accessor get method for owner. */
    public String getOwner() {
        return owner;
    }


    /** Accessor set method for tableName.
     No validation provided in base method. */
    public void setTableName(String val) {
        tableName = val;
    }

    /** Accessor get method for tableName. */
    public String getTableName() {
        return tableName;
    }


    /** Accessor set method for columnName.
     No validation provided in base method. */
    public void setColumnName(String val) {
        columnName = val;
    }

    /** Accessor get method for columnName. */
    public String getColumnName() {
        return columnName;
    }


    /** Accessor set method for comments.
     No validation provided in base method. */
    public void setComments(String val) {
        comments = val;
    }

    /** Accessor get method for comments. */
    public String getComments() {
        return comments;
    }


	
}
