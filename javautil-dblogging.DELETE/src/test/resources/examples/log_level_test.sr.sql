set echo on
set serveroutput on;

begin
    plpllogger.create_set ('demo1',plpllogger.info);
    plpllogger.set_level('demo1','plpllogger_test_1',plpllogger.warn );
    plpllogger.set_level('demo1','plpllogger_test_2',plpllogger.info);
    commit;
end;
/

declare
   hdr pllogger_hdr%rowtype;
   dtl pllogger_dtl%rowtype;
   my_set_nm varchar(64) := 'demo1';
   unit_test_exception exception; 
begin

   select * into hd from pllogger_hdr 
   where pllogger_set_nm = my_set_nm;

   select count(*) into dtl_cnt
   from pllogger_dtl
   where pllogger_dtl.pllogger_hdr_id = (   
      select pllogger_hdr_id 
      from pllogger_hdr 
      where set_nm = my_set_nm
   );

   if dtl_cnt !=2 then
     raise unit_test_exception;
   end if;

end;
/
--#<
set serveroutput on
--#>
create or replace procedure pllogger_test_1
is 
    logfile_name varchar(255);
    open_file_name varchar(255);
    my_log_file_name varchar(255) := 'pllogger_test_1';
begin
    pllogger.use_logger_set('demo1');
	logfile_name := logger.begin_job('log_level_test_01');
    open_file_name := open_log_file(p_file_name  >= my_log_file_name,
                               p_headers => true,
                               p_job_log_id => null);
    dbms_output('logging to '|| open_file_name);
                               
	pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,'begin loop');
	dbms_output.put_line('entering loop');
	dbms_output.put_line('about to emit warn');
	pllogger.warning($$PLSQL_UNIT,$$PLSQL_LINE,'5 - warn' );
	pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,'5 - info');
	pllogger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'5 - fine');
	pllogger.finer($$PLSQL_UNIT,$$PLSQL_LINE,'5 - finer');
	--pllogger.set_filter_level(9);
	pllogger.warning($$PLSQL_UNIT,$$PLSQL_LINE,'9 - warn' );
	pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,'9 - info');
	pllogger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'9 - fine');
	pllogger.finer($$PLSQL_UNIT,$$PLSQL_LINE,'9 - finer');
	dbms_output.put_line('ending job');
	logger.end_job;
exception when others
then
	pllogger.severe($$PLSQL_UINIT,$$PLSQL_LINE,sqlerrm);
	logger.abort_job;
raise;
end;
--#<
/
--#>
exec pllogger_test_1;
