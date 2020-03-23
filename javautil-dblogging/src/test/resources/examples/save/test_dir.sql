set echo on
set serveroutput on
select * from all_directories where directory_name = 'JOB_MSG_DIR';

declare
    g_file_handle utl_file.file_type;
begin
    dbms_output.put_line('about to open');
    g_file_handle := utl_file.fopen('JOB_MSG_DIR','testfile','w');
    dbms_output.put_line('about to write');
    utl_file.put_line(g_file_handle,'text');
    utl_file.fclose(g_file_handle);
end;
/
