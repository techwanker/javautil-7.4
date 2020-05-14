   declare
       job_token varchar(64);
   begin
        job_token := logger.begin_job('Simple example');
        pllogger.set_job_token(job_token);
        pllogger.log_msg('This is a simple example');
        logger.end_job;
   end;
