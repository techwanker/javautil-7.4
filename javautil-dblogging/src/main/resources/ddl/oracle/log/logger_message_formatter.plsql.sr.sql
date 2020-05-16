--#<
set echo on
--#>

 create or replace function  logger_message_formatter  (
      job_log_id    in   pls_integer,
      job_msg_id    in   pls_integer,
      log_msg       in   varchar,
      log_level     in   pls_integer,
      caller_name   in   varchar default null,
      line_number   in   pls_integer default null,
      call_stack    in   boolean default false,
      separator     in   varchar default ','
   ) return varchar 
   is
       my_log_msg  varchar2(32767) := REPLACE (log_msg, '"', '""');
       my_log_entry varchar2(32767);
       my_timestamp varchar(256);
       stack varchar(32767);
       -- my_text_field_end_separator varchar)  := '",';
   begin
      my_timestamp := format_timestamp(current_timestamp);

      if call_stack then 
          stack := dbms_utility.format_call_stack;
      end if;
      --      dbms_output.put_line('my_timestamp '||  my_timestamp);
      my_log_entry :=
           log_level    || separator ||
           '"' ||my_timestamp  || '"' || separator ||
           '"' || my_log_msg   || '"' || separator ||
           '"' || caller_name  || '"' || separator ||
           line_number  || separator  ||
           job_log_id   || separator ||
           job_msg_id   || separator ||
           '"' || stack || '"';
	 -- dbms_output.put_line('log entry: ' || my_log_entry);
         return my_log_entry;
end;
/
--#<
show errors
--#>
