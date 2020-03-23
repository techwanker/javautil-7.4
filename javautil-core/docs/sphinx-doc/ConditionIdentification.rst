Condition Identification
========================
Condition Identification supports the declarative identification of records that meet one or more criteria.

It works by running a set of SQL queries against the data and 
persisting the identification rule identifier and
the primary key for the records that satisfy the query.

Features
--------


Concepts
--------

Rule Group
**********

Run Parms
*********
Run Parms are bind variables used for the various rules.

Installation

Terminology

A rule group is a set of rules that collectively define the conditions to be isolated.

Metrics
*******
Metrics may be gathered for 
UT_TABLE_RULE
This table identifies the database table that contains the primary key for table that is being identified.

Simple Processor
----------------
The simple processor reads rules such as the following

This is a sample .yaml file

code::

    -   rule_name:  NO_CUSTOMER_TOTAL
        table_name: ETL_FILE
        msg:      No customer total record
        sql_text: >
            select etl_file_id
            from etl_file
            where etl_file_id = %(ETL_FILE_ID)s
            and not exists (
               select 'x'
               from etl_customer_tot
               where etl_file_id = %(ETL_FILE_ID)s
            )
        narrative: >
              There is no customer total record
        severity: 3
        format_str: "No customer total record in %s"
        java_format: "No customer total record for etl_file_id %s"

Rule Parts
**********


.. glossary::

   rule_name
        Identifies the rule, should be globally unique, but this is not enforced
   table_name
        the name of the database table from which the primary key is extracted
   sql_text
        a select statement that returns, minimally, the primary key for the table as the first result,
        other columns may be selected for formatting messages 
   severity
        this should be further documented but may be used in subsequent processing to determine 
        processing, such as refusing to post this record, or to reject an entire set of records



One file defines a set of rules, each of which are run.

The bind variables %(ETL_FILE_ID)s are in python postgres format in this example but
may be specified as :ETL_FILE_ID.  Bind variables are not case sensitive.

The Complete Processor has a web interface and a large variety of output formats.


Tables
------
ut_condition_run
****************
One record is created or a run

ut_condition_run_parm
*********************
This table stores the parameters used on a single run.

ut_condition
************ 
This table stores the rule as described above

ut_condition_run_step
*********************
Associates a run with the condition


ut_condition_row_msg
********************
Stores the primary key and references to the condition being identifieed



Full Project
------------
* The full project includes the ability to persist the rules in a database.

* Support for running multiple queries as threads

* Support for maximum execution time

* Gathering metrics for comparison of based data for determination of outliers and exceptional values.


Rule Processor


Invocation
Classes

Parallelism

Data Setup
Output Review

Fatal and Warn
 * fix schema tables with no surrogate key, no foreign keys etc. See exception processing.mer
 * document the package
 * document logging
 * document metrics
 * document ut_table_msg
 * document ut_table_report_sum
 * document exclusion rules
 * need functional area
 * document calling as a procedure
 * what does ut_query do with anything
 * no real support for ut_table_msg
 * item todo what is ds_table and why does ut_query reference it?
 * document ut_table_rule and hist figure out what all of these columns are used for


The Javautil Exception Generator allows you to set up simple rules to identify records or tables that fail
to meet business requirements.

Benefits

Pre-requisites
--------------
* Obtain javautil code
* configure machine
* item configure datasources


Overview
--------
 * getParms();
 * getRun();
 * getRules();
 * getBinds();
 * createProcessLog();
 * processRules();
 * updateRunStatus();
 * acknowledge()


Installation
------------
Creating the User
grant create sequence to &&user;
Parameters

Get UtRuleGrpRun
Get UT_TABLE_RULE
Get the rules for the run.
Get UT_RULE_GRP_RUN_PARMS
Process Rules
Connect to source - todo describe data
Connect to destination
binds
Run the query
insert into gtt_ut_table_row_msg
merge into ut_table_row
delete where they don't exist

Database Objects 
----------------
Also depends on the logging tables in Dbexperts3/ddl/oracle/logging
%\includegraphics*[width=\textwidth,viewport=0 0 3046 1632, bb=0 0  0 0]{ExceptionProcessing.jpeg

\includegraphics*[width=\textwidth,viewport=0 0 3046 1632, bb=0 0 3046 1632]{ExceptionProcessing.jpeg


To generate the tables not only are the mapping files required, 
the associated beans are even though they are 
never used. 

The dto's must be in the classpath.





source and destination databases may be different 

declarative rules

TODO need to support initialization procedure
SqlDeveloper
cd /opt
sudo unzip /common/Downloads/sqldeveloper-4.2.0.16.260.1303-x64.zip 

Installation
