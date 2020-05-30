--------------------------------------------------------
--  Ref Constraints for Table JOB_MSG
--------------------------------------------------------

  ALTER TABLE "JOB_MSG" ADD CONSTRAINT "JOB_MSG_JOB_LOG_FK" FOREIGN KEY ("JOB_LOG_ID")
	  REFERENCES "JOB_LOG" ("JOB_LOG_ID") ENABLE;
  ALTER TABLE "JOB_MSG" ADD CONSTRAINT "JOB_FK" FOREIGN KEY ("JOB_STEP_ID")
	  REFERENCES "JOB_STEP" ("JOB_STEP_ID") ENABLE;
