--------------------------------------------------------
--  DDL for Table CURSOR_STAT
--------------------------------------------------------

  CREATE TABLE "CURSOR_STAT" 
   (	"CURSOR_INFO_ID" NUMBER(9,0), 
	"SEQ_NBR" NUMBER(3,0), 
	"OPERATION_DEPTH" NUMBER(2,0), 
	"OPERATION" VARCHAR2(100), 
	"CONSISTENT_READS" NUMBER(9,0), 
	"PHYSICAL_READS" NUMBER(9,0), 
	"PHYSICAL_WRITES" NUMBER(9,0), 
	"ELAPSED_MILLIS" NUMBER(12,0)
   ) ;
