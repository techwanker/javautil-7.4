set serveroutput on
set echo on
spool example_log_suite.lst

--%## Define some procedures
--%```

--%### example_04
--%```
create or replace 
procedure example_04
is
    token varchar(32);
begin
    pllog.log('severe 1',1);
    pllog.log('warning 2',2);
    pllog.log('undefined 3',3);
    pllog.log('info 4',4);
    pllog.log('verbose 6',6);
    pllog.log('debug 7',7);
    pllog.log('trace 8',9);
    for lvl in 1 .. 9
    loop
    	pllog.log('lvl is ' || lvl,lvl);
    end loop;
end;
--%```
/

--%### example_05
--%```
create or replace 
procedure example_job_05
is
    lines number := 0;
begin
    pllog.log('severe 1',1);
    pllog.log('warning 2',2);
    pllog.log('undefined 3',3);
    pllog.log('info 4',4);
    pllog.log('verbose 6',6);
    pllog.log('debug 7',7);
    pllog.log('trace 8',9);
    for lvl in 1 .. 9
    loop
    	pllog.log('lvl is ' || lvl,lvl);
    end loop;
    for rec in select * from all_source
    loop
       lines := lines + 1;
    end loop;
    pllog.end_job;
end;
--%```
/

begin
  pllog.set_debug;
  pllog.log('no logfile specified');
end;
/

--%# Example usage
--%
--%
--%* begin_log (logfile_name, p_log_level)
--%
--%  A logfile will be created in the *job_msg_dir* directory and
--%  unless other filters are applied messages of level 3 and lower
--%  will be written
--%
--%* set_caller_level(unit_name,level)
--%  
--%  All messages for procedure *example_05* of with a level of 8
--%  or less will be written  
--%```
declare
    token varchar(32);
    --logname varchar(32) := to_char(job_log_id_seq.nextval) || '.log';
    my_job_step_id number;
begin
   pllog.set_debug;
   token := pllog.begin_job(p_process_name => 'example_job');
   pllog.set_caller_level('example_05',8);
   my_job_step_id := pllog.job_step_insert('example_01','no parms');
   example_01;
   pllog.job_step_finish(my_job_step_id);
   
   my_job_step_id := pllog.job_step_insert(p_step_name => 'example_04',
                        p_step_info => 'no parms');
   example_04;
   pllog.job_step_finish(my_job_step_id);

   my_job_step_id := pllog.job_step_insert('example_05');
   example_05;
   pllog.job_step_finish(my_job_step_id);
exception when others then
   pllog.abort_job;
end;
--%```
/
exit;

## The output
--%```
--%1,"2020-05-15T18:23:57.422520000","severe","EXAMPLE_01",4,,,""
--%2,"2020-05-15T18:23:57.427165000","warning","EXAMPLE_01",5,,,""
--%3,"2020-05-15T18:23:57.429084000","undefined","EXAMPLE_01",6,,,""
--%1,"2020-05-15T18:23:57.439742000","lvl is 1","EXAMPLE_04",8,,,""
--%2,"2020-05-15T18:23:57.441323000","lvl is 2","EXAMPLE_04",8,,,""
--%3,"2020-05-15T18:23:57.442867000","lvl is 3","EXAMPLE_04",8,,,""
--%1,"2020-05-15T18:23:57.453696000","lvl is 1","EXAMPLE_05",7,,,""
--%2,"2020-05-15T18:23:57.455170000","lvl is 2","EXAMPLE_05",7,,,""
--%3,"2020-05-15T18:23:57.456750000","lvl is 3","EXAMPLE_05",7,,,""
--%```
