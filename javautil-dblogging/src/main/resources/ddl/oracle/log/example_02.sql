set serveroutput on
set echo on
spool example_02.lst
begin
    pllog.begin_log(p_process_name => 'example 02',
                  logfile_name   => 'example_02.log',
                  p_log_level      => 3);
    pllog.log('severe',1);
    pllog.log('warning',2);
    pllog.log('undefined',3);
    pllog.log('info',4);
    pllog.log('verbose',6);
    pllog.log('debug',7);
    pllog.log('trace',9);
end;
/
--#<
