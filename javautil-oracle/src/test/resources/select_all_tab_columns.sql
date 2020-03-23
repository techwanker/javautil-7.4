var tab_owner varchar2(8)
var tab_table varchar2(8)
exec :tab_OWNER := 'SYS'
exec :tab_TABLE := 'DUAL'
select * from all_tab_columns where owner = :tab_OWNER and table_name = :tab_TABLE;
select * from all_tab_columns where owner = :tab_OWNER and table_name = :tab_TABLE;
