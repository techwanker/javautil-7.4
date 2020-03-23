declare
     
        processName varchar = "Process Name";
        cursor utc_cur := "select * from user_tab_columns, user_tables where rownum < 100");
        cursor atc_cur :=  "select count(*) from all_tab_columns");
begin
        dblogger.prepareConnection();
        DBMS_MONITOR.session_trace_enable(waits=>TRUE, binds=>FALSE);
        dblogger.startJobLogging("anonymous", null, null,  4);
        dblogger.setModule("SplitLoggerTest", "simple example");
        dblogger.setAction("Some work");
        dblogger.insertStep("Full join", "Meaningless busy work", getClass().getName());
        loop
            fetch utc_cur into utc_rec;
            exit when 
        end loop;
        //ConnectionUtil.exhaustQuery(appConnection, "select * from user_tab_columns, user_tables where rownum < 100");

       dblogger.setAction("Another set of work");
        loop
            fetch utc_cur into utc_rec;
            exit when 
        end loop;
        // End the job
        dblogger.endJob();
end;
/
