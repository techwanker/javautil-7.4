set serveroutput on
set echo on
create or replace procedure log_to_file_only is 
       long_msg clob := 'this is an absurdly long message, ' || 
                ' exceeding the length of the log_msg field ' ||
                ' this should be inserted into the log_msg_clob column.  ' || 
                ' This message is part of ' ||
                ' a unit test of from sample_job_02 of the logging package. ' || 
                '  I am running out of ' ||
                ' interesting stuff to say so I will just write meaningless ' ||
                ' stuff for a little while. ' ||
                ' The quick brown fox jumped over the lazy dog. '; 
    
    my_log_file_name varchar(4096); 
begin
    my_log_file_name := pllogger.open_log_file('log_to_file_only.text');
    pllogger.set_filter_level(9); -- all messages should go to log file
    dbms_output.put_line('caller is anoymous');
    pllogger.info('anonymous',$$PLSQL_LINE,'begin loop');
    pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,long_msg); 

    for i in 1..3  
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

exec log_to_file_only();
