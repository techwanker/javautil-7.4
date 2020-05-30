set echo on
select * from all_directories;

declare
   my_clob         clob;
   my_bfile bfile := bfilename('UDUMP_DIR', 'dev18b_ora_10790.trc');
begin
   dbms_lob.CreateTemporary(my_clob, FALSE, dbms_lob.CALL);
   dbms_lob.FileOpen(my_bfile);
end;
/
declare
   my_clob         clob;
   my_bfile bfile := bfilename('UDUMP_DIR', '/common/oracle/diag/rdbms/dev18b/dev18b/trace/dev18b_p009_11999.trc');
begin
   dbms_lob.CreateTemporary(my_clob, FALSE, dbms_lob.CALL);
   dbms_lob.FileOpen(my_bfile);
end;
/
