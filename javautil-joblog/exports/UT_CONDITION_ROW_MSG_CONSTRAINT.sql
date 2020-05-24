--------------------------------------------------------
--  Constraints for Table UT_CONDITION_ROW_MSG
--------------------------------------------------------

  ALTER TABLE "UT_CONDITION_ROW_MSG" MODIFY ("UT_CONDITION_RUN_STEP_ID" NOT NULL ENABLE);
  ALTER TABLE "UT_CONDITION_ROW_MSG" ADD PRIMARY KEY ("UT_CONDITION_RUN_STEP_ID", "TABLE_PK")
  USING INDEX  ENABLE;
