--------------------------------------------------------
--  Constraints for Table CURSOR_EXPLAIN_PLAN
--------------------------------------------------------

  ALTER TABLE "CURSOR_EXPLAIN_PLAN" MODIFY ("EXPLAIN_PLAN_HASH" NOT NULL ENABLE);
  ALTER TABLE "CURSOR_EXPLAIN_PLAN" MODIFY ("EXPLAIN_PLAN" NOT NULL ENABLE);
  ALTER TABLE "CURSOR_EXPLAIN_PLAN" ADD CONSTRAINT "CURSOR_EXPLAIN_PLAN_PK" PRIMARY KEY ("EXPLAIN_PLAN_HASH")
  USING INDEX  ENABLE;