--------------------------------------------------------
--  DDL for Table UT_CONDITION_RUN_STEP
--------------------------------------------------------

  CREATE TABLE "UT_CONDITION_RUN_STEP" 
   (	"UT_CONDITION_RUN_STEP_ID" NUMBER(9,0), 
	"UT_CONDITION_ID" NUMBER(*,0), 
	"UT_CONDITION_RUN_ID" NUMBER(*,0), 
	"START_TS" TIMESTAMP (6), 
	"END_TS" TIMESTAMP (6)
   ) ;
