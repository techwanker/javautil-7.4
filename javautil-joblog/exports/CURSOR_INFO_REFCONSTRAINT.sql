--------------------------------------------------------
--  Ref Constraints for Table CURSOR_INFO
--------------------------------------------------------

  ALTER TABLE "CURSOR_INFO" ADD CONSTRAINT "CURSOR_INFO_TEXT_FK" FOREIGN KEY ("SQL_TEXT_HASH")
	  REFERENCES "CURSOR_SQL_TEXT" ("SQL_TEXT_HASH") ENABLE;
  ALTER TABLE "CURSOR_INFO" ADD CONSTRAINT "CURSOR_INFO_PLAN_FK" FOREIGN KEY ("EXPLAIN_PLAN_HASH")
	  REFERENCES "CURSOR_EXPLAIN_PLAN" ("EXPLAIN_PLAN_HASH") ENABLE;
  ALTER TABLE "CURSOR_INFO" ADD CONSTRAINT "CURSOR_INFO_RUN_FK" FOREIGN KEY ("CURSOR_INFO_RUN_ID")
	  REFERENCES "CURSOR_INFO_RUN" ("CURSOR_INFO_RUN_ID") ENABLE;