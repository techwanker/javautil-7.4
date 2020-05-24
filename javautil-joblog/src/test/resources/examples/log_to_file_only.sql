set serveroutput on 
declare
       long_msg clob := 'this is an absurdly long message, ' || 
                ' exceeding the length of the log_msg field ' ||
                ' The quick brown fox jumped over the lazy dog. '; 
    
    my_log_file_name varchar(4096); 
begin
    my_log_file_name := pllogger.open_log_file('log_to_file_only.text');
    pllogger.set_filter_level(9); -- all messages should go to log file
    dbms_output.put_line('caller is anoymous');
    pllogger.info('anonymous',$$PLSQL_LINE,'begin loop');
    pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,long_msg); 

    for i in 1..10  
    loop
        pllogger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'i is ' || to_char(i));
    end loop; 
exception when others
then
        -- a severe condition is not necessarily fatal
    pllogger.severe($$PLSQL_UINIT,$$PLSQL_LINE,sqlerrm);
    pllogger.close_log_file();
    raise;
end;
/
