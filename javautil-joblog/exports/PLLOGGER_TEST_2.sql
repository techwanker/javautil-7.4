--------------------------------------------------------
--  DDL for Procedure PLLOGGER_TEST_2
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "PLLOGGER_TEST_2" 
is 
    tracefile_name varchar(255);
    open_file_name varchar(255);
    my_log_file_name varchar(255) := 'pllogger_test_2';
    my_job_log_id number:= logger.get_new_job_log_id;
    job_token varchar(64);
begin
    dbms_output.put_line('plogger_test_1 begins');
    pllogger.use_logger_set('demo1');
	job_token := logger.begin_job(p_process_name => 'log_level_test_01');
    pllogger.set_job_token(job_token);
    dbms_output.put_line('before open_log_file ' || my_log_file_name);
    open_file_name := pllogger.open_log_file(my_log_file_name,
                               p_headers => true);
    dbms_output.put_line('open_log_file_returned '|| open_file_name);
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
    pllogger.close_log_file;
exception when others
then
	pllogger.severe($$PLSQL_UINIT,$$PLSQL_LINE,sqlerrm);
	logger.abort_job;
raise;
end;
--#<

/
