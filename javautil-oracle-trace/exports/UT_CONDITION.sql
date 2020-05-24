--------------------------------------------------------
--  DDL for Table UT_CONDITION
--------------------------------------------------------

  CREATE TABLE "UT_CONDITION" 
   (	"UT_CONDITION_ID" NUMBER(9,0), 
	"CONDITION_NAME" VARCHAR2(30), 
	"TABLE_NAME" VARCHAR2(60), 
	"CONDITION_MSG" VARCHAR2(2000), 
	"SQL_TEXT" VARCHAR2(4000), 
	"NARRATIVE" VARCHAR2(2000), 
	"CONDITION_SEVERITY" NUMBER(1,0), 
	"CONDITION_FORMAT_STR" VARCHAR2(256), 
	"CORRECTIVE_ACTION" VARCHAR2(2000)
   ) ;