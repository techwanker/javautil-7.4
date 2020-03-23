set -x 
## cp sales_reporting.sql.gz /tmp
## cd /tmp && gunzip sales_reporting.sql
cd ../exports
pg_restore --dbname=sales_reporting sales_reporting.pg_dump
