--#<
set echo on 
spool file_helper.pkb.sr.lst
--#>
--/<
CREATE OR REPLACE PACKAGE BODY file_helper 
is
  g_debug                 boolean := false;
  g_job_msg_dir           varchar (32) := 'JOB_MSG_DIR';
  g_logfile_name          varchar(255);

  
 /*
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
   */

  function open_log_file (
        directory_name in varchar,
        file_name in varchar, 
        headers in boolean default true)
  return utl_file.file_type
   --
   --% opens a log file with the specified file name in the directory g_job_msg_dir
  is
      my_directory_path varchar2(4000);
      my_handle utl_file.file_type;
  begin
      if (g_debug) then
          dbms_output.put_line('open_log_file() dir: "' || directory_name || 
                           '" file: "' || file_name || '"');
      end if;
      my_handle := utl_file.fopen(directory_name,file_name,'a');
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
  function basename (full_path in varchar,
                     suffix    in varchar default null,
                     separator in char default '/')
  return varchar
      --:: like bash basename or gnu basename, returns the filename of a path optionally
      --:: stripping the specified file extension
      --::>
  is
       my_basename varchar(256);
  begin
        dbms_output.put_line('basename ' || full_path);
        my_basename := substr(full_path, instr(full_path,separator,-1)+1);
        dbms_output.put_line('my_basename' || my_basename);
        if suffix is not null then
            my_basename := substr(my_basename, 1, instr(my_basename, suffix, -1)-1);
        end if;

       return my_basename;
  end basename;
 
  /* 
  function get_my_tracefile return clob is
  begin
     return get_tracefile(basename(get_my_tracefile_name));
  end get_my_tracefile;
  */ 
  function get_file(directory in varchar, file_name in varchar)
  return clob is
     my_clob         clob;
     my_bfile        bfile;
     my_dest_offset  integer := 1;
     my_src_offset   integer := 1;
     my_lang_context integer := dbms_lob.default_lang_ctx;
     my_warning      integer;
  begin
     my_bfile := bfilename(directory, file_name);

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
  end get_file;



begin dbms_output.ENABLE(1000000) ; -- set_context;
end file_helper;
--/>

--#<
/
show errors
--#>
