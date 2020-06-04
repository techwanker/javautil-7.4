--#<
set echo on
spool joblog
--#>

--/<
CREATE OR REPLACE PACKAGE BODY joblog
is
     --g_job_log_id            pls_integer;
     --g_job_step_id           pls_integer;

 

    procedure job_log_insert (
    	p_process_name in varchar,
        p_classname    in varchar,
        p_module_name  in varchar,
        p_status_msg   in varchar,
        p_thread_name  in varchar,
        p_trace_level  in pls_integer default logger.G_INFO
    ) is
        pragma autonomous_transaction ;                
    	my_job_log_id number :=  job_log_id_seq.nextval; 
   --- 	my_schem  varchar(64) :=  sys_context('userenv','current_schema') 
    begin  
        insert into job_log (
          job_log_id,   process_name,   thread_name,        
          status_msg,   status_ts,      module_name,
          classname
        ) values (
          my_job_log_id,  p_process_name,  p_thread_name, 
          p_status_msg,  systimestamp,    p_module_name,
          p_classname
        );
        commit;
   end job_log_insert;
   
   	-- TODO should use token
   function job_step_insert (
        p_job_log_id  in pls_integer, 
        p_step_name   in varchar, 
        p_step_info   in varchar, 
        p_classname   in varchar,     
        p_start_ts    in timestamp,
        p_stacktrace  in varchar
   ) return number
   is 
            pragma autonomous_transaction ;             
		my_job_step_id number;
   begin
      insert into job_step (
        job_step_id,   job_log_id, step_name, step_info, 
        classname,     start_ts,   stacktrace
      ) values (
        job_step_id_seq.nextval, p_job_log_id, p_step_name, p_step_info, 
        p_classname,   p_start_ts,   p_stacktrace
      ) returning job_step_id into my_job_step_id;
      return my_job_step_id;
      
   end job_step_insert;
   
    procedure finish_step(stepid in number) is 
        pragma autonomous_transaction ;   
    begin
       update job_step 
       set end_ts = systimestamp
       where job_step_id = stepid;
    end finish_step;


   procedure end_job(p_job_token in varchar, p_elapsed_milliseconds in pls_integer)
   --::* update job_log.status_id to 'C' and status_msg to 'DONE'
   --::>
   is
       PRAGMA AUTONOMOUS_TRANSACTION;
   begin
       update job_log
       set
              status_msg = 'DONE',
              status_ts = SYSDATE,
              elapsed_millis = p_elapsed_milliseconds
       where job_token = p_job_token;

      commit;
   --   logger.set_action('end_job complete');
   end end_job;
   
   --::<
   procedure abort_job(p_job_token in varchar, p_elapsed_milliseconds in pls_integer,
   	p_stacktrace in varchar)
   --::* status_id = 'I'
   --::* status_msg = 'ABORT'
   --::>
   is
      PRAGMA AUTONOMOUS_TRANSACTION;

      
   begin
      update job_log
      set 
          status_msg = 'ABORT',
          status_ts = current_timestamp,
          abort_stacktrace = p_stacktrace,
          elapsed_millis = p_elapsed_milliseconds
       where job_token = p_job_token;

      COMMIT;
      logger.set_action('abort_job complete');
   end abort_job;



end joblog;
--/>

--#<
/
show errors
--#>
