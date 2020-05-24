--------------------------------------------------------
--  DDL for Table CURSOR_INFO
--------------------------------------------------------

  CREATE TABLE "CURSOR_INFO" 
   (	"CURSOR_INFO_ID" NUMBER(9,0), 
	"CURSOR_INFO_RUN_ID" NUMBER(9,0), 
	"SQL_TEXT_HASH" VARCHAR2(44), 
	"PARSE_CPU_MICROS" NUMBER(9,0), 
	"PARSE_ELAPSED_MICROS" NUMBER(9,0), 
	"PARSE_BLOCKS_READ" NUMBER(9,0), 
	"PARSE_CONSISTENT_BLOCKS" NUMBER(9,0), 
	"PARSE_CURRENT_BLOCKS" NUMBER(9,0), 
	"PARSE_LIB_MISS" NUMBER(9,0), 
	"PARSE_ROW_COUNT" NUMBER(9,0), 
	"EXEC_CPU_MICROS" NUMBER(9,0), 
	"EXEC_ELAPSED_MICROS" NUMBER(9,0), 
	"EXEC_BLOCKS_READ" NUMBER(9,0), 
	"EXEC_CONSISTENT_BLOCKS" NUMBER(9,0), 
	"EXEC_CURRENT_BLOCKS" NUMBER(9,0), 
	"EXEC_LIB_MISS" NUMBER(9,0), 
	"EXEC_ROW_COUNT" NUMBER(9,0), 
	"FETCH_CPU_MICROS" NUMBER(9,0), 
	"FETCH_ELAPSED_MICROS" NUMBER(9,0), 
	"FETCH_BLOCKS_READ" NUMBER(9,0), 
	"FETCH_CONSISTENT_BLOCKS" NUMBER(9,0), 
	"FETCH_CURRENT_BLOCKS" NUMBER(9,0), 
	"FETCH_LIB_MISS" NUMBER(9,0), 
	"FETCH_ROW_COUNT" NUMBER(9,0), 
	"EXPLAIN_PLAN_HASH" VARCHAR2(44)
   ) ;
