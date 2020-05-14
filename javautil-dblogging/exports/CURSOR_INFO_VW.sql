--------------------------------------------------------
--  DDL for View CURSOR_INFO_VW
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "CURSOR_INFO_VW" ("CURSOR_INFO_ID", "SQL_TEXT", "EXPLAIN_PLAN", "PARSE_CPU_MICROS", "PARSE_ELAPSED_MICROS", "PARSE_BLOCKS_READ", "PARSE_CONSISTENT_BLOCKS", "PARSE_CURRENT_BLOCKS", "PARSE_LIB_MISS", "PARSE_ROW_COUNT", "EXEC_CPU_MICROS", "EXEC_ELAPSED_MICROS", "EXEC_BLOCKS_READ", "EXEC_CONSISTENT_BLOCKS", "EXEC_CURRENT_BLOCKS", "EXEC_LIB_MISS", "EXEC_ROW_COUNT", "FETCH_CPU_MICROS", "FETCH_ELAPSED_MICROS", "FETCH_BLOCKS_READ", "FETCH_CONSISTENT_BLOCKS", "FETCH_CURRENT_BLOCKS", "FETCH_LIB_MISS", "FETCH_ROW_COUNT", "CPU_MICROS", "ELAPSED_MICROS", "BLOCKS_READ", "CONSISTENT_BLOCKS", "CURRENT_BLOCKS", "LIB_MISS", "ROW_COUNT") AS 
  select 
	cursor_info.cursor_info_id,
    cursor_sql_text.sql_text,
    explain_plan,
    parse_cpu_micros,
    parse_elapsed_micros,
    parse_blocks_read,
    parse_consistent_blocks,
    parse_current_blocks,
    parse_lib_miss,
    parse_row_count,
    exec_cpu_micros,
    exec_elapsed_micros,
    exec_blocks_read,
    exec_consistent_blocks,
    exec_current_blocks,
    exec_lib_miss,
    exec_row_count,
    fetch_cpu_micros,
    fetch_elapsed_micros,
    fetch_blocks_read,
    fetch_consistent_blocks,
    fetch_current_blocks,
    fetch_lib_miss,
    fetch_row_count,
    parse_cpu_micros + exec_cpu_micros + fetch_cpu_micros cpu_micros ,
    parse_elapsed_micros + exec_elapsed_micros + fetch_elapsed_micros elapsed_micros ,
    parse_blocks_read + exec_blocks_read + fetch_blocks_read blocks_read ,
    parse_consistent_blocks + exec_consistent_blocks + fetch_consistent_blocks consistent_blocks ,
    parse_current_blocks + exec_current_blocks + fetch_current_blocks current_blocks ,
    parse_lib_miss + exec_lib_miss + fetch_lib_miss lib_miss ,
    parse_row_count + exec_row_count + fetch_row_count row_count
from cursor_info,
     cursor_sql_text,
     cursor_explain_plan
 where cursor_info.sql_text_hash = cursor_sql_text.sql_text_hash and
       cursor_info.explain_plan_hash = cursor_explain_plan.explain_plan_hash
;
