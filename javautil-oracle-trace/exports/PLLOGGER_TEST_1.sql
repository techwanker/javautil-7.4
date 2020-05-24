--------------------------------------------------------
--  DDL for Procedure PLLOGGER_TEST_1
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "PLLOGGER_TEST_1" 
is 
    logfile_name varchar(255);
begin
    pllogger.use_logger_set('demo1');
	logfile_name := logger.begin_job('log_level_test_01');
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
