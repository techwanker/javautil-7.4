set serveroutput on 
declare
    long_msg clob := 'this is an absurdly long message, ' || 
                ' exceeding the length of the log_msg field ' ||
                ' The quick brown fox jumped over the lazy dog. '; 
    
    my_log_file_name varchar(4096); 
begin

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
