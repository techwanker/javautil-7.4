set serveroutput on
set echo on
set timing on
create or replace procedure log_to_file_only_100000 is 
    
    my_log_file_name varchar(4096); 
begin
    my_log_file_name := pllogger.open_log_file('log_to_file_only.text');
    pllogger.set_filter_level(9); -- all messages should go to log file
    dbms_output.put_line('caller is anoymous');
    pllogger.info('anonymous',$$PLSQL_LINE,'begin loop');

    for i in 1..100000  
    loop
        pllogger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'i is ' || to_char(i));
    end loop; 
    pllogger.close_log_file();
exception when others
then
        -- a severe condition is not necessarily fatal
    pllogger.severe($$PLSQL_UINIT,$$PLSQL_LINE,sqlerrm);
    pllogger.close_log_file();
    raise;
end;
/
show errors

exec log_to_file_only_100000();
exit
