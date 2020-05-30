--------------------------------------------------------
--  DDL for Package LOGGER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE "LOGGER" AS
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

    procedure set_filter_level (p_level in pls_integer);

    function get_new_job_log_id 
    return number;

    function begin_job (
        p_process_name in varchar,
        p_logger_set   in varchar default null,
        p_classname    in varchar default null,
        p_module_name  in varchar default null,
        p_status_msg   in varchar default null,
        p_thread_name  in varchar default null,
        p_logger_level in pls_integer default G_INFO,
        p_trace_level  in pls_integer default G_INFO)
    return varchar;

    procedure end_job;

    procedure abort_job(p_stacktrace in varchar default null);

    procedure set_action (p_action IN        VARCHAR2) ;

    procedure set_module (
        p_module_name IN        VARCHAR,
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

    function basename (p_full_path in varchar2,
                       p_suffix    in varchar2 default null,
                       p_separator in char default '/') 
    return varchar2;

    procedure prepare_connection;

    procedure trace_step(p_step_name in varchar, p_job_step_id in number);

    function set_tracefile_identifier(p_job_nbr in number) 
    return varchar;

END logger ;

/
