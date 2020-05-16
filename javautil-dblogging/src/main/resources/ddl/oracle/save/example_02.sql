   declare
       job_token varchar(64);
   begin
        job_token := logger.begin_job('example2');
        pllogger.set_job_token(job_token);
        pllogger.log_msg('This is a simple example');
        pllogger.log_msg('With Unit',6,$$PLSQL_UNIT);
        logger.end_job;
   end;
