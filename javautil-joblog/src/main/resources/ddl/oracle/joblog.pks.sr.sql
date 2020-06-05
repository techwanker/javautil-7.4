--#<
set echo on
--#>

--/<
CREATE OR REPLACE PACKAGE joblog
is
   

    function job_log_insert (
    	p_process_name in varchar,
        p_classname    in varchar,
        p_module_name  in varchar,
        p_status_msg   in varchar,
        p_thread_name  in varchar,
        p_trace_level  in pls_integer default logger.G_INFO
    )  return varchar ;

   function job_step_insert (
        p_job_token   in varchar,
        p_step_name   in varchar, 
        p_step_info   in varchar, 
        p_classname   in varchar,     
        p_start_ts    in timestamp,
        p_stacktrace  in varchar
   ) return number;

   procedure finish_step(stepid in number);

   procedure end_job(p_job_token in varchar);
   
   procedure abort_job(p_job_token in varchar, 
   p_stacktrace in varchar);
--cancer 2
end joblog;
--/>

--#<
/
show errors
--#>
