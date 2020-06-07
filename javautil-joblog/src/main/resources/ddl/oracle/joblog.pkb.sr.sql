--#<
set echo on
spool joblog
--#>

--/<
CREATE OR REPLACE PACKAGE BODY joblog
is

    function job_log_insert (
    	p_process_name in varchar,
        p_classname    in varchar,
        p_module_name  in varchar,
        p_status_msg   in varchar,
        p_thread_name  in varchar,
        p_trace_level  in pls_integer default logger.G_INFO
    )  return varchar is
        pragma autonomous_transaction ;                
    	my_job_log_id number :=  job_log_id_seq.nextval; 
    	my_job_token varchar(32) := logger.get_job_token;
   --- 	my_schem  varchar(64) :=  sys_context('userenv','current_schema') 
    begin  
        insert into job_log (
          job_log_id,    job_token,
          process_name,   thread_name,        
          status_msg,   start_ts,      module_name,
          classname
        ) values (
          my_job_log_id,  my_job_token,
          p_process_name,  p_thread_name, 
          p_status_msg,  systimestamp,    p_module_name,
          p_classname
        );
        commit;
        return my_job_token;
   end job_log_insert;
   
   function job_step_insert (
        p_job_token   in varchar,
        p_step_name   in varchar, 
        p_step_info   in varchar, 
        p_classname   in varchar,     
        p_start_ts    in timestamp,
        p_stacktrace  in varchar
   ) return number
   is 
        pragma autonomous_transaction ;             
		my_job_step_id number;
		job_log_rec job_log%rowtype;
		
   begin
       select * 
       into   job_log_rec
       from   job_log
       where  job_token = p_job_token;
	  
      insert into job_step (
        job_step_id,   job_log_id, step_name, step_info, 
        classname,     start_ts,   stacktrace
      ) values (
        job_step_id_seq.nextval, job_log_rec.job_log_id, p_step_name, p_step_info, 
        p_classname,   p_start_ts,   p_stacktrace
      ) returning job_step_id into my_job_step_id;
      commit;
      return my_job_step_id;
   end job_step_insert;
   
    procedure finish_step(stepid in number) is 
        pragma autonomous_transaction ;   
    begin
       update job_step 
       set end_ts = systimestamp
       where job_step_id = stepid;
       commit;
    end finish_step;


   procedure end_job(p_job_token in varchar)
   --::* update job_log.status_id to 'C' and status_msg to 'DONE'
   --::>
   is
       PRAGMA AUTONOMOUS_TRANSACTION;
   begin
       update job_log
       set
              status_msg = 'DONE'
       where job_token = p_job_token;

      commit;
   --   logger.set_action('end_job complete');
   end end_job;
   
   --::<
   procedure abort_job(p_job_token in varchar, 
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
          abort_stacktrace = p_stacktrace
       where job_token = p_job_token;

      COMMIT;
      logger.set_action('abort_job complete');
   end abort_job;

-- cancer 3
end joblog;
--/>

--#<
/
show errors
--#>
