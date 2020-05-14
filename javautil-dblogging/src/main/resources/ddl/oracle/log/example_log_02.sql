set serveroutput on
set echo on
spool example_02.lst
begin
    pllog.begin_log(p_process_name => 'example 02',
                  logfile_name   => 'example_02.log');
    pllog.log('one message');
end;
--#<
