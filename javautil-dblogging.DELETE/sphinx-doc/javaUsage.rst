Using in Java
#############

The javautil-dblogging.jar and its dependencies must be in your classpath.

Simply add a dependency to the maven co-ordinates specified in javautil-dblogging/pom.xml to your maven project.

The oracle jdbc driver must be in your maven repository.

Add dependencies
----------------

code-block:: 

    <dependency>
         <groupId>org.javautil</groupId>
         <artifactId>javautil-core</artifactId>
         <version>???</version>
    </dependency>
    <dependency>
        <groupId>org.javautil</groupId>
        <artifactId>javautil-dblogging</artifactId>
        <version>???</version>
    </dependency>

Specifying the appropriate version

Configure dblogger.properties
-----------------------------

TODO this should be externalized

code-block:: 

    dblogger.datasource.driver-class-name=org.h2.Driver
    dblogger.datasource.url=jdbc:h2:/tmp/dbloggerh2;AUTO_SERVER=TRUE;COMPRESS=TRUE
    dblogger.datasource.username=sr
    dblogger.datasource.password=tutorial

Trace Records
-------------

TODO provide reference for reading trace records 

see https://docs.oracle.com/cd/B10500_01/server.920/a96533/sqltrace.htm
TODO Oracle Trace File Analyzer https://docs.oracle.com/en/engineered-systems/health-diagnostics/trace-file-analyzer/tfaug/quick-start-guide.html#GUID-A1DBE3D4-6501-47D3-854E-E9978F19F7BA

Unfortunately I wrote the trace analyzer over a decade ago and I can't seem to find any documentation on the trace files.

You will have to look at the various parsers located in $JAVAUTIL_DBLOGGING/src/main/java/org/javautil/oracle/trace/record

The field names for the regular expression patterns are very descriptive.


