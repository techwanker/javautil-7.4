--------------------------------------------------------
--  Ref Constraints for Table UT_CONDITION_RUN_STEP
--------------------------------------------------------

  ALTER TABLE "UT_CONDITION_RUN_STEP" ADD FOREIGN KEY ("UT_CONDITION_ID")
	  REFERENCES "UT_CONDITION" ("UT_CONDITION_ID") ENABLE;
  ALTER TABLE "UT_CONDITION_RUN_STEP" ADD FOREIGN KEY ("UT_CONDITION_RUN_ID")
	  REFERENCES "UT_CONDITION_RUN" ("UT_CONDITION_RUN_ID") ENABLE;
