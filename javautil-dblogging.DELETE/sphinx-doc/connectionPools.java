Connection Pools
================

* Auto commit should be off
* All context should be cleared
* Packages should be re-initialized

prepare\_connnection
--------------------

| prepare-connection provides one procedure.
| This will call dbms\_session.clear\_context for each context variable,
restoring the context for a connection returned from a connection\_pool
to the state the of an initially opened connection.

Connection pools do not generally clear this information out as it is
Oracle specific.

Example
-------

Immediately after obtaining a connection from a connection pool invoke the *prepare_connection* 
procedure.

From java

::


prepare\_connection
~~~~~~~~~~~~~~~~~~~

::

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

create public synonym prepare\_connection for prepare\_connection; 
grant execute on prepare\_connection to public; 
\`\`\`

Zaxxer
~~~~~~

TODO how to call this procedure in the connection pool

