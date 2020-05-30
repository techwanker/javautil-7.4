--------------------------------------------------------
--  DDL for Package Body LOGGER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE BODY "LOGGER" 
is
    g_job_msg_dir    varchar2 (32) := 'UT_PROCESS_LOG_DIR';
    g_filter_level          pls_integer := G_INFO ;
    g_record_level          pls_integer := G_INFO ;
    g_file_handle           UTL_FILE.file_type;
    g_log_file_name         varchar2 (255);
    g_last_log_seq_nbr      pls_integer;
    g_dbms_output_level     pls_integer        := 5;
    g_process_start_tm      timestamp;
    g_process_end_tm        timestamp;
    g_process_name          varchar2 (128);
    g_process_status_id     pls_integer;
 -- set by get_caller
    g_owner_name            varchar2 (100);
    g_caller_name           varchar2 (100);
    g_line_number           pls_integer;
    g_caller_type           varchar2 (100);
--
    g_sid                   pls_integer;
    g_current_schema        varchar2 (32);
    g_current_user          varchar2 (32);
    g_session_user          varchar2 (32);
    g_proxy_user            varchar2 (32);
    g_who_called_me_level   BINARY_integer     := 6;
--    g_job_log_id            pls_integer;


    function format_time(p_timestamp in timestamp)
    return varchar 
    is
	my_timestamp varchar(256) :=  to_char (current_timestamp, 'YYYY-MM-DD HH24:MI:SSXFF');
    begin
        my_timestamp := replace(my_timestamp,' ','T');
        dbms_output.put_line('format_time ' || my_timestamp);       
        return my_timestamp;
    end format_time;

    function get_new_job_log_id return number 
    is
    begin
        return job_log_id_seq.nextval;
    end;

    --%#Tracing
    --%<
    procedure set_trace (p_trace_level in pls_integer)
    --%>
    is
    begin
       DBMS_TRACE.set_plsql_trace (p_trace_level);
    end set_trace;

    --%<
    function get_my_tracefile_name 
    return varchar 
    --%>
    is
         tracefile_name varchar2(4096);
    begin
        select value into tracefile_name
        from v$diag_info
        where name = 'Default Trace File';

        return tracefile_name;
    end get_my_tracefile_name;

    --%<
    function set_tracefile_identifier(p_job_nbr in number) 
    return varchar
    --%>
    is
       identifier varchar2(32) := 'job_' || to_char(p_job_nbr);
    begin
        execute immediate 'alter session set tracefile_identifier = ''' || identifier || '''';
        return get_my_tracefile_name;
    end set_tracefile_identifier;

    procedure job_msg_insert (
               p_job_log_id in pls_integer,
               g_next_log_seq_nbr in pls_integer,
               p_log_msg_id in varchar,
               p_short_message in varchar,
               p_log_level in pls_integer,
               p_caller_name in varchar,
               p_line_number in pls_integer,
               p_long_message in varchar
      )
   is
       pragma autonomous_transaction ;
   begin

      if p_log_level = g_snap OR p_log_level <= g_record_level then
          insert into job_msg (
               job_msg_id,    job_log_id,        log_seq_nbr,         log_msg_id,    
               log_msg,       log_level,         log_msg_ts,          caller_name,    
               line_nbr,      log_msg_clob
          )
          values(
               p_log_msg_id,    p_job_log_id,    g_next_log_seq_nbr,  p_log_msg_id,   
               p_short_message, p_log_level,     current_timestamp,   p_caller_name,
               p_line_number,   p_long_message
         );
      end if;
   end;




    --%<
    procedure job_log_insert ( 
        p_job_log_id   in number,
        p_process_name in varchar,
        p_classname    in varchar,
        p_module_name  in varchar,
        p_status_msg   in varchar,
        p_thread_name  in varchar,
        p_job_token    in varchar,
        p_logfile_name in varchar,
        p_trace_level  in pls_integer default G_INFO
    ) 
    --%>
    is
        PRAGMA AUTONOMOUS_TRANSACTION;
        my_tracefile_name varchar(4096) := get_my_tracefile_name();
        my_current_schema varchar(32):= SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA');
        --g_current_user    := SYS_CONTEXT('USERENV', 'CURRENT_USER');
        --g_session_user    := SYS_CONTEXT('USERENV', 'SESSION_USER');
    begin

       insert into job_log (    
          job_log_id,     process_name,    thread_name,
          status_msg,     status_ts,       tracefile_name,
          classname,      schema_name,     module_name, 
          job_token,      logfile_name
     ) values (
          p_job_log_id,  p_process_name,     p_thread_name,
          p_status_msg,  current_timestamp,  my_tracefile_name,
          p_classname,   my_current_schema,  p_module_name, 
          p_job_token,   p_logfile_name
   );
       commit;
   end job_log_insert;


    --%~~~<
   FUNCTION begin_job ( 
        p_process_name in varchar,
        p_logger_set   in varchar default null,
        p_classname    in varchar default null,
        p_module_name  in varchar default null,
        p_status_msg   in varchar default null,
        p_thread_name  in varchar default null,
        p_logger_level in pls_integer default G_INFO,
        p_trace_level  in pls_integer default G_INFO)
        return varchar
    --%>---
    is
        my_tracefile_name varchar2(256);
        my_job_log_id number:= job_log_id_seq.nextval;
        my_job_token varchar(64) := format_time(current_timestamp);
    begin
        set_trace(p_trace_level);
        dbms_output.put_line('begin job ' || to_char(my_job_log_id));
        g_process_start_tm := current_timestamp;
        my_tracefile_name := set_tracefile_identifier(my_job_log_id);
        set_action('begin_job ' || to_char(my_job_log_id)); 

        g_log_file_name := my_job_token;
        dbms_output.put_line('begin_job g_log_file_name ' || g_log_file_name);

        job_log_insert ( 
            p_job_log_id   => my_job_log_id,
            p_process_name => p_process_name,
            p_classname    => p_classname,
            p_module_name   => p_module_name,
            p_status_msg   => p_status_msg,
            p_thread_name  => p_thread_name,
            p_job_token    => my_job_token,
            p_logfile_name => my_job_token,
            p_trace_level  => p_trace_level);

            return my_job_token;

    end begin_job;



   procedure end_job
   --::* update job_log.status_id to 'C' and status_msg to 'DONE'
   --::>
   is
       PRAGMA AUTONOMOUS_TRANSACTION;
       elapsed_tm   INTERVAL DAY TO SECOND;
   begin
       set_action('end_job');
       g_process_end_tm := current_timestamp;
       elapsed_tm := g_process_end_tm - g_process_start_tm;

       update job_log
       set
              SID = NULL,
              status_msg = 'DONE',
              status_ts = SYSDATE
       where job_log_id = g_process_status_id;

      commit;
      set_action('end_job complete');
   end end_job;

    procedure abort_job(p_stacktrace in varchar default null)
    --::* procedure abort_job
    --::* update job_log
    --::* elapsed_time
    --::* status_id = 'I'
    --::* status_msg = 'ABORT'
    --::>
    is
       PRAGMA AUTONOMOUS_TRANSACTION;
       elapsed_tm   INTERVAL DAY TO SECOND;
       stack   varchar2 (32767);
    begin
        set_action('abort_job');
        g_process_end_tm := current_timestamp;
        elapsed_tm := g_process_end_tm - g_process_start_tm;

        if p_stacktrace is not null then
            stack := p_stacktrace;
        else
            stack := DBMS_UTILITY.format_call_stack ();
        end if;

        update job_log
        set  SID = NULL,
             status_msg = 'ABORT',
             status_ts = SYSDATE,
             abort_stacktrace = stack
        where job_log_id = g_process_status_id;

        COMMIT;
        set_action('abort_job complete');
    end abort_job;


    procedure set_action ( p_action in varchar2 ) is
    begin
            dbms_application_info.set_action(substr(p_action, 1, 64)) ;
    end set_action ;

    procedure set_module ( p_module_name in varchar, p_action_name in varchar )
    is
    begin
            dbms_application_info.set_module(p_module_name, p_action_name) ;
    end set_module ;


   function get_directory_path return varchar is
       -- todo see if grants are wrong, permission must be granted to the user
       cursor directory_cur is
       select  owner, directory_name, directory_path
       from    all_directories
       where   directory_name = g_job_msg_dir;

       directory_rec directory_cur%rowtype;

    begin
        open directory_cur;
        fetch directory_cur into directory_rec;
        dbms_output.put_line('owner: '           || directory_rec.owner ||
                           ' directory_name: ' || directory_rec.directory_name ||
                           ' directory_path: ' || directory_rec.directory_path);
       close directory_cur;

       return directory_rec.directory_path;
    end get_directory_path;
  --::<
      function basename (p_full_path in varchar2,
                     p_suffix    in varchar2 default null,
                     p_separator in char default '/')
      return varchar2
      --:: like bash basename or gnu basename, returns the filename of a path optionally
      --:: stripping the specified file extension
      --::>
    is
       my_basename varchar2(256);
    begin
        dbms_output.put_line('basename ' || p_full_path);
        my_basename := substr(p_full_path, instr(p_full_path,p_separator,-1)+1);
        dbms_output.put_line('my_basename' || my_basename);
        if p_suffix is not null then
            my_basename := substr(my_basename, 1, instr(my_basename, p_suffix, -1)-1);
        end if;

       return my_basename;
    end basename;

    function get_my_tracefile return clob is
    begin
        return get_tracefile(basename(get_my_tracefile_name));
    end get_my_tracefile;

    function get_tracefile(p_file_name in varchar)
    return clob is
        my_clob         clob;
        my_bfile        bfile;
        my_dest_offset  integer := 1;
        my_src_offset   integer := 1;
        my_lang_context integer := dbms_lob.default_lang_ctx;
        my_warning      integer;
    begin
        my_bfile := bfilename('UDUMP_DIR', p_file_name);

        dbms_lob.CreateTemporary(my_clob, FALSE, dbms_lob.CALL);
        dbms_lob.FileOpen(my_bfile);
        dbms_output.put_line('get_tracefile: before LoadClobFromFile');

        dbms_lob.LoadClobFromFile (
            dest_lob     => my_clob,
            src_bfile    => my_bfile,
            amount       => dbms_lob.lobmaxsize,
            dest_offset  => my_dest_offset,
            src_offset   => my_src_offset,
            bfile_csid   => dbms_lob.default_csid,
            lang_context => my_lang_context,
            warning      => my_warning
        );
        dbms_output.put_line('get_tracefile warning: ' || my_warning);
        dbms_lob.FileClose(my_bfile);

        return my_clob;
    end get_tracefile;

    procedure trace_step(p_step_name in varchar, p_job_step_id in number) is
       job_step_id varchar(9) := to_char(p_job_step_id);
       sql_text varchar(255) := 'select ''step_name: ''''' || p_step_name || 
               ''''' job_log_id: ' || g_process_status_id || 
              ' job_step_id: ' || p_job_step_id || ''' from dual';
    begin
     --  dbms_output.put_line(sql_text);
       execute immediate sql_text;
    end;

    procedure set_filter_level (p_level in pls_integer) is
    begin
        if    p_level < 1 then g_filter_level := 1;
        elsif p_level > 9 then g_filter_level := 9;
        else  g_filter_level := p_level;
        end if;
    end set_filter_level;


    PROCEDURE prepare_connection is
        context_info DBMS_SESSION.AppCtxTabTyp;
        info_count   PLS_INTEGER;
        indx         PLS_INTEGER;
    BEGIN
        DBMS_SESSION.LIST_CONTEXT ( context_info, info_count);
        indx := context_info.FIRST;
        LOOP
           EXIT WHEN indx IS NULL;
           DBMS_SESSION.CLEAR_CONTEXT(
               context_info(indx).namespace,
               context_info(indx).attribute,
              null
            );
           indx := context_info.NEXT (indx);
       END LOOP;
       DBMS_SESSION.RESET_PACKAGE;
    END prepare_connection;




begin
   dbms_output.ENABLE(1000000) ;
  -- set_context;
end logger;

/
