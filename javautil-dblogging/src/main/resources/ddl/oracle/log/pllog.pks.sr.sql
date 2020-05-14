spool pllog.pks.lst
set echo on
create or replace PACKAGE pllog AS
    G_SEVERE       CONSTANT PLS_INTEGER := 1 ;
    G_WARNING      CONSTANT PLS_INTEGER := 2 ;
    G_INFO         CONSTANT PLS_INTEGER := 4 ;
    G_SNAP         CONSTANT PLS_INTEGER := 5 ;
    G_ENTERING     CONSTANT PLS_INTEGER := 6 ;
    G_EXITING      CONSTANT PLS_INTEGER := 6 ;
    G_FINE         CONSTANT PLS_INTEGER := 7 ;
    G_FINER        CONSTANT PLS_INTEGER := 8 ;
    G_FINEST       CONSTANT PLS_INTEGER := 9 ;
    G_NONE         CONSTANT PLS_INTEGER := 10 ;

    function format_time(p_timestamp in timestamp) 
    return varchar;

--    procedure set_logger_level (p_level in pls_integer);

    function get_new_job_log_id 
    return number;


   procedure begin_log ( 
        logfile_name   in varchar,
        logfile_directory in varchar default 'JOB_MSG_DIR',
        p_process_name in varchar default null,
        p_log_set      in varchar default null,
        p_classname    in varchar default null,
        p_module_name  in varchar default null,
        p_status_msg   in varchar default null,
        p_thread_name  in varchar default null,
        p_log_level    in pls_integer default G_INFO,
        p_trace_level  in pls_integer default G_INFO);


   FUNCTION begin_job ( 
        p_process_name in varchar,
        p_log_set      in varchar default null,
        p_classname    in varchar default null,
        p_module_name  in varchar default null,
        p_status_msg   in varchar default null,
        p_thread_name  in varchar default null,
        logfile_name   in varchar default null,
        logfile_directory in varchar default 'JOB_MSG_DIR',
        p_log_level    in pls_integer default G_INFO,
        p_trace_level  in pls_integer default G_INFO)
        return varchar;

    procedure end_job;

    procedure abort_job(p_stacktrace in varchar default null);

    procedure set_action (p_action in        varchar) ;

    procedure set_module (
        p_module_name in        varchar,
        p_action_name in   varchar
    );

    function get_my_tracefile 
    return clob ;

    function get_directory_path 
    return varchar;

    function get_tracefile(p_file_name in varchar) 
    return clob;

    function get_my_tracefile_name 
    return varchar;

    function basename (p_full_path in varchar,
                       p_suffix    in varchar default null,
                       p_separator in char default '/') 
    return varchar;

    procedure prepare_connection;

    procedure trace_step(p_step_name in varchar, p_job_step_id in number);

    function set_tracefile_identifier(p_job_nbr in number) 
    return varchar;


  procedure log (
      p_log_msg      in   varchar,
      p_log_level    in   pls_integer default g_info,
      p_job_log_id   in   pls_integer default null,
      p_job_msg_id   in   pls_integer default null,
      p_elapsed_time in   INTERVAL DAY TO SECOND DEFAULT NULL, -- TODO not recorded at this time
      p_caller_name  in   varchar default null,
      p_line_number  in   pls_integer default null,
      p_dump_stack   in   boolean default false
   );

END pllog ;
/
show errors
