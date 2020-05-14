--------------------------------------------------------
--  Constraints for Table JOB_LOG
--------------------------------------------------------

  ALTER TABLE "JOB_LOG" MODIFY ("IGNORE_FLG" NOT NULL ENABLE);
  ALTER TABLE "JOB_LOG" ADD CHECK ( ignore_flg in ('Y', 'N')) ENABLE;
  ALTER TABLE "JOB_LOG" ADD CONSTRAINT "JOB_LOG_PK" PRIMARY KEY ("JOB_LOG_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "JOB_LOG" ADD CONSTRAINT "JOB_LOG_TOKEN_UQ" UNIQUE ("JOB_TOKEN")
  USING INDEX  ENABLE;
