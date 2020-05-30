--------------------------------------------------------
--  Constraints for Table UT_CONDITION_RUN
--------------------------------------------------------

  ALTER TABLE "UT_CONDITION_RUN" MODIFY ("UT_CONDITION_RUN_ID" NOT NULL ENABLE);
  ALTER TABLE "UT_CONDITION_RUN" MODIFY ("START_TS" NOT NULL ENABLE);
  ALTER TABLE "UT_CONDITION_RUN" ADD PRIMARY KEY ("UT_CONDITION_RUN_ID")
  USING INDEX  ENABLE;
