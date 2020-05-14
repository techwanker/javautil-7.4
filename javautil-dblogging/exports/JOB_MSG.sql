--------------------------------------------------------
--  DDL for Table JOB_MSG
--------------------------------------------------------

  CREATE TABLE "JOB_MSG" 
   (	"JOB_MSG_ID" NUMBER(9,0), 
	"JOB_LOG_ID" NUMBER(9,0), 
	"JOB_STEP_ID" NUMBER(9,0), 
	"LOG_MSG_ID" VARCHAR2(8), 
	"LOG_MSG" VARCHAR2(256), 
	"LOG_MSG_CLOB" CLOB, 
	"LOG_MSG_TS" TIMESTAMP (9), 
	"ELAPSED_TIME_MILLISECONDS" NUMBER(9,0), 
	"LOG_SEQ_NBR" NUMBER(18,0), 
	"CALLER_NAME" VARCHAR2(100), 
	"LINE_NBR" NUMBER(5,0), 
	"CALL_STACK" CLOB, 
	"LOG_LEVEL" NUMBER(2,0)
   ) ;
