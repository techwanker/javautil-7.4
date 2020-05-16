spool example_log_03.lst
set echo on
declare
	 token varchar(32);
begin
	 token := pllog.begin_job(p_process_name => 'example 03',
		       p_log_level	=> 3);
	 pllog.log('begins',4,$$PLS_UNIT,$$PLS_LINE);
	 for lvl in 1 .. 9
	 loop
	     pllog.log('lvl is ' || lvl,lvl,$$PLS_UNIT,$$PLS_LINE);
	 end loop;
	 pllog.end_job;
end;
/
