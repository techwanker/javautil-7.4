--------------------------------------------------------
--  Ref Constraints for Table JOB_STEP
--------------------------------------------------------

  ALTER TABLE "JOB_STEP" ADD FOREIGN KEY ("CURSOR_INFO_RUN_ID")
	  REFERENCES "CURSOR_INFO_RUN" ("CURSOR_INFO_RUN_ID") ENABLE;
  ALTER TABLE "JOB_STEP" ADD CONSTRAINT "STEP_STATUS_FK" FOREIGN KEY ("JOB_LOG_ID")
	  REFERENCES "JOB_LOG" ("JOB_LOG_ID") ENABLE;
