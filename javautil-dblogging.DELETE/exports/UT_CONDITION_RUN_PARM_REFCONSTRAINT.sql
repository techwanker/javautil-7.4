--------------------------------------------------------
--  Ref Constraints for Table UT_CONDITION_RUN_PARM
--------------------------------------------------------

  ALTER TABLE "UT_CONDITION_RUN_PARM" ADD FOREIGN KEY ("UT_CONDITION_RUN_ID")
	  REFERENCES "UT_CONDITION_RUN" ("UT_CONDITION_RUN_ID") ENABLE;
