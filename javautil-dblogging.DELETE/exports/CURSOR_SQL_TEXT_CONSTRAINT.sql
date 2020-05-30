--------------------------------------------------------
--  Constraints for Table CURSOR_SQL_TEXT
--------------------------------------------------------

  ALTER TABLE "CURSOR_SQL_TEXT" MODIFY ("SQL_TEXT_HASH" NOT NULL ENABLE);
  ALTER TABLE "CURSOR_SQL_TEXT" MODIFY ("SQL_TEXT" NOT NULL ENABLE);
  ALTER TABLE "CURSOR_SQL_TEXT" ADD CONSTRAINT "CURSOR_TEXT_PK" PRIMARY KEY ("SQL_TEXT_HASH")
  USING INDEX  ENABLE;
