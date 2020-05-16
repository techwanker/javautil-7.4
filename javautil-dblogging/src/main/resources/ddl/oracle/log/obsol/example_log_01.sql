set serveroutput on
set echo on
spool example_01.lst
begin
    pllog.begin_log(p_process_name => 'example 01',
                  logfile_name   => 'example_01.log');
    pllog.log('one message');
end;
--#<
