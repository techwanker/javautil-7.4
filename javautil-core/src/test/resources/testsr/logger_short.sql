--#<
set echo on
--#>

--/<
--%```
create or replace package logger as
   

--%```
    function format_time(p_timestamp in timestamp) 
    return varchar;
--%```

END logger;
--/>

--#<
/
show errors
--#>
