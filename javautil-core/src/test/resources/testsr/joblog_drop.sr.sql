--#<
set echo on
spool joblog_drop
--#>
drop sequence logger_settings_id_seq;
drop sequence job_log_id_seq;
drop sequence job_step_id_seq;
drop sequence job_msg_id_seq;
drop table logger_settings cascade constraints;
drop table job_log cascade constraints;
drop table job_step  cascade constraints;
drop table job_step_tracefile cascade constraints;
drop table job_step_tracefile_json cascade constraints; 
drop table job_msg cascade constraints;
drop view job_step_vw;