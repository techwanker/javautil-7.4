--#<
set echo on 
spool tracer.pks.sr.lst
--#>
--/<
CREATE OR REPLACE PACKAGE tracer
is

  --%#Tracing
  --%<
  procedure set_trace (trace_level in pls_integer);
  --%>


  --%<
  function get_my_tracefile_name 
  return varchar ;
  --%>

  --%<
  function set_tracefile_identifier(job_nbr in number) 
  return varchar;
  --%>

  --%<
  procedure set_debug(debug boolean default true);
  --%>


  --%<
  procedure set_action ( action in varchar) ;
  --%>

  --%<
  procedure set_module ( module_name in varchar, action_name in varchar );
  --%>


  --%<
  function get_tracefile(file_name in varchar)
  return clob;
  --%>


  --%<
  procedure trace_step(step_name in varchar, job_step_id in number) ;
  --%>

end tracer;
--/>

--#<
/
show errors
--#>
