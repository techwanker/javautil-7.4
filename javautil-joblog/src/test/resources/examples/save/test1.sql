--#<
set serveroutput on
--#>
declare
    my_tracefile_name varchar(4096);
    my_logfile_name  varchar(4096);
begin
     DBMS_MONITOR.session_trace_enable(waits=>TRUE, binds=>FALSE);
    my_logfile_name := logger.open_log_file('test01');
	my_tracefile_name := logger.begin_job('sample_job_01');
	logger.info($$PLSQL_UNIT,$$PLSQL_LINE,'begin loop');
        -- all messages should go to log file
	logger.set_filter_level(9);
	dbms_output.put_line('entering loop');
	for i in 1..2  
	loop
		dbms_output.put_line('about to emit into');
		logger.info($$PLSQL_UNIT,$$PLSQL_LINE,'i is ' || to_char(i));
		dbms_output.put_line('emitted info');
		logger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'i is ' || to_char(i));
	end loop; 
	dbms_output.put_line('ending job');
	logger.end_job;
exception when others
then
	pllogger.severe($$PLSQL_UNIT,$$PLSQL_LINE,sqlerrm);
	logger.abort_job(dbms_utility.format_error_backtrace);
raise;
end;
--#<
/
--#>
