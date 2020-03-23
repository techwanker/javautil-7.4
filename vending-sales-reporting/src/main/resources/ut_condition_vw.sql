create view vend_sales_condition
as
   select uc.ut_condition_id,
          uc.condition_name,
          uc.table_name,
          msg.table_pk,
          parm.parm_value_str etl_file_id
   from  ut_condition uc,
         ut_condition_run_step step,
         ut_condition_row_msg  msg,
         ut_condition_run_parm parm
    where uc.ut_condition_id = step.condition_id and
          uc.table_name  = 'ETL_SALE' and
          step.ut_condition_run_step_id =msg.ut_condition_run_step_id and
          parm.parm_nm = 'ETL_FILE_ID';

select * from vend_sales_condition;
