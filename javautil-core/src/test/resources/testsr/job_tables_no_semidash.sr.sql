--#<
-- TODO check in sqlsplitter
set echo on
spool job_tables
--#>
create sequence logger_settings_id_seq
;--
create table logger_settings (    
    logger_settings_id number(9),
    logger_token            varchar(64),
    process_name         varchar(128),
    classname            varchar(255),
    thread_name          varchar(128),
    module_name          varchar(64),
    status_msg           varchar(256),
    trace_level          number(2),
    directory_name       varchar(128),
    logfile_name         varchar(64),
    abort_stacktrace     clob,
    log_level            number(1),
    msg_lvl              number(1),
    constraint logger_settings_pk primary key (logger_settings_id)
   ) 
;--

create sequence job_log_id_seq
;--

    job_token            varchar(64),
    process_name         varchar(128),
    thread_name          varchar(128),
    module_name          varchar(64),
    status_msg           varchar(256),
    start_ts    	     timestamp(9),
    end_ts               timestamp(9),
    elapsed_millis       number(9),
    ignore_flg           varchar(1) default 'N' not null,
    classname            varchar(255),
    trace_level          number(2),
    abort_stacktrace     clob,
    msg_lvl              number(1),
    check ( ignore_flg in ('Y', 'N')) ,
    constraint job_log_pk primary key (job_log_id)
   )
;-- 

create sequence job_step_id_seq
;--

alter table job_log add constraint job_log_token_uq unique (job_token)
;--

create table job_step (   
    job_step_id             number(9),
    job_log_id 	            number(9),
    step_name               varchar(64),
    classname               varchar(256),
    step_info               varchar(2000),
    start_ts    	    timestamp(9),
    end_ts  		    timestamp(9),
    tracefile_name       varchar(255),
    --cursor_info_run_id      number(9) references cursor_info_run,
    stacktrace              varchar(4000),
    sid                     number(8),
    serial_nbr                 number(8),
    instance_name            varchar(16),
    constraint job_step_pk primary key (job_step_id),
    constraint step_status_fk
	foreign key (job_log_id) references job_log
) -- TOEO should allow closing ;
;--

create table job_step_tracefile (   
    job_step_id             number(9) primary key,
    tracefile_data          clob
    )
;--
    
alter table job_step_tracefile
add constraint jst_js_fk 
foreign key (job_step_id) references job_step
;--

create table job_step_tracefile_json (   
    job_step_id             number(9) primary key,
    tracefile_jsoh          clob
    )
;--
    
alter table job_step_tracefile_json
add constraint jstj_js_fk 
foreign key (job_step_id) references job_step
;--

create sequence job_msg_id_seq
;--

create table job_msg (    
    job_msg_id 	      number(9) not null,
    job_log_id 	      number(9) not null,
    job_step_id       number(9),  
    log_msg_id 		  varchar2(8) not null,
    log_msg 		  varchar2(256),
    log_msg_clob      clob,
    log_msg_ts 		  timestamp (9),
    elapsed_time_milliseconds number(9),
    log_seq_nbr 	  number(18,0) not null,
    caller_name       varchar2(100),
    line_nbr 		  number(5,0),
    call_stack 		  clob,
    log_level 		  number(2,0),
    constraint job_msg_pk primary key (job_log_id, log_seq_nbr)
)
;--



alter table job_msg 
add constraint job_msg_job_log_fk 
foreign key (job_log_id) 
references job_log(job_log_id)
;--

alter table job_msg 
add constraint job_fk 
foreign key (job_step_id) 
references job_step(job_step_id)
;--

create or replace view job_step_vw as
select
    job_step_id,
    job_log_id,
	step_name,
	classname ,
	step_info,
    start_ts,
    end_ts ,
    end_ts - start_ts elapsed_millis
from job_step
;--

create or replace view job_log_vw as 
select  
   job_log_id, 
   process_name,        
   thread_name,          
   status_msg,                               
   start_ts,                        
   end_ts,                               
   ignore_flg,    
   module_name,    
   classname,             
   end_ts - start_ts elapsed_millis 
from job_log
;--

