--------------------------------------------------------
--  DDL for Table JOB_STEP
--------------------------------------------------------

  CREATE TABLE "JOB_STEP" 
   (	"JOB_STEP_ID" NUMBER(9,0), 
	"JOB_LOG_ID" NUMBER(9,0), 
	"STEP_NAME" VARCHAR2(64), 
	"CLASSNAME" VARCHAR2(256), 
	"STEP_INFO" VARCHAR2(2000), 
	"START_TS" TIMESTAMP (9), 
	"END_TS" TIMESTAMP (9), 
	"DBSTATS" CLOB, 
	"STEP_INFO_JSON" CLOB, 
	"CURSOR_INFO_RUN_ID" NUMBER(9,0), 
	"STACKTRACE" VARCHAR2(4000)
   ) ;
