# Logger

Start with

# Simple logfile example

```
   declare
       job_token varchar(64);
   begin
        job_token := logger.begin_job('Simple example');
        pllogger.set_job_token(job_token);
        pllogger.log_msg('This is a simple example');
        logger.end_job;
   end;
```

**begin_job**

* process_name
* logger_level

This will:

1. Assign a job_token for future reference to this job

2. Insert a record into job_log (job_log_insert)
    
    * job_log_id
    * job_token
    * logfile_name
    * tracefile_name



