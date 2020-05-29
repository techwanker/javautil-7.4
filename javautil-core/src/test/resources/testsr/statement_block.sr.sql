--/<
create or replace package logger as  -- 
--
    G_SEVERE       CONSTANT PLS_INTEGER := 1 ;
  

    function set_tracefile_identifier(p_job_nbr in number) return varchar;
END logger ;
--/>
--#<
show errors
--#>

