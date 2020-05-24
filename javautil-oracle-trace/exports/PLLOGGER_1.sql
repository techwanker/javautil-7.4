--------------------------------------------------------
--  DDL for Package Body PLLOGGER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE BODY "PLLOGGER" 
is
   g_job_msg_dir    varchar2 (32) := 'JOB_MSG_DIR';
   g_job_msg_id     pls_integer;
   g_filter_level          pls_integer := G_INFO ;
   g_record_level          pls_integer := G_INFO ;
   g_file_handle           UTL_FILE.file_type;
   g_log_file_name         varchar2 (255);
   g_next_log_seq_nbr      pls_integer;
   g_dbms_output_level     pls_integer        := 5;
   g_process_start_tm      timestamp;
   g_process_end_tm        timestamp;
   g_process_name          varchar2 (128);
   g_job_id     pls_integer;
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
   g_debug                 boolean := false;
   g_emit_headers          boolean := true;
   g_job_log          job_log%rowtype;

   g_logger_hdr logger_hdr%rowtype;

   /* https://livesql.oracle.com/apex/livesql/docs/lnpls/plsql-collections-and-records/composites1.html */

   type logger_dtl_type is table of logger_dtl%rowtype index by varchar(64);

   logger_dtls logger_dtl_type;

   procedure set_trace (p_trace_level in pls_integer)
   is
   begin
      DBMS_TRACE.set_plsql_trace (p_trace_level);
   end set_trace;

   procedure set_job_token(p_job_token in varchar) is
   begin
       select * into g_job_log
       from job_log
       where job_token  = p_job_token;
    end;

    procedure  emit_header is
    begin
        if g_emit_headers then
            utl_file.put_line (g_file_handle, '"log_level","job_log_id","job_msg_id","line_number",' || 
                                              '"timestamp","log_msg","caller_name","call_stack"');
         end if;
    end emit_header;

   function open_log_file (
        p_file_name in varchar, 
        p_headers in boolean default true)
   return varchar
   --
   --% opens a log file with the specified file name in the directory g_job_msg_dir
   is
      my_log_file_name varchar(255);
      my_directory_path varchar2(4000);
   begin
      if (NOT UTL_FILE.is_open (g_file_handle))
      then
         -- TODO filter putlines 
         g_log_file_name := p_file_name;
         dbms_output.put_line('open_log_file:> ' || g_job_msg_dir || ' ' || g_log_file_name);
         g_file_handle := utl_file.fopen(g_job_msg_dir,g_log_file_name,'a');
         --utl_file.fclose(g_file_handle);
         --g_file_handle := utl_file.fopen(g_job_msg_dir,g_log_file_name,'a');
         emit_header;

      else
         dbms_output.put_line('open_log_file log_file is open: ' || g_log_file_name);

      end if;
      return my_log_file_name;
   end open_log_file;

   function get_g_job_id return number is
   begin
       if g_job_id  is  null then
           g_job_id := job_log_id_seq.nextval;
       end if;
       return g_job_id;
   end;

  /*
  procedure update_tracefile_name(p_tracefile_name in varchar) is
     pragma autonomous_transaction ;
  begin
	  update job_log set tracefile_name = p_tracefile_name 
	  where job_log_id = g_job_id;
	  commit;
  end;
  */

    procedure use_logger_set(p_set_nm in varchar) 
    is
        no_such_logger_set exception;
    begin
       dbms_output.put_line('use_logger_set "' || p_set_nm || '"');
       select * into g_logger_hdr from logger_hdr 
       where logger_set_nm = upper(p_set_nm);

       exception when
           no_data_found then raise_application_error(-200001,'no_such_logger_set ' || p_set_nm);

       for dtl in ( select * 
       from logger_dtl
       where logger_hdr_id = g_logger_hdr.logger_hdr_id)
       loop
           logger_dtls(upper(dtl.logger_nm)) := dtl;
       end loop;
    end;

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
             retval := g_filter_level;
         end;

        dbms_output.put_line('get_log_level() ====> ' || p_logger_name || ' ' || to_char(my_log_dtl.log_lvl) || ' retval ' || to_char(retval));

        return retval;

    end get_log_level;
  --::<
  procedure create_process_log (
      -- TODO no database updates, no commit or autonomous required

      p_job_log_id   in   pls_integer,
      p_job_msg_id   in   pls_integer,
      p_log_msg      in   varchar2,
      p_log_level    in   pls_integer,
      p_elapsed_time in   INTERVAL DAY TO SECOND DEFAULT NULL, -- TODO not recorded at this time
      p_caller_name  in   varchar2,
      p_line_number  in   pls_integer,
      p_call_stack   in   varchar2 DEFAULT NULL
   )
   is
      my_message   varchar2 (32767);
      -- my_msg       varchar2 (32767);
      now          timestamp        := SYSDATE;
      --pragma autonomous_transaction ;
      -- short_message varchar2(255);
      -- long_message  clob;
      my_log_file_name varchar2(4000);
      my_logger_level number;

   begin
       if p_caller_name is not null then  -- TODO make it work with null
           my_logger_level := get_log_level(p_caller_name);
        else
           my_logger_level := g_filter_level;
       end if;
       g_next_log_seq_nbr := 1;

    /*
      if g_log_file_name is NULL then  -- TODO name shoul be in one place
         g_log_file_name := g_process_name || '_' || to_char (current_timestamp, 'YYYY-MM-DD_HH24MisSXFF');
      end if;
      */
      my_log_file_name := open_log_file (g_job_log.logfile_name);  -- TODO why pass a global
      g_next_log_seq_nbr := g_next_log_seq_nbr;        -- TODr + 1; O doesnt work with multitask
      if g_debug then 
        dbms_output.put_line('open_log_file' || g_log_file_name);
        dbms_output.put_line('my_log_file_name: ' || my_log_file_name);
        dbms_output.put_line('g_next_log_seq_nbr ' || g_next_log_seq_nbr);
      end if;

      if p_log_level <= my_logger_level then
          g_next_log_seq_nbr := g_next_log_seq_nbr + 1;   -- TODO this should go as too much work if multiple connections

          my_message := logger_message_formatter  (
              job_log_id   => p_job_log_id,
              job_msg_id   => p_job_msg_id,
              log_msg      => p_log_msg,
              log_level    => p_log_level,
              caller_name  => p_caller_name,
              line_number  => p_line_number,
              call_stack   => p_call_stack
          );
     	  dbms_output.put_line('create_process_log about to write ' || to_char(p_log_level) || my_message); 
     	  --dbms_output.put_line('p_caller_name ' || p_caller_name);
     	  --dbms_output.put_line('p_line_number ' || p_line_number);
    my_log_file_name := open_log_file (g_job_log.logfile_name); 
          UTL_FILE.put_line (g_file_handle, my_message);
          close_log_file;
      else
          dbms_output.put_line('create_process_log() skip p_log_level
          ' || to_char(p_log_level) || ' my_logger_level ' 
          || to_char(my_logger_level) || ' ' || my_message);

      end if;
      -- commit;
   end create_process_log;

   -- TODO bad name
   --<
   procedure TRACE (p_string in varchar2)
   --% procedure TRACE (p_string in varchar2)
   --% Write the messsage to dbms_output
   -->
   is
   begin
      dbms_output.put_line (p_string);
   end TRACE;

-- public
   procedure set_dbms_output_level (p_level in pls_integer)
   is
   begin
      g_dbms_output_level := p_level;
   end set_dbms_output_level;

   procedure close_log_file
   is
   begin
      if utl_file.is_open (g_file_handle) then
          utl_file.fclose (g_file_handle);
      end if;
   end close_log_file;

   procedure log_level (
      p_log_msg       in   varchar2,
      p_log_level     in   pls_integer,
      p_caller_name   in   varchar2 DEFAULT NULL,
      p_line_number   in   pls_integer DEFAULT NULL,
      p_call_stack    in   varchar2 DEFAULT NULL
   )
   is
      log_time       timestamp  := current_timestamp;
      elapsed        INTERVAL DAY TO SECOND;
      my_log_level   pls_integer;
   begin
      my_log_level := p_log_level;

      if my_log_level < 1 then my_log_level := 1; end if;
      if my_log_level > 9 then my_log_level := 9; end if;

      -- dbms_output.put_line('about to create process log');
      --utl_file.put_line(g_file_handle, p_log_msg);  -- TODO
      create_process_log (p_job_log_id  => g_job_id,
                          p_job_msg_id            => g_job_log.job_log_id,
                          p_log_msg               => p_log_msg,
                          p_log_level             => my_log_level,
                          p_elapsed_time          => elapsed,
                          p_caller_name           => p_caller_name,
                          p_line_number           => p_line_number,
                          p_call_stack            => p_call_stack
                         );
   end log_level;


   procedure LOG (p_level in pls_integer, p_log_msg IN varchar2)
   is
   begin
      log_level (p_log_level        => p_level,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => g_caller_name,
                 p_line_number      => g_line_number
                );
   end LOG;

   procedure severe (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2 DEFAULT '',
      p_record_stack   in   BOOLEAN default false
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then
         stack := DBMS_UTILITY.format_call_stack ();
      end if;

      log_level (p_log_level        => g_severe,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
   end severe;

   procedure warning (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2,
      p_record_stack   in   BOOLEAN
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then
         stack := DBMS_UTILITY.format_call_stack ();
      end if;

      log_level (p_log_level        => g_warning,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
   end warning;


   procedure log_snap (
      p_unit      in   varchar2,
      p_line      in   pls_integer,
      p_log_msg   in   varchar2
   )
   is
   begin
      OWA_UTIL.who_called_me (g_owner_name,
                              g_caller_name,
                              g_line_number,
                              g_caller_type
                             );

      log_level (p_log_level        => g_snap,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => g_caller_name,
                 p_line_number      => g_line_number
                );
   end log_snap;

   procedure entering (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2 DEFAULT '',
      p_record_stack   in   BOOLEAN DEFAULT FALSE,
      p_set_action     in   BOOLEAN DEFAULT TRUE
   )
   is
      stack   varchar2 (32767) := NULL;
   begin
      if p_record_stack then
         stack := DBMS_UTILITY.format_call_stack ();
      end if; 
      log_level (p_log_level        => g_entering,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
      if p_set_action then dbms_application_info.set_action($$PLSQL_UNIT) ; end if;
   end entering;

   procedure exiting (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2,
      p_record_stack   in   BOOLEAN
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then
         stack := DBMS_UTILITY.format_call_stack ();
      end if;

      log_level (p_log_level        => g_exiting,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
      dbms_application_info.set_action('');
   end exiting;

   procedure fine (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2,
      p_record_stack   in   BOOLEAN
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then
         stack := DBMS_UTILITY.format_call_stack ();
      end if;

      log_level (p_log_level        => g_fine,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
   end fine;

   procedure finer (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2,
      p_record_stack   in   BOOLEAN DEFAULT FALSE
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then 
        stack := DBMS_UTILITY.format_call_stack (); 
   end if;

      log_level (p_log_level        => g_finer,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
   end finer;

   procedure finest (
      p_unit           in   varchar2,
      p_line           in   pls_integer,
      p_log_msg        in   varchar2,
      p_record_stack   in   BOOLEAN DEFAULT FALSE
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then stack := DBMS_UTILITY.format_call_stack (); end if;

      log_level (p_log_level        => g_finest,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
   end finest;

   procedure LOG (
      p_msg_id         in   varchar2,
      p_log_msg        in   varchar2,
      p_long_msg       in   varchar2,
      p_elapsed_time   in   INTERVAL DAY TO SECOND DEFAULT NULL,
      p_log_level      in   pls_integer,
      p_call_stack     in   varchar2 DEFAULT NULL
   )
   is
      my_log_time    timestamp ( 6 )        := current_timestamp;
      my_elapsed     INTERVAL DAY TO SECOND;
      my_log_level   pls_integer;
   begin
      my_log_level := p_log_level;

      if my_log_level < 1 then my_log_level := 1; end if;
      if my_log_level > 9 then my_log_level := 9; end if;

      create_process_log (p_job_log_id   => g_job_id,
                          p_job_msg_id              => p_msg_id,
                          p_log_msg                 => p_log_msg,
                          p_log_level               => my_log_level,
                          p_elapsed_time            => my_elapsed,
                          p_caller_name             => g_caller_name,
                          p_line_number             => g_line_number,
                          p_call_stack              => p_call_stack
                         );
   end LOG;


   procedure set_filter_level (p_level in pls_integer)
   is
   begin
      if    p_level < 1 then g_filter_level := 1;
      elsif p_level > 9 then g_filter_level := 9;
      else  g_filter_level := p_level; 
      end if;
   end set_filter_level;

   /**
    procedure set_record_level (p_level in pls_integer)
   */
   procedure set_record_level (p_level in pls_integer)
   is
   begin
      if    p_level < 1 then g_record_level := 1;
      elsif p_level > 9 then g_record_level := 9;
      else  g_record_level := p_level;
      end if;
   end set_record_level;

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

   procedure log_msg (
      p_log_msg        in   varchar,
      p_level          in   pls_integer default G_INFO,
      p_unit           in   varchar     default null,
      p_line           in   pls_integer default null,
      p_record_stack   in   BOOLEAN         -- record the call stack

   ) 
   is 
      stack   varchar2 (32767);
   begin
      if p_record_stack then
        stack := dbms_utility.format_call_stack ();
      end if;
      log_level (p_log_level        => g_info,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );

   end log_msg;


   procedure info (
      p_unit           in   varchar2,       -- should be set with $$PLSQL_UNIT
      p_line           in   pls_integer,    -- should be set with $$PLSQL_LINE
      p_log_msg        in   varchar2,       -- the message to be logged
      p_record_stack   in   BOOLEAN         -- record the call stack
   )
   is
      stack   varchar2 (32767);
   begin
      if p_record_stack then
        stack := dbms_utility.format_call_stack ();
      end if;
      log_level (p_log_level        => g_info,
                 p_log_msg          => p_log_msg,
                 p_caller_name      => p_unit,
                 p_line_number      => p_line,
                 p_call_stack       => stack
                );
   end info;

    procedure set_action (p_action_name in varchar) is 
    -- record the step name and then restore the action
    -- the execute immediate makes sure the action is written to the trace file 
    -- if there is no sql activity it is not written
    begin
       dbms_application_info.set_action(p_action_name);
       execute immediate 'select ''' || p_action_name || ''' from dual'; -- TODO why 
    end;

    procedure set_job_msg_id (p_job_msg_id in pls_integer) is
    begin
        g_job_msg_id := p_job_msg_id;
    end;

  --
  --  Logger hdr and dtl
  --

    procedure create_set (p_set_nm    in varchar,
                        p_default_level     in number)
    is
    begin
        insert into logger_hdr (logger_hdr_id, logger_set_nm, default_lvl)
        values (logger_hdr_id_seq.nextval, upper(p_set_nm), p_default_level);
     end create_set;

    procedure define_logger_level(p_set_nm    in varchar,
                        p_logger_nm in varchar,
                        p_level     in number)
    is 
        logger_rec logger_hdr%rowtype;

    begin

             insert into logger_dtl (logger_dtl_id, logger_hdr_id, 
                     logger_nm, log_lvl)
             select logger_dtl_id_seq.nextval, 
                    logger_hdr.logger_hdr_id,
                    upper(p_logger_nm), p_level
             from   logger_hdr 
             where 
                   logger_set_nm = upper(p_set_nm);

             exception when dup_val_on_index 
             then
                 update logger_dtl 
                 set  log_lvl =  p_level
                 where logger_hdr_id =  (
                         select logger_hdr_id 
                         from   logger_hdr
                         where logger_set_nm = upper(p_set_nm) 
			)
                         and logger_nm = upper(p_logger_nm);



    end define_logger_level;




begin
   dbms_output.ENABLE(1000000) ;
end pllogger;

/
