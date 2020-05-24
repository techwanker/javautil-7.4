--------------------------------------------------------
--  Ref Constraints for Table CURSOR_STAT
--------------------------------------------------------

  ALTER TABLE "CURSOR_STAT" ADD CONSTRAINT "CURSOR_STAT_CURSOR_INFO_FK" FOREIGN KEY ("CURSOR_INFO_ID")
	  REFERENCES "CURSOR_INFO" ("CURSOR_INFO_ID") ENABLE;
