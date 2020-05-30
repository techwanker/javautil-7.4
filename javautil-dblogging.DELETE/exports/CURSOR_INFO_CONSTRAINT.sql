--------------------------------------------------------
--  Constraints for Table CURSOR_INFO
--------------------------------------------------------

  ALTER TABLE "CURSOR_INFO" MODIFY ("CURSOR_INFO_ID" NOT NULL ENABLE);
  ALTER TABLE "CURSOR_INFO" ADD CONSTRAINT "CURSOR_INFO_PK" PRIMARY KEY ("CURSOR_INFO_ID")
  USING INDEX  ENABLE;
