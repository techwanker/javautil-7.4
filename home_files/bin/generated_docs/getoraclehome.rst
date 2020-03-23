getoraclehome.sh
================

::

    if [ $# -ne 1 ] ; then
       echo "usage: getoraclehome ORACLE_SID" >&2
    fi

    SID=$1
    #grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f 1 -d : | grep $SID
    oracle_home=`grep -v "^#.*" /etc/oratab | grep "$SID[^:]*:[^:]*"  | cut -f 2 -d : `
    if  [ ! -d $oracle_home ] ; then 
    echo "directory does not exist $oracle_home" 2>&1
        exit 1
    fi

    echo $oracle_home

