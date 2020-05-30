--------------------------------------------------------
--  DDL for View JOB_LOG_VW
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "JOB_LOG_VW" ("JOB_LOG_ID", "SCHEMA_NAME", "PROCESS_NAME", "THREAD_NAME", "STATUS_MSG", "STATUS_TS", "END_TS", "SID", "SERIAL_NBR", "IGNORE_FLG", "MODULE_NAME", "CLASSNAME", "TRACEFILE_NAME", "ELAPSED_MILLIS") AS 
  select  
   job_log_id, 
   schema_name,         
   process_name,        
   thread_name,          
   status_msg,                               
   status_ts,                        
   end_ts,                               
   sid,                                      
   serial_nbr,                       
   ignore_flg,    
   module_name,    
   classname,             
   tracefile_name,                 
   end_ts - status_ts elapsed_millis 
from job_log
;
