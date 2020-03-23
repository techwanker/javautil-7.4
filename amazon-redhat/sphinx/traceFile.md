# Trace Files

# References

* https://docs.oracle.com/cd/B28359_01/server.111/b28310/diag006.htm#ADMIN12484

To find the trace file for your current session:

    Submit the following query:

    SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Default Trace File';

    The full path to the trace file is returned.

To find all trace files for the current instance:

    Submit the following query:

    SELECT VALUE FROM V$DIAG_INFO WHERE NAME = 'Diag Trace';

    The path to the ADR trace directory for the current instance is returned.

To determine the trace file for each Oracle Database process:

    Submit the following query:

    SELECT PID, PROGRAM, TRACEFILE FROM V$PROCESS;

# Change the location of the trace files

* https://asktom.oracle.com/pls/apex/asktom.search?tag=trace-files-location

  sudo mkdir -p /var/log/oracle/trace

  sudo chgrp oracle_trace /var/log/oracle/trace 
  **TODO** should be by instance

  /opt/oracle/product/19c/dbhome_1/dbs


