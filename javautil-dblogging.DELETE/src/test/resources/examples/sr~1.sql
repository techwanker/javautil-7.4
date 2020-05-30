set serveroutput on;
   declare
       job_token varchar(64);
       
   begin
        job_token := logger.begin_job('Simple example');
        pllogger.set_job_token(job_token);
        pllogger.log_msg('This is a simple example');
        pllogger.log_msg('This is a second line');
     --   pllogger.close_log_file;
        logger.end_job;
   end;

declare
   my_handle utl_file.file_type;
   i number;
begin
   for i in 1 .. 1000
   loop
       my_handle := utl_file.fopen('JOB_MSG_DIR','test','a');
       utl_file.put_line(my_handle,'test message');
       utl_file.fclose(my_handle);
    end loop;
end;