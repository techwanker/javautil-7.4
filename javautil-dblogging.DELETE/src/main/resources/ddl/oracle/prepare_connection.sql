-- https://docstore.mik.ua/orelly/oracle/guide8i/ch08_04.htm
/**
 * 
 * 
 */
set echo on
CREATE OR REPLACE PROCEDURE prepare_connection (p_logger_set_nm in varchar default null)
AS
   context_info DBMS_SESSION.AppCtxTabTyp;
   info_count PLS_INTEGER;
   indx PLS_INTEGER;

   cursor log_cur is
   select
         d.logger_nm,
         d.log_lvl
   from logger_dtl d,
        logger_hdr h
    where d.logger_hdr_id = h.logger_hdr_id
          and h.logger_set_nm = p_logger_set_nm;

BEGIN
   DBMS_SESSION.LIST_CONTEXT ( context_info, info_count);
   indx := context_info.FIRST;   
   LOOP
      EXIT WHEN indx IS NULL;
      DBMS_SESSION.CLEAR_CONTEXT(
         context_info(indx).namespace,
         context_info(indx).attribute,
         null 
      );
      indx := context_info.NEXT (indx);
   END LOOP;
   DBMS_SESSION.RESET_PACKAGE;  
 
   for logger_rec in log_cur
   loop
       DBMS_SESSION.SET_CONTEXT ( namespace => 'logger',
   				attribute => logger_rec.logger_nm,
                                value => to_char(logger_rec.log_lvl));

   end loop;
END;
/
show errors;

create public synonym prepare_connection for prepare_connection;
grant execute on prepare_connection to public;
