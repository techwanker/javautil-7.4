sudo su postgres  -c "createdb sales_reporting --owner $USER"
zcat sales_reporting.sql.gz | psql sales_reporting
