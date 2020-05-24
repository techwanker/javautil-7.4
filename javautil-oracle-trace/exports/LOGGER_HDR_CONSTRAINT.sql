--------------------------------------------------------
--  Constraints for Table LOGGER_HDR
--------------------------------------------------------

  ALTER TABLE "LOGGER_HDR" MODIFY ("LOGGER_SET_NM" NOT NULL ENABLE);
  ALTER TABLE "LOGGER_HDR" MODIFY ("DEFAULT_LVL" NOT NULL ENABLE);
  ALTER TABLE "LOGGER_HDR" ADD PRIMARY KEY ("LOGGER_HDR_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "LOGGER_HDR" ADD UNIQUE ("LOGGER_SET_NM")
  USING INDEX  ENABLE;
