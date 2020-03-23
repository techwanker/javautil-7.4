# Configuring for oracle

## set up your environment 
    sudo su - oracle 
    export ORACLE_SID=ORCLCDB
    export ORAENV_ASK=NO
    . oraenv

## change password to something known
    sqlplus / as sysdba

    alter user system identified by tutorial;
    shutdown;

## create an image

TODO image picture

## Create a new machine from the image


## listener

The hostname will now be wrong when you start from the image

   hostname  


   cd /opt/oracle/product/19c/dbhome_1/network/admin

TODO 
write a script to 
**change to hostname in tnsnames and listener.ora**

startup the listener

    lsnrctl start

## startup the database

    sqlplus / as sysdba
    sqlplus> startup

## check the listener

    lsnctl status

Service "961e797725212caae0535c1d1faced5c" has 1 instance(s).
  Instance "ORCLCDB", status READY, has 1 handler(s) for this service...
Service "ORCLCDB" has 1 instance(s).
  Instance "ORCLCDB", status READY, has 1 handler(s) for this service...
Service "ORCLCDBXDB" has 1 instance(s).
  Instance "ORCLCDB", status READY, has 1 handler(s) for this service...
Service "orclpdb1" has 1 instance(s).
  Instance "ORCLCDB", status READY, has 1 handler(s) for this service...
The command completed successfully

## Connect through tcp

   sqlplus system/tutorial@ORCLCDB

## tunnel i

## Create the pluggable database 

   cd ${JAVAUTIL-6}/src/main/resources/ddl/oracle
   
   sqlplus system/password @ create_pluggable

##  Define a service in listener


