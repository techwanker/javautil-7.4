--#<
set echo on 
spool log.pkb.lst
--#>
CREATE OR REPLACE PACKAGE BODY pllog
is
    g_debug                 boolean := true;
    g_job_msg_dir           varchar (32) := 'JOB_MSG_DIR';
    --g_file_handle           UTL_FILE.file_type;
    g_logfile_name          varchar(255);

  
   type logger_dtl_type is table of logger_dtl%rowtype index by varchar(64);
   
     logger_dtls logger_dtl_type;

    g_job_log job_log%rowtype;
    
    
    function format_time(p_timestamp in timestamp)
    return varchar 
    is
	my_timestamp varchar(256) :=  to_char (current_timestamp, 'YYYY-MM-DD HH24:MI:SSXFF');
    begin
        my_timestamp := replace(my_timestamp,' ','T');
        -- dbms_output.put_line('format_time ' || my_timestamp);       
        return my_timestamp;
    end format_time;

    function get_job_token 
    return varchar 
    is begin
        return format_time(current_timestamp);
    end;

    function get_new_job_log_id 
    return number 
    is begin
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
         tracefile_name varchar(4096);
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
       identifier varchar(32) := 'job_' || to_char(p_job_nbr);
    begin
        execute immediate 'alter session set tracefile_identifier = ''' || identifier || '''';
        return get_my_tracefile_name;
    end set_tracefile_identifier;

    procedure job_msg_insert (
               p_job_log_id in pls_integer,
    --           g_next_log_seq_nbr in pls_integer,
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
    
      if p_log_level = g_snap OR p_log_level <= g_job_log.msg_lvl then
          insert into job_msg (
               job_msg_id,    job_log_id,        
              -- log_seq_nbr,         
               log_msg_id,    
               log_msg,       log_level,         log_msg_ts,          caller_name,    
               line_nbr,      log_msg_clob
          )
          values(
               p_log_msg_id,    p_job_log_id,    
	       -- g_next_log_seq_nbr,  
               p_log_msg_id,   
               p_short_message, p_log_level,     current_timestamp,   p_caller_name,
               p_line_number,   p_long_message
         );
      end if;
   end;

    procedure job_log_insert(rec in job_log%rowtype) is
    begin
       insert into job_log (    
          job_log_id,     process_name,    thread_name,
          status_msg,     status_ts,       tracefile_name,
          classname,      schema_name,     module_name, 
          job_token,      logfile_name
     ) values (
          rec.job_log_id,  rec.process_name,   rec.thread_name,
          rec.status_msg,  current_timestamp,  rec.tracefile_name,
          rec.classname,   rec.schema_name,  rec.module_name, 
          rec.job_token,   rec.logfile_name
   );

    end;

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
        p_trace_level  in pls_integer default G_INFO)
    --%>
    is
        my_tracefile_name varchar(256);
        my_job_token varchar(64) := format_time(current_timestamp);
    
    begin
        dbms_output.put_line('begin_log() logfile_name "' || logfile_name || '"');
        g_job_log.logfile_name := logfile_name;
        g_job_log.directory_name := logfile_directory;
        --g_job_log.job_log_id   := job_log_id_seq.nextval;
        g_job_log.process_name := p_process_name;
        g_job_log.classname    := p_classname;
        g_job_log.module_name  := p_module_name;
        g_job_log.status_msg   := p_status_msg;
        g_job_log.thread_name  := p_thread_name;
        g_job_log.job_token    := my_job_token;
        g_job_log.logfile_name := logfile_name;
        g_job_log.trace_level  := p_trace_level;
        g_job_log.start_ts     := current_timestamp;
        g_job_log.log_level    := p_log_level;

        set_trace(p_trace_level);

        my_tracefile_name := set_tracefile_identifier(g_job_log.job_log_id);
        set_action('begin_job ' || to_char(g_job_log.job_log_id)); 

         
    end begin_log;
 
    --%~~~<
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
        return varchar
    --%>
    is
        my_tracefile_name varchar(256);
        my_job_token varchar(64) := format_time(current_timestamp);
        my_logfile_name varchar(64);
    begin
        dbms_output.put_line('begin_job logfile_name "' || logfile_name);
    
        if logfile_name is not null then
            my_logfile_name := logfile_name;
        else 
            my_logfile_name := my_job_token;
        end if;
 
        begin_log (
	    logfile_name   => my_logfile_name,
            logfile_directory => logfile_directory,
            p_process_name => p_process_name,
            p_log_set      => p_log_set,
            p_classname    => p_classname,
            p_module_name  => p_module_name,
            p_status_msg   => p_status_msg,
            p_thread_name  => p_thread_name,
            p_log_level    => p_log_level,
            p_trace_level  => p_trace_level  
	);

	g_job_log.job_log_id := job_log_id_seq.nextval;
        set_action('begin_job ' || to_char(g_job_log.job_log_id)); 
        job_log_insert ( g_job_log);

        return my_job_token;
         end begin_job;
   
 

   procedure end_job
   --::* update job_log.status_id to 'C' and status_msg to 'DONE'
   --::>
   is
       PRAGMA AUTONOMOUS_TRANSACTION;
--       elapsed_tm   INTERVAL DAY TO SECOND;
   begin
       set_action('end_job');
 --      g_process_end_tm := current_timestamp;
 --     elapsed_tm := g_process_end_tm - g_process_start_tm;

       update job_log
       set
              SID = NULL,
              status_msg = 'DONE',
              status_ts = SYSDATE
        where job_log_id = g_job_log.job_log_id;

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
       -- elapsed_tm   INTERVAL DAY TO SECOND;
       stack   varchar (32767);
    begin
        set_action('abort_job');
        -- g_process_end_tm := current_timestamp;
        -- elapsed_tm := g_process_end_tm - g_process_start_tm;
      
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
        where job_log_id = g_job_log.job_log_id;

        COMMIT;
        set_action('abort_job complete');
    end abort_job;


    procedure set_action ( p_action in varchar ) is
    begin
            dbms_application_info.set_action(substr(p_action, 1, 64)) ;
    end set_action ;

    procedure set_module ( p_module_name in varchar, p_action_name in varchar )
    is
    begin
            dbms_application_info.set_module(p_module_name, p_action_name) ;
    end set_module ;

   function open_log_file (
        directory_name in varchar,
        p_file_name in varchar, 
        p_headers in boolean default true)
   return utl_file.file_type
   --
   --% opens a log file with the specified file name in the directory g_job_msg_dir
   is
      my_directory_path varchar2(4000);
      my_handle utl_file.file_type;
   begin
      dbms_output.put_line('open_log_file() dir: "' || directory_name || 
                           '" file: "' || p_file_name || '"');
      --if (NOT UTL_FILE.is_open (g_file_handle)) then
            my_handle := utl_file.fopen(directory_name,p_file_name,'a');
      --end if;
      return my_handle;
   end open_log_file;

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
      function basename (p_full_path in varchar,
                     p_suffix    in varchar default null,
                     p_separator in char default '/')
      return varchar
      --:: like bash basename or gnu basename, returns the filename of a path optionally
      --:: stripping the specified file extension
      --::>
    is
       my_basename varchar(256);
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
               ''''' job_log_id: ' || g_job_log.job_log_id || 
              ' job_step_id: ' || p_job_step_id || ''' from dual';
    begin
       execute immediate sql_text;
    end;

    procedure set_log_level (p_level in pls_integer) is
    begin
        if    p_level < 1 then g_job_log.log_level := 1;
        elsif p_level > 9 then g_job_log.log_level := 9;
        else  g_job_log.log_level := p_level;
        end if;
    end set_log_level;


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
    
        procedure logger_dtls_to_str is
        ndx varchar(64);
        dtl logger_dtl%rowtype;
        retval long := '';
    begin
        dbms_output.put_line('logger_dtls_to_str');
       -- dbms_output.put_line('about to get first');
       -- ndx := logger_dtls.first();
        -- dbms_output.put_line('ndx "' || ndx || '"');
        
        while ndx is not null loop
            dtl :=  logger_dtls(ndx);
            retval := retval || dtl.logger_nm  || ' ' || dtl.log_lvl || '\n';
            ndx := logger_dtls.next(ndx);
        end loop;
        dbms_output.put_line('>> ' || retval);
       -- dbms_output.put_line('end logger_dtls_to_str');
    end logger_dtls_to_str;
    
 function get_log_level (p_logger_name in varchar) 
    return number 
    is 
        my_logger_name varchar(64) := upper(p_logger_name);
        my_log_dtl logger_dtl%rowtype;
        retval number;
    begin 
         dbms_output.put_line('get_log_level()  p_logger_name *' || p_logger_name || ' my_logger_name *' || my_logger_name || '*');
         logger_dtls_to_str;
         
         begin
             my_log_dtl  := logger_dtls(my_logger_name);
             retval := my_log_dtl.log_lvl;
         exception 
            when no_data_found then
             dbms_output.put_line('logger not found ' || my_logger_name);
             retval := g_job_log.log_level;
         end;
         
        dbms_output.put_line('get_log_level() ====> ' || p_logger_name || ' ' || to_char(my_log_dtl.log_lvl) || ' retval ' || to_char(retval));
        
        return retval;
            
    end get_log_level;

  procedure log (
      -- TOD no database updates, no commit or autonomous required

      p_log_msg      in   varchar,
      p_log_level    in   pls_integer default g_info,
      p_job_log_id   in   pls_integer default null,
      p_job_msg_id   in   pls_integer default null,
      p_elapsed_time in   INTERVAL DAY TO SECOND DEFAULT NULL, -- TODO not recorded at this time
      p_caller_name  in   varchar default null,
      p_line_number  in   pls_integer default null,
      p_dump_stack   in   boolean default false
   )
   is
      my_message   varchar2 (32767);
      now          timestamp        := SYSDATE;
      --pragma autonomous_transaction ;
  --   
      my_logger_level number;
      my_file_handle utl_file.file_type;
   
   begin
       if p_caller_name is not null then  -- TODO make it work with null
           my_logger_level := get_log_level(p_caller_name);
        else
           my_logger_level := g_job_log.log_level;
       end if;
       dbms_output.put_line('log() caller: ' || p_caller_name || 
          ' my_logger_level ' || to_char(my_logger_level) ||
          ' p_log_level '     || to_char(p_log_level) ||
          ' g_job_log.log_level '     || to_char(g_job_log.log_level));

      
      if p_log_level <= my_logger_level then
          my_message := logger_message_formatter  (
              job_log_id   => p_job_log_id,
              job_msg_id   => p_job_msg_id,
              log_msg      => p_log_msg,
              log_level    => p_log_level,
              caller_name  => p_caller_name,
              line_number  => p_line_number,
              call_stack   => null
          );
     	  dbms_output.put_line('log() about to write ' || to_char(p_log_level) || my_message); 
     	  --dbms_output.put_line('p_caller_name ' || p_caller_name);
     	  --dbms_output.put_line('p_line_number ' || p_line_number);
          my_file_handle := open_log_file (g_job_log.directory_name,g_job_log.logfile_name); 
          UTL_FILE.put_line (my_file_handle, my_message);
          utl_file.fclose(my_file_handle); 
          
      else
          dbms_output.put_line(
            'log() skip p_log_level ' || to_char(p_log_level) || 
	    ' my_logger_level ' || to_char(my_logger_level) || 
            ' ' || my_message);

      end if;
      
      -- commit;
   end log;


begin
   dbms_output.ENABLE(1000000) ;
  -- set_context;
end pllog;
/

/*
begin
      sys.DBMS_MONITOR.session_trace_enable(waits=>TRUE, binds=>FALSE);
end;
/
*/
--#<
show errors
exit
--#>
