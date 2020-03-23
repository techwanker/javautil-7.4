# SqlStatement
  SqlStatement simplifies SQL by obviating the need to deal with JDBC.

## Overview
- Support named bind variables for JDBC implementations that don't
- Queries may be returned in numerous formats
  - NameValue a single row returned as a map
  - ListOfNameValue an array list of maps
  - ListOfLists
  - A dataset
- Upon an error the SQL is displayed along with the error before
         the SQLexception is reraised
- sequence abstraction
- Metadata
 - name
 - description of the statement
 - narrative, a long description of the statement and its purpose


## Examples

### Fetch

.. code::  

     SqlStatement ss = new SqlStatement(connection,
       "select distinct cust_id from etl_sale where etl_file_id = :etl_file_id";
     Bind binds = new Binds();
     binds.put("etl_file_id", 1l);
     ListOfNameValue rows = ss.getListOfNameValue(binds);
 