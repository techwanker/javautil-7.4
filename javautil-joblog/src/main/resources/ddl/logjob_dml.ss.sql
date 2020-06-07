--@name job_log_insert
-- jvautil-logjob/src/main/resojrces/ddl/logjob_dml.ss.sql
insert into job_log (    
	job_log_id,     job_token, 
	process_name,   thread_name,
	status_msg,     start_ts,  
	classname,      module_name
) values (
	:job_log_id,   :job_token, 
	:process_name, :thread_name,
   :status_msg,    :start_ts,
	:classname,    :module_name
);


--@name job_step_insert
/*
    job_step_id             number(9),
    job_log_id 	            number(9),
    step_name               varchar(64),
    classname               varchar(256),
    step_info               varchar(2000),
    start_ts    	    timestamp(9),
    end_ts  		    timestamp(9),
    dbstats                 clob,
    step_info_json          clob,
    tracefile_name       varchar(255),
    --cursor_info_run_id      number(9) references cursor_info_run,
    stacktrace              varchar(4000),
    sid                     number(8),
    serial#                
    instance_name      */
    
insert into job_step (
        job_step_id,   job_log_id, step_name, 
        classname,     step_info, 
        start_ts,      stacktrace,
        tracefile_name, sid, serial#
) values (
        :job_step_id, :job_log_id, :step_name,:step_info, 
        :classname,   :start_ts,   :stacktrace,
        :tracefile_name, :sid, :serial_nbr
);
--@name end_step
update job_step 
set end_ts = :end_ts
where job_step_id = :job_step_id;
--@name job_msg_insert
insert into job_msg (
	job_msg_id,  job_log_id, log_msg_id,                log_msg,
	log_msg_clob,       log_msg_ts,           elapsed_time_MILLISECONDS, log_seq_nbr,
	caller_name,        line_nbr,             call_stack,                log_level
) values (
	:job_msg_id, :job_log_id, :log_msg_id,                :log_msg,
	:log_msg_clob,      :log_msg_ts,           :elapsed_time_milliseconds, :log_seq_nbr,
	:caller_name,       :line_nbr,             :call_stack,                :log_level
);
--@name select_job_log_by_id
select * from job_log
where job_log_id = :job_log_id;
--@name abort_job
UPDATE job_log
SET 
    status_msg = 'ABORT',
    end_ts = :end_ts
where job_log_id = :job_log_id;
--@name end_job
UPDATE job_log
SET   
       status_msg = 'DONE',
       end_ts    = :end_ts
where job_log_id = :job_log_id;
