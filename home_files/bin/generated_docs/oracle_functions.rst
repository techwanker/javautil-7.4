oracle\_functions.sh
====================

configure tnsnames and listener
-------------------------------

CODE set\_tns\_admin() { grep -v "^#." /etc/oratab
database\_count=\ ``grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | wc -l``
echo database count is :math:`database_count    if [ `\ database\_count
-eq 1 ] ; then export TNS\_ADMIN=\ ``grep "[^:\]:[^:}" /etc/oratab``
echo using TNS\_ADMIN $TNS\_ADMIN fi }

gettnsadmin() { SID=$1

}

getsids
=======

Usage
=====

getsids

displays the SIDS from /etc/oratab getsids() {
databases=\ ``grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f 1 -d :``
echo $databases }

usedb() { if [ -z $1 ] ; then echo usage: usedb ORACLE\_SID fi

::

    local db=$1
    grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f 1 -d : | grep $db
    database=`grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f 1 -d : | grep $db`

    if [ -z $database ] ; then 
        echo SID does not exist in `getsids` >&2
        grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  
    else  
        export ORACLE_SID=$db
        ORAENV_ASK=NO
        . oraenv
     fi

}

settnsadmin() { if [ ! -z $1 ] ; then echo usage: gettnsadmin
ORACLE\_SID else usedb
:math:`ORACLE_SID         echo ORACLE_HOME `\ ORACLE\_HOME pushd
$ORACLE\_HOME

-  resolve and report local
   tnsnames\_file=\ :math:`{ORACLE_HOME}/`find . -name tnsnames.ora | grep -v samples`         echo tnsnames_file `\ tnsnames\_file
   local tns\_admin=\ ``dirname $tnsnames_file`` echo tns\_admin
   $tns\_admin

   if [ ! -d $tns\_admin ] ; then echo directory does not exist
   :math:`tns_admin        return 1    fi    pushd `\ tns\_admin ls
   ##popd #local
   db=\ :math:`1    #local oracle_home=`grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f h -d : | grep `\ db\ ``#echo tns_admin=$tns_admin    #pushd $tns_admin && export TNS_ADMIN=``\ pwd\`
   fi }

tnsadminconfig
==============

tnsadminconfig() { if [ $# -ne 1 ] ; then echo "usage: tnsadminconfig
ORACLE\_SID" >&2 return 1 else usedb
:math:`ORACLE_SID         echo ORACLE_HOME `\ ORACLE\_HOME pushd
$ORACLE\_HOME

::

       local tnsnames_file=${ORACLE_HOME}/`find . -name tnsnames.ora | grep -v samples` 
       echo tnsnames_file $tnsnames_file

       local tns_admin=`dirname $tnsnames_file`
       if [ ! -d $tnsnames_admin ] ; then 
           echo directory does not exist $tnsnames_admin
           return 1
       fi
       echo tns_admin $tns_admin
       echo tnsnames_file $tnsnames_file
       pushd $tns_admin
       ls
       cat $tnsnames_file

-  if amazon names are present, set to localhost sed -i -e
   "s/ip\_[^)]*/localhost/" tnsnames.ora sed -i -e
   "s/ip\_[^)]*/localhost/" listener.ora add sales\_reporting if [ grep
   sales\_reporting $tnsnames\_file ] ; then echo sales\_reporting
   already in
   :math:`tnsnames_file        else            cat >> `\ tnsnames\_file
   <<:EOF: sales\_reporting= (DESCRIPTION = (ADDRESS = (PROTOCOL =
   TCP)(HOST = localhost)(PORT = 1521)) (CONNECT\_DATA = (SERVER =
   DEDICATED) (SERVICE\_NAME = sales\_reporting) ) ) :EOF: fi popd fi

} CODE
