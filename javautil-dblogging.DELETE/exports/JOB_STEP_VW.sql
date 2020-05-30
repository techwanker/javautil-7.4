--------------------------------------------------------
--  DDL for View JOB_STEP_VW
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "JOB_STEP_VW" ("JOB_STEP_ID", "JOB_LOG_ID", "STEP_NAME", "CLASSNAME", "STEP_INFO", "START_TS", "END_TS", "ELAPSED_MILLIS") AS 
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
;
