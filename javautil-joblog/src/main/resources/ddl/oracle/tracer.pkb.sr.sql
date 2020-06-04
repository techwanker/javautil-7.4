--# SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Default Trace File';
--#https://docs.oracle.com/cd/E18283_01/server.112/e17120/diag006.htm

--# https://docs.oracle.com/cd/A58617_01/server.804/a58246/otrace.htm
--#<
set echo on 
spool tracer.pkb.sr.lst
--#>
--/<
CREATE OR REPLACE PACKAGE BODY tracer
is
  g_debug                 boolean := false;
  g_job_msg_dir           varchar (32) := 'JOB_MSG_DIR';
  g_logfile_name          varchar(255);

  
  type logger_dtl_type is table of logger_dtl%rowtype index by varchar(64);
   
  logger_dtls logger_dtl_type;

  g_job_log job_log%rowtype;
    

  --%#Tracing
  --%<
  procedure set_trace (trace_level in pls_integer)
  --%>
  is
  begin
    DBMS_TRACE.set_plsql_trace (trace_level);
  end set_trace;

  --%<
  function get_my_tracefile_name 
  return varchar 
  --%>
  is
    tracefile_name varchar(4096);
    begin
        select value into tracefile_name
        from v$diag_info
        where name = 'Default Trace File';

        return tracefile_name;
    end get_my_tracefile_name;

  --%<
  function set_tracefile_identifier(job_nbr in number) 
  return varchar
  --%>
  is
    identifier varchar(32) := 'job_' || to_char(job_nbr);
  begin
    execute immediate 'alter session set tracefile_identifier = ''' || identifier || '''';
    return get_my_tracefile_name;
  end set_tracefile_identifier;

   --%# Job DML 

  procedure set_debug(debug boolean default true) 
  is
  begin
        g_debug := debug;
  end;


  procedure set_action ( action in varchar ) is
  begin
            dbms_application_info.set_action(substr(action, 1, 64)) ;
  end set_action ;

  procedure set_module ( module_name in varchar, action_name in varchar )
  is
  begin
            dbms_application_info.set_module(module_name, action_name) ;
  end set_module ;


  function get_tracefile(file_name in varchar)
  return clob is
  begin
    return get_file('UDUMP_DIR',file_name);
  end get_tracefile;


  procedure trace_step(step_name in varchar, job_step_id in number) is
    my_job_step_id varchar(9) := to_char(job_step_id);
    sql_text varchar(255) := 'select ''step_name: ''''' || step_name || 
               ''''' job_log_id: ' || g_job_log.job_log_id || 
              ' job_step_id: ' || my_job_step_id || ''' from dual';
  begin
    execute immediate sql_text;
  end trace_step;


  --
  --  Logger hdr and dtl
  --

end tracer;
--/>

--#<
/
show errors
--#>
