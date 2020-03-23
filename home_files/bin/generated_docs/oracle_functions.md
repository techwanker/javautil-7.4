# oracle_functions.sh

## configure tnsnames and listener
CODE
set_tns_admin() {
   grep -v "^\#." /etc/oratab
   database_count=`grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | wc -l`
   echo database count is $database_count
   if [ $database_count -eq 1 ] ; then
      export TNS_ADMIN=`grep "[^:\]:[^:}" /etc/oratab`
      echo using TNS_ADMIN $TNS_ADMIN
   fi
}

gettnsadmin() {
   SID=$1
   
}    

# getsids

# Usage
  getsids

 displays the SIDS from /etc/oratab 
getsids() {
   databases=`grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f 1 -d : `
   echo $databases
}
   
usedb() {
    if [ -z $1 ] ; then
       echo usage: usedb ORACLE_SID
    fi

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

settnsadmin() {
    if [ ! -z $1 ] ; then
       echo usage: gettnsadmin ORACLE_SID
    else
       usedb $ORACLE_SID 
       echo ORACLE_HOME $ORACLE_HOME
       pushd $ORACLE_HOME

* resolve and report 
       local tnsnames_file=${ORACLE_HOME}/`find . -name tnsnames.ora | grep -v samples` 
       echo tnsnames_file $tnsnames_file
       local tns_admin=`dirname $tnsnames_file`
       echo tns_admin $tns_admin

       if [ ! -d $tns_admin ] ; then 
           echo directory does not exist $tns_admin
           return 1
       fi
       pushd $tns_admin
       ls
       ##popd
       #local db=$1
       #local oracle_home=`grep -v "^#.*" /etc/oratab | grep "[^:]*:[^:]*"  | cut -f h -d : | grep $db`
       #echo tns_admin=$tns_admin
       #pushd $tns_admin && export TNS_ADMIN=`pwd`
    fi
}

# tnsadminconfig 
tnsadminconfig() {
    if [ $# -ne 1 ] ; then
       echo "usage: tnsadminconfig ORACLE_SID" >&2
       return 1
    else
       usedb $ORACLE_SID 
       echo ORACLE_HOME $ORACLE_HOME
       pushd $ORACLE_HOME

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
* if amazon names are present, set to localhost 
       sed -i -e "s/ip_[^)]*/localhost/"  tnsnames.ora
       sed -i -e "s/ip_[^)]*/localhost/"  listener.ora
 add sales_reporting
       if [  grep sales_reporting $tnsnames_file ] ; then
           echo sales_reporting already in $tnsnames_file
       else
           cat >> $tnsnames_file <<:EOF: 
sales_reporting=
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = sales_reporting)
    )
  )
:EOF:
       fi
      popd
    fi

}
CODE
