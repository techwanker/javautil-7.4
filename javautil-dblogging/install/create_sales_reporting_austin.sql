--#https://docs.oracle.com/en/database/oracle/oracle-database/12.2/admin/creating-and-removing-pdbs-with-sql-plus.html#GUID-05702CEB-A43C-452C-8081-4CA68DDA8007

spool create_sales_reporrting_austin
set echo on

CREATE PLUGGABLE DATABASE sales_reporting ADMIN USER sr IDENTIFIED BY tutorial
  STORAGE (MAXSIZE 2G)
  DEFAULT TABLESPACE sales_reporting
    DATAFILE '/common/oracle/oradata/DEV18B/sales_reporting/sales_reporting.dbf' 
       SIZE 32M AUTOEXTEND ON
  FILE_NAME_CONVERT = ('/pdbseed/', '/sales_reporting/');

alter pluggable database sales_reporting open;
alter pluggable database sales_reporting save state;

alter session set container = sales_reporting;

