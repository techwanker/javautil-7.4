--------------------------------------------------------
--  Constraints for Table LOGGER_DTL
--------------------------------------------------------

  ALTER TABLE "LOGGER_DTL" MODIFY ("LOG_LVL" NOT NULL ENABLE);
  ALTER TABLE "LOGGER_DTL" ADD PRIMARY KEY ("LOGGER_DTL_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "LOGGER_DTL" ADD CONSTRAINT "LOGGER_DTL_UNIQ" UNIQUE ("LOGGER_HDR_ID", "LOGGER_NM")
  USING INDEX  ENABLE;
