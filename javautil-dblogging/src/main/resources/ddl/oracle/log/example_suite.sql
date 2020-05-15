set serveroutput on
set echo on
spool example_suite.lst

create or replace 
procedure example_01
is
begin
    pllog.log('severe',1);
    pllog.log('warning',2);
    pllog.log('undefined',3);
    pllog.log('info',4);
    pllog.log('verbose',6);
    pllog.log('debug',7);
    pllog.log('trace',9);
end;
/

create or replace 
procedure example_04
is
    token varchar(32);
begin
    pllog.log('begins',4,$$PLS_UNIT,$$PLS_LINE);
    for lvl in 1 .. 9
    loop
    	pllog.log('lvl is ' || lvl,lvl,$$PLSQL_UNIT,$$PLSQL_LINE);
    end loop;
end;
/

create or replace 
procedure example_05
is
begin
    pllog.log('begins',4,$$PLS_UNIT,$$PLS_LINE);
    for lvl in 1 .. 9
    loop
    	pllog.log('lvl is ' || lvl,lvl,$$PLSQL_UNIT,$$PLSQL_LINE);
    end loop;
    pllog.end_job;
end;
/
--#<
declare
    token varchar(32);
    logname varchar(32) := to_char(job_log_id_seq.nextval) || '.log';
begin
   pllog.set_debug;
   pllog.begin_log(logfile_name => logname, p_log_level      => 3);
   example_01;
   example_04;
   example_05;
end;
/
exit;
