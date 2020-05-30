--------------------------------------------------------
--  Constraints for Table CURSOR_INFO_RUN
--------------------------------------------------------

  ALTER TABLE "CURSOR_INFO_RUN" MODIFY ("CURSOR_INFO_RUN_ID" NOT NULL ENABLE);
  ALTER TABLE "CURSOR_INFO_RUN" ADD CONSTRAINT "CURSOR_INFO_RUN_ID" PRIMARY KEY ("CURSOR_INFO_RUN_ID")
  USING INDEX  ENABLE;
