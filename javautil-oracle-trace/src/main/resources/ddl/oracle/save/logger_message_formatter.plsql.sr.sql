--#<
set echo on
--#>

 create or replace function  logger_message_formatter  (
      -- log_seq_nbr             in pls_integer,
      job_log_id    in   pls_integer,
      job_msg_id    in   pls_integer,
      log_msg       in   varchar2,
      log_level     in   pls_integer,
      caller_name   in   varchar2,
      line_number   in   pls_integer,
      call_stack    in   varchar2 DEFAULT NULL
   ) return varchar 
   is
       my_log_msg  varchar2(32767) := REPLACE (log_msg, '"', '""');
       my_log_entry varchar2(32767);
       my_timestamp varchar(256);
       -- my_text_field_end_separator varchar)  := '",';
   begin
      select to_char (current_timestamp, 'YYYY-MM-DD HH24:MI:SSXFF') into my_timestamp from sys.dual ;
      -- dbms_output.put_line('my_timestamp pre-t'||  my_timestamp);
      my_timestamp := replace(my_timestamp, ' ','T');
      --      dbms_output.put_line('my_timestamp '||  my_timestamp);
      my_log_entry :=
           -- log_seq_nbr  || ','  || 
           log_level    || ',' || 
           job_log_id   || ',' || 
           job_msg_id   || ',' || 
           line_number  || ','  || 
           '"' ||my_timestamp || '",' || 
           '"' || my_log_msg   || '",' ||
           '"' || caller_name  || '",' || 
           '"' || call_stack || '"';
	 -- dbms_output.put_line('log entry: ' || my_log_entry);
         return my_log_entry;
end;
/
--#<
show errors
--#>
