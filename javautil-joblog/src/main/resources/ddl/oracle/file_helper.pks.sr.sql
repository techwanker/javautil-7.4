--#<
set echo on 
spool file_helper.pks.sr.lst
--#>
--/<
CREATE OR REPLACE PACKAGE file_helper 
is

  function open_log_file (
        directory_name in varchar,
        file_name in varchar, 
        headers in boolean default true)
  return utl_file.file_type;

  function get_directory_path return varchar;

  function basename (full_path in varchar,
                     suffix    in varchar default null,
                     separator in char default '/')
  return varchar;

  function get_file(directory in varchar, file_name in varchar)
  return clob;

end file_helper;
--/>

--#<
/
show errors
--#>
