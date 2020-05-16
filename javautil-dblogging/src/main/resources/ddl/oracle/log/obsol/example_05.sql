set serveroutput on
set echo on
spool example_05.lst

create or replace 
procedure example_05
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
    logname := to_char(job_log_id_seq.nextval) || '.log';
begin
   pllog.begin_log(p_log_name => logname p_log_level      => 3);
   example_04;
   example_05;
end;
/
