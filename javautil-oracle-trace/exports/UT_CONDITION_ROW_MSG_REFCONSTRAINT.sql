--------------------------------------------------------
--  Ref Constraints for Table UT_CONDITION_ROW_MSG
--------------------------------------------------------

  ALTER TABLE "UT_CONDITION_ROW_MSG" ADD FOREIGN KEY ("UT_CONDITION_RUN_STEP_ID")
	  REFERENCES "UT_CONDITION_RUN_STEP" ("UT_CONDITION_RUN_STEP_ID") ENABLE;
