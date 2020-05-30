set serveroutput on
create or replace procedure pllogger_test_2
is 
    tracefile_name varchar(255);
    open_file_name varchar(255);
    my_log_file_name varchar(255) := 'pllogger_test_2';
begin
    dbms_output.put_line('plogger_test_1 begins');
    pllogger.use_logger_set('demo1');
	tracefile_name := logger.begin_job('log_level_test_01');
    dbms_output.put_line('before open_log_file ' || my_log_file_name);
    open_file_name := open_log_file(p_file_name  >= my_log_file_name,
                               p_headers => true,
                               p_job_log_id => null);
    dbms_output('open_log_file_returned '|| open_file_name);
	pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,'begin loop');
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
