set serveroutput on
set echo on
spool example_01.lst
begin
    log.begin_job(p_process_name => 'example 01',
                  logfile_name   => 'example_01.log');
    log.log('one message');
    log.end_job;
exception when others
then
	pllogger.severe($$PLSQL_UINIT,$$PLSQL_LINE,sqlerrm);
	logger.abort_job;
raise;
end;
--#<
