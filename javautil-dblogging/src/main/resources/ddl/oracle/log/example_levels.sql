set serveroutput on
set echo on
spool example_levels.lst

create or replace procedure 
example_levels is
begin
    pllog.log('severe',1);
    pllog.log('warning',2,$$plsql_unit);
    pllog.log('undefined',3,$$plsql_unit);
    pllog.log('info',4);
    pllog.log('verbose',6);
    pllog.log('debug',7);
    pllog.log('trace',9);
end;
/
--#<
begin
   example_levels;
end;
/

