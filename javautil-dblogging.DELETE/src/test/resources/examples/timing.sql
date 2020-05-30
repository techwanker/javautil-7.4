
set timing on
declare
   my_handle utl_file.file_type;
   i number;
begin
   for i in 1 .. 20000
   loop
       my_handle := utl_file.fopen('JOB_MSG_DIR','test','a');
       utl_file.put_line(my_handle,'test message');
       utl_file.fclose(my_handle);
    end loop;
end;
/
