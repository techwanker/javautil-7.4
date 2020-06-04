# dblogging 

Application instrumentation is essential to performance monitoring, unfortunately this is often
performed by throwing in some logging statements using a java logging framework or wrapper, such as slf,

This however fails to capture any information essential to end-to-end monitoring as it omits what is
generally the biggest source of latency, the relational database.   What statements are being executed,
how long do they take, which statements take up the bulk of the resources in aggregate (an under-performing statement invoked thousands of times an hour is not uncommon).

Oracle provides, at great expense, the ASH subsystem and even that does not associated the sql statements to
the application code.

This utility provides a simple Application Program Interface to allow you to record performance information
in a simple, low overhead fashion from any java program or any program that allows pl/sql calls.

Thus a few judicious additions to an Oracle Form or a batch job can provide the foundation of information
necessary to establish where database resources are being consumed.

Resolution of these matters is a different matter and may involve gathering statistics, 
altering execution plans, creating or eliminating indexes or rewriting SQL.  

SGA parameters may have to be changed.


https://asktom.oracle.com/Misc/instrumentation.html
# TODO



## Document Organization

We will introduce the types of logging, show examples of how to instrument your code and 
what type of output is generated.

Then we will guide you through the installation and options.  

## Overview of logging types

### Flat file logging

Writes log messages to a text file on the database server.  

Includes: 

  * log_seq_nbr 
  * log_level   
  * job_log_id  
  * log_msg_id  
  * line_number
  * current_timestamp, 'YYYY-MM-DDTHH24:MI:SSXFF'
  * log_msg  
  * caller_name  optional
  * call_stack optional

This will allow you to easily write log messages from pl/sql in stored procedures, functions, packages and 
with most of the flexibility and ease of using a java logging framework.

### Database Logging

Record jobs and their steps, how long each step took to execute and optionally extremely detailed
information about every database operation as an oracle trace file may be parsed and stored in the 
log repository.

The log repository may be on the same oracle database server, even the same schema 
using the same connection as it uses autonomous transactions, or in postgresql or h2.

### Oracle trace information

The third type of logging is an extension of database logging and stores oracle trace information
a relational database.

Oracle tracing is turned on and the trace files parsed and aggregated and stored in tables associated
with the various job steps.  

* oracle
* h2
* postgresql


## Logging type details 

### Flat file log 

#### Flat file logging example

Before diving into the details, here is a simple example of usage.

##### Input 
    set serveroutput on 
    declare
           long_msg clob := 'this is an absurdly long message, ' || 
                    ' exceeding the length of the log_msg field ' ||
                    ' The quick brown fox jumped over the lazy dog. '; 
        
        my_log_file_name varchar(4096); 
    begin
        my_log_file_name := pllogger.open_log_file('log_to_file_only.text');
        pllogger.set_filter_level(9); -- all messages should go to log file
        dbms_output.put_line('caller is anoymous');
        pllogger.info('anonymous',$$PLSQL_LINE,'begin loop');
        pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,long_msg); 
    
        for i in 1..10  
        loop
            pllogger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'i is ' || to_char(i));
        end loop; 
    exception when others
    then
        -- a severe condition is not necessarily fatal
        pllogger.severe($$PLSQL_UNIT,$$PLSQL_LINE,sqlerrm);
        pllogger.close_log_file();
        raise;
    end;
    /

#### The log file 

The preceding will produce this Comma Separated Values (CSV) file...


    "log_level","job_log_id","job_msg_id","line_number","timestamp","log_msg","caller_name","call_stack"
    4,,,17,"2019-10-26T21:15:14.187497","begin loop","anonymous",""
    4,,,18,"2019-10-26T21:15:14.187652","this is an absurdly long message,  exceeding the length of the log_msg field  this should be inserted into the log_msg_clob column.   This message is part of  a unit test of from sample_job_02 of the logging package.   I am running out of  interesting stuff to say so I will just write meaningless  stuff for a little while.  The quick brown fox jumped over the lazy dog. ","LOG_TO_FILE_ONLY",""
    7,,,22,"2019-10-26T21:15:14.187723","i is 1","LOG_TO_FILE_ONLY",""
    7,,,22,"2019-10-26T21:15:14.187782","i is 2","LOG_TO_FILE_ONLY",""
    7,,,22,"2019-10-26T21:15:14.187837","i is 3","LOG_TO_FILE_ONLY",""

### Formatting the log file

The format of the log file is determined by the function logger_message_formatter.

The default is CSV, Comma separated values as 

    my_log_entry :=
        log_level   || ',"' ||
        job_log_id  || ',"' || 
        log_msg_id  || '",' ||
        line_number || ',' ||
        to_char (current_timestamp, 'YYYY-MM-DDTHH24:MI:SSXFF') || ',"' ||
        my_log_msg  || '",' ||
        caller_name || '",' ||
        call_stack;

#### pllogger package
This facility is provide for pl/sql programmers to provide the ability to write 
formatted trace messages including:

* log_level 0 to 9 also called: 
  * pllogger.G_SEVERE        CONSTANT PLS_INTEGER := 1;
  * pllogger.G_WARNING       CONSTANT PLS_INTEGER := 2;
  * pllogger.GA_INFO         CONSTANT PLS_INTEGER := 4;
  * pllogger.G_SNAP         CONSTANT PLS_INTEGER := 5;
  * pllogger.G_ENTERING     CONSTANT PLS_INTEGER := 6;
  * pllogger.G_EXITING      CONSTANT PLS_INTEGER := 6;
  * pllogger.G_FINE         CONSTANT PLS_INTEGER := 7;
  * pllogger.G_FINER        CONSTANT PLS_INTEGER := 8;
  * pllogger.G_FINEST       CONSTANT PLS_INTEGER := 9;
  * pllogger.G_NONE         CONSTANT PLS_INTEGER := 10 ;
  * log_msg_Id TODO if persisted what is this 
  * line number of caller
  * a timestamp for the message in ISO-8601 date time format example  2008-09-15T15:53:00000
  the log message
  * the name of the invoker 
  * the pl/sql call stack


## The package

.. code-block:: plsql
    
    CREATE OR REPLACE PACKAGE pllogger AS
    --
        G_SEVERE       CONSTANT PLS_INTEGER := 1 ;
        G_WARNING      CONSTANT PLS_INTEGER := 2 ;
        G_INFO         CONSTANT PLS_INTEGER := 4 ;
        G_SNAP         CONSTANT PLS_INTEGER := 5 ;
        G_ENTERING     CONSTANT PLS_INTEGER := 6 ;
        G_EXITING      CONSTANT PLS_INTEGER := 6 ;
        G_FINE         CONSTANT PLS_INTEGER := 7 ;
        G_FINER        CONSTANT PLS_INTEGER := 8 ;
        G_FINEST       CONSTANT PLS_INTEGER := 9 ;
        G_NONE         CONSTANT PLS_INTEGER := 10 ;
    
        -- setup must open_log_file, 
        -- must close when done
        FUNCTION open_log_file(p_file_name in varchar)  return varchar;
        
        PROCEDURE set_dbms_output_level(
            p_level  IN        PLS_INTEGER ) ;
       
        PROCEDURE set_filter_level (  
            p_level  IN PLS_INTEGER ) ;
      
        PROCEDURE set_record_level (
            p_level  IN        PLS_INTEGER ) ;
     
        -- Write messages
        
        PROCEDURE severe (
            p_unit          IN VARCHAR2,
            p_line          IN PLS_INTEGER,
            p_log_msg       IN VARCHAR2 DEFAULT '',
            p_record_stack  IN BOOLEAN DEFAULT FALSE ) ;
            
        PROCEDURE warning (
            p_unit          IN        VARCHAR2,
            p_line          IN        PLS_INTEGER,
            p_log_msg       IN        VARCHAR2 DEFAULT '',
            p_record_stack  IN        BOOLEAN DEFAULT FALSE ) ;
            
        PROCEDURE info (
            p_unit          IN        VARCHAR2,
            p_line          IN        PLS_INTEGER,
            p_log_msg       IN        VARCHAR2 DEFAULT '',
            p_record_stack  IN        BOOLEAN DEFAULT FALSE ) ;
            
        PROCEDURE entering (
            p_unit         IN VARCHAR2,
            p_line         IN PLS_INTEGER,
            p_log_msg      IN VARCHAR2 DEFAULT '',
            p_record_stack IN BOOLEAN DEFAULT FALSE,
            p_set_action   IN BOOLEAN DEFAULT TRUE ) ;
        --
        PROCEDURE exiting (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
        --
        PROCEDURE fine (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
        --
        PROCEDURE finer (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
        --
        PROCEDURE finest (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
            
        -- close when done
        PROCEDURE close_log_file;
            
        function get_directory_path return varchar;
    
        function basename (p_full_path in varchar2,
                           p_suffix    in varchar2 default null,
                           p_separator in char default '/') return varchar2;
        
    END pllogger ;
    /
    --#<
    show errors
    --#>

## Database logs

We will start with an example program and show what is logged.

### Java Example

.. code-block:: java

    package org.javautil.dblogging;
    
    import java.sql.Connection;
    import java.sql.SQLException;
    
    import org.javautil.core.sql.Binds;
    import org.javautil.core.sql.ConnectionUtil;
    import org.javautil.core.sql.SqlStatement;
    import org.javautil.dblogging.logger.Dblogger;
    import org.javautil.util.NameValue;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    public class DbloggerForOracleExample {
    
    	private Dblogger dblogger;
    	private Connection connection;
    	private String processName;
    	private boolean testAbort = false;
    	private int traceLevel;
    	private final Logger logger = LoggerFactory.getLogger(getClass());
    
    	public DbloggerForOracleExample(Connection connection, Dblogger dblogger, String processName, boolean testAbort,
    			int traceLevel) {
    		this.connection = connection;
    		this.dblogger = dblogger;
    		this.processName = processName;
    		this.testAbort = testAbort;
    		this.traceLevel = traceLevel;
    	}
    
    	public long process() throws SQLException {
    		dblogger.prepareConnection();
    		long id = 0;
    
    		try {
    			id = dblogger.startJobLogging(processName, getClass().getName(), "ExampleLogging", null, 12);
    			logger.debug("============================jobId: {}", id);
    			limitedFullJoin();
    			fullJoin();
    			userTablesCount();
    			if (testAbort) {
    				int x = 1 / 0;
    			}
    			logger.debug("calling endJob");
    			dblogger.endJob();
    		} catch (Exception e) {
    			logger.error(e.getMessage());
    			e.printStackTrace();
    			dblogger.abortJob(e);
    			throw e;
    		}
    		return id;
    	}
    
    	/**
    	 * This will set the v$session.action
    	 */
    	private void limitedFullJoin() throws SQLException {
    		logger.debug("limitedFullJoin =============");
    		dblogger.setAction("actionNoStep");
    		ConnectionUtil.exhaustQuery(connection, "select * from user_tab_columns, user_tables where rownum < 200");
    		dblogger.setAction(null);  // no longer performing that action, so clear 
    		logger.debug("limitedFullJoin complete =============");
    	}
    
    	private void fullJoin() throws SQLException {
    		logger.debug("fullJoinBegins =============");
    		// TODO insertStep should set the action 
    		dblogger.insertStep("fullJoin", "fullJoin", getClass().getName());
    		ConnectionUtil.exhaustQuery(connection, "select * from user_tab_columns, user_tables");
    		dblogger.finishStep();
    		logger.debug("fullJoin complete =============");
    	}
    
    	private void userTablesCount() throws SQLException {
    		dblogger.insertStep("count full", "userTablesCount", getClass().getName());
    		ConnectionUtil.exhaustQuery(connection, "select count(*) dracula from user_tables");
    		dblogger.finishStep();
    		// TODO support implicit finish step
    	}
    
    	NameValue getJobLog(Connection connection, long id) throws SQLException {
    		final String sql = "select * from job_log " + "where job_log_id = :job_stat_id";
    		final SqlStatement ss = new SqlStatement(connection, sql);
    		Binds binds = new Binds();
    		binds.put("job_stat_id", id);
    		final NameValue retval = ss.getNameValue();
    		ss.close();
    		return retval;
    	}
    }

### job_tables

In the interest of expediency we have a quick listing of the job tables.


    SQL> describe job_log
     Name                                      Null?    Type
     ----------------------------------------- -------- ----------------------------
     JOB_LOG_ID                                NOT NULL NUMBER(9)
     SCHEMA_NAME                                        VARCHAR2(30)
     PROCESS_NAME                                       VARCHAR2(128)
     THREAD_NAME                                        VARCHAR2(128)
     STATUS_MSG                                         VARCHAR2(256)
     STATUS_TS                                          TIMESTAMP(9)
     END_TS                                             TIMESTAMP(9)
     ELAPSED_MILLIS                                     NUMBER(9)
     SID                                                NUMBER
     SERIAL_NBR                                         NUMBER
     IGNORE_FLG                                         VARCHAR2(1)
     MODULE_NAME                                        VARCHAR2(64)
     CLASSNAME                                          VARCHAR2(255)
     TRACEFILE_NAME                                     VARCHAR2(4000)
     TRACEFILE_DATA                                     CLOB
     TRACEFILE_JSON                                     CLOB
     ABORT_STACKTRACE                                   CLOB
    
    SQL> describe job_step
     Name                                      Null?    Type
     ----------------------------------------- -------- ----------------------------
     JOB_STEP_ID                               NOT NULL NUMBER(9)
     JOB_LOG_ID                                         NUMBER(9)
     STEP_NAME                                          VARCHAR2(64)
     CLASSNAME                                          VARCHAR2(256)
     STEP_INFO                                          VARCHAR2(2000)
     START_TS                                           TIMESTAMP(9)
     END_TS                                             TIMESTAMP(9)
     DBSTATS                                            CLOB
     STEP_INFO_JSON                                     CLOB
     CURSOR_INFO_RUN_ID                                 NUMBER(9)
     STACKTRACE                                         VARCHAR2(4000)
    
    SQL> describe job_msg;
     Name                                      Null?    Type
     ----------------------------------------- -------- ----------------------------
     JOB_MSG_ID                                         NUMBER(9)
     JOB_LOG_ID                                NOT NULL NUMBER(9)
     LOG_MSG_ID                                         VARCHAR2(8)
     LOG_MSG                                            VARCHAR2(256)
     LOG_MSG_CLOB                                       CLOB
     LOG_MSG_TS                                         TIMESTAMP(9)
     ELAPSED_TIME_MILLISECONDS                          NUMBER(9)
     LOG_SEQ_NBR                               NOT NULL NUMBER(18)
     CALLER_NAME                                        VARCHAR2(100)
     LINE_NBR                                           NUMBER(5)
     CALL_STACK                                         CLOB
     LOG_LEVEL                                          NUMBER(2)

An ERD is near the end of this document.

TODO run the python with the comments     

Each job has one job_log entry and one or more job_steps.

Job steps may have associated log messages.

### Analyzing the logs

Separate utilities are used to analyzed the logs. A very useful tool is 
javautil-condition-identification.

Did any job abort?

What is the trend on elapsed times? 

How do elapsed times vary based on time of day?

Getting deeper, with trace information one can drill down to the details, we will
cover that later.

## Tracefile generation and persistence

This utility provides the information to the Oracle Performance specialist to identify the root cause of the problem, how to repair is another speciality.


# Installation of database artifacts for oracle

These files may be found under *src/main/resources/ddl/oracle*

The script that runs them all is *install.sql*

    set echo on 
    @prepare_connection.sql
    @my_session_info.sql
    @dblogger_uninstall.sr.sql
    @logger_message_formatter.plsql.sr.sql
    @dblogger_install_tables.sr.sql
    @dblogger_install.pks.sr.sql
    @dblogger_install.pkb.sr.sql
    @logger_persistence.pks.sr.sql
    @logger_persistence.pkb.sr.sql

## prepare_connnection

prepare-connection provides one procedure.   
This will call dbms_session.clear_context for each context variable, 
restoring the context for a connection returned from a connection_pool to
the state the of an initially opened connection.  

Connection pools do not generally clear this information out as it is Oracle specific.

## my_session_info.sql

creates the view *my_session_info* to allow the connected user to obtain the v$session record for the current connection.

## logger_message_formatter

Provides  the *logger_message_formatter* function, which creates a single string from all of the logging 
parameters and makes a call to dbms_output.put_line and then returns the formatted message.

## dblogger_install_tables

Creates the job and job step tables and views

#### sequences

cursor_info_run_id_seq;
cursor_info_id_seq;
job_log_id_seq;
job_msg_id_seq;
job_step_id_seq;

#### tables

* cursor_explain_plan 
* cursor_sql_text 
* cursor_info_run
* cursor_info 
* cursor_stat 
* job_log     
* job_msg     
* job_step     


#### views

* cursor_info_vw  
* job_step_vw 
* job_log_vw  


## Job Logging

Logging information may be written to a text file, stored in a  database and written to the oracle trace file.

### Steps start job logging.

    public long sampleUsage(Dblogger dblogger, Connection appConnection) throws SqlSplitterException, Exception {
        dblogger.prepareConnection();
        final String processName = "Process Name";
        // Start the job

        final long logJobId = dblogger.startJobLogging(processName,getClass().getName(), null, null,  4);
        dblogger.setModule("SplitLoggerTest", "simple example");
        dblogger.setAction("Some work");
        dblogger.insertStep("Full join", "Meaningless busy work", getClass().getName());
        ConnectionUtil.exhaustQuery(appConnection, "select * from user_tab_columns, user_tables where rownum < 100");

        dblogger.setAction("Another set of work");
        ConnectionUtil.exhaustQuery(appConnection, "select count(*) from all_tab_columns");
        // End the job
        dblogger.endJob();
        return logJobId;
    }

job logging persistence has a bit of indirection

     
## Installation

### Repositories

RDBMS persistence support is provided for Oracle, H2 and postgresql 

H2 is a lightweight database and may be used to eliminate the need for support of another Oracle Database.

Postgresql is a high end database that requires minimal installation and administration.

You should probable not compound your problem with yet another Oracle install, but if your DBA will allow you a schema in your database for
logging, you don't have to learn anything else.

The Oracle database could be the same instance as the application being monitored, but this may raise
some objections to the application DBA.


### Oracle logging repository

If the logging data is to be persisted in Oracle, the tables must be created and some packages created.

#### Job log tables

1. job_log
#. job_msg
#. job step

The granularity of job step is left to the invoker.  
As the overhead is very low, there is no reason to
be parsimonious with identification, it's a simple one line call in the user app.  


These records can be reviewed for job success or failure and form a historical basis of time elapsed.

This may be used as a starting pointing in locating "what processes are using the time?"  

Additionally they constitute a base performance metric from which runtime 
degradation or periodic anomalous runs may be identified.

Data is committed by calls from java to the package logger, provided here.

The package creates autonomous commits and hence may be safely called using the same 
connection as the application.

### logging package

The logger package provides the following:

	These primarily set information in the SGA and enable oracle session tracing.

begin_java_java

#### change v$session information

procedure prepare_connection;

set_module
set action

## Trace Repository


#### Oracle performance tables

* cursor_explain_plan 
* cursor_sql_text 
* cursor_info_run
* cursor_info 
* cursor_stat 

## logger_persistence package

The logger persistence package provides an API for writing to various tables using 
autonomous transactions.



    procedure save_job_log (
        p_job_log_id   in number,
   		p_schema_name  in varchar2,
    	p_process_name in varchar2,
        p_classname    in varchar2,
        p_module_name  in varchar2,
        p_status_msg   in varchar2,
        p_thread_name  in varchar2,
        p_trace_level  in pls_integer default logger.G_INFO,
        p_tracefile_name in varchar2,
        p_sid          in pls_integer
    );

The source of work is indentifable down to the java thread.

	function save_job_step (
        p_job_log_id  in pls_integer, 
        p_step_name   in varchar, 
        p_step_info   in varchar, 
        p_classname   in varchar,     
        p_start_ts    in timestamp,
        p_stacktrace  in varchar
   ) return number;  

    procedure finish_step

    procedure end_job(p_elapsed_milliseconds in pls_integer)

	procedure abort_job(p_elapsed_milliseconds in pls_integer,p_stacktrace in varchar);

### Install Oracle JDBC

[See this post](https://blogs.oracle.com/dev2dev/get-oracle-jdbc-drivers-and-ucp-from-oracle-maven-repository-without-ides) to use Oracle JDBC properly. Or, you could download the JAR file, and then execute this command:

TODO the script to locatge 
`mvn install:install-file -DgroupId=com.oracle -DartifactId=oracle-jdbc8 -Dversion=12c -Dpackaging=jar -Dfile=<THE_JDBC_JAR_LOCATION>`

Notations in job .sql script used by sqlrunner.

# Security in production

# User priviliges

# Performance

# Tools and concepts

User should be familiar with v$ssession view, tkprof command line utility

# Connection Pools

## After Getting a connection

### Contexts

If a session is being used as part of a connection pool and the state of
its contexts are not reinitialized, this can lead to unexpected
behavior.

### Packages

Sessions have the ability to alter package state by amending the
values of package variables. If a session is being used as part of a
connection pool and the state of its packages are not reinitialized,
this can lead to unexpected behavior. To solve this, Oracle provides
the dbms_session.reset_package procedure.

The dbloggging provided procedure clears all context variables and resets package state.

Connections must be reset immediately after being obtained from a
connection pool

In src/main/resources/ddl/oracle/prepare_connection

### Convenience Procedure

    CREATE OR REPLACE PROCEDURE prepare_connection
    AS
        context_info DBMS_SESSION.AppCtxTabTyp;
        info_count PLS_INTEGER;
        indx PLS_INTEGER;
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
    END;

create public synonym prepare_connection for prepare_connection;
grant execute on prepare_connection to public;
```

### Zaxxer

TODO how to call this procedure in the connection pool

## DBMS\_SESSION
-------------

## Identifier

SET_IDENTIFIER and CLEAR_IDENTIFIER procedures to allow the real user
to be associated with a session, regardless of what database user was
being used for the connection.

## Metrics

try {
:   String e2eMetrics[] = new
    String[OracleConnection.END\_TO\_END\_STATE\_INDEX\_MAX];
    e2eMetrics[OracleConnection.END\_TO\_END\_ACTION\_INDEX] = null;
    e2eMetrics[OracleConnection.END\_TO\_END\_MODULE\_INDEX] = null;
    e2eMetrics[OracleConnection.END\_TO\_END\_CLIENTID\_INDEX] = null;
    ((OracleConnection) conn).setEndToEndMetrics(e2eMetrics,
    Short.MIN\_VALUE);

} catch (SQLException sqle) {
:   // Do something...

}

0 - No trace. Like switching sql\_trace off. 2 - The equivalent of
regular sql\_trace. 4 - The same as 2, but with the addition of bind
variable values. 8 - The same as 2, but with the addition of wait
events. 12 - The same as 2, but with both bind variable values and wait
events.

Monitoring long running
<https://oracle-base.com/articles/11g/real-time-sql-monitoring-11gr1>

## References

<https://oracle-base.com/articles/misc/dbms_session>

<https://oracle-base.com/articles/misc/sql-trace-10046-trcsess-and-tkprof>


![logger_tables.png](logger_tables.png)


#Spring Developers

Oracle tracing is a powerful tool that logs detailed information about all calls
to the Oracle database.

In order to use this :

* one must turn on tracing for the current connection
* set the log file
* stop tracing
* call a service to store the trace

* store the raw trace file
* analyze the trace file
* store the analyzed trace file

## Logging to flat files 

### Overview

Logs messages using utl_file to a directory on the database server specified.

First the database directory is created and oracle is granted permission to read and
write it, then the ddl "create directory...." and "grant read, write on directory..."


### Examples

#### log_to_file_only.proc.sr.sql

### Input

    set serveroutput on
    set echo on
    create or replace procedure log_to_file_only is 
           long_msg clob := 'this is an absurdly long message, ' || 
                    ' interesting stuff to say so I will just write meaningless ' ||
                    ' stuff for a little while. ' ||
                    ' The quick brown fox jumped over the lazy dog. '; 
        
        my_log_file_name varchar(4096); 
    begin
        my_log_file_name := pllogger.open_log_file('log_to_file_only.text');
        pllogger.set_filter_level(9); -- all messages should go to log file
        pllogger.info('anonymous',$$PLSQL_LINE,'begin loop');
        pllogger.info($$PLSQL_UNIT,$$PLSQL_LINE,long_msg); 
        for i in 1..3  
        loop
            pllogger.fine($$PLSQL_UNIT,$$PLSQL_LINE,'i is ' || to_char(i));
        end loop; 
        pllogger.close_log_file();
    exception when others then
            -- a severe condition is not necessarily fatal
        pllogger.severe($$PLSQL_UINIT,$$PLSQL_LINE,sqlerrm);
        pllogger.close_log_file();
        raise;
    end;
    /
    show errors
    
    exec log_to_file_only();

##### Output 

    "log_level","job_log_id","job_msg_id","line_number","timestamp","log_msg","caller_name","call_stack"
    4,,,17,"2019-10-26T17:19:52.885607","begin loop","anonymous",""
    4,,,18,"2019-10-26T17:19:52.886020","this is an absurdly long message,  exceeding the length of the log_msg field  this should be inserted into the log_msg_clob column.   This message is part of  a unit test of from sample_job_02 of the logging package.   I am running out of  interesting stuff to say so I will just write meaningless  stuff for a little while.  The quick brown fox jumped over the lazy dog. ","LOG_TO_FILE_ONLY",""
    7,,,22,"2019-10-26T17:19:52.886197","i is 1","LOG_TO_FILE_ONLY",""
    7,,,22,"2019-10-26T17:19:52.886357","i is 2","LOG_TO_FILE_ONLY",""
    7,,,22,"2019-10-26T17:19:52.886502","i is 3","LOG_TO_FILE_ONLY",""

# TODO 
Tracing should do the following

* Begin with any transaction as annotated by @Transactional

#  Install 


    cd src/main/resources/ddl/oracle
    
    sqlplus $ORACLE_UID @ pllogger.pkgs.sr.sql
    sqlplus $ORACLE_UID @ pllogger.pkgb.sr.sql

   create directory job_msg_dir as '/common/scratch/ut_process_log_dir';
   grant write on directory to sr;
 
   should be granted by user, not by role.


* Configuring to use your database 
* Example schema

# TODO 
* security can't specify file name 
* need an agent to get the log files for remote users
* TODO escape double quotes in text fields 
* check for anomolous run-times using condition identification
* plot runtimes
* TODO describe microservices, multiple connections, tying them all together
* TODO describe using with spring 

