# tnsadminconfig.sh
    if [ $# -ne 1 ] ; then
       echo "usage: tnsadminconfig ORACLE_SID" >&2
       exit 1
    else
       SID=$1
* find the files 
       oracle_home=`~/bin/getoraclehome.sh $SID`
       =
       #sudo updatedb
       tns_admin=${oracle_home}/network/admin
       tnsnames_file=${oracle_home}/network/admin/tnsnames.ora
       echo tnsnames_file: $tnsnames_file
       if [ ! -d $tnsnames_admin ] ; then 
           echo tns_admin $tns_admin
           echo directory does not exist $tnsnames_admin
           exit 1 
       fi
       
       cd $tns_admin
       pwd
       echo tnsnames_file $tnsnames_file before is:
       cat $tnsnames_file
* if amazon names are present, set to localhost 
       sed -i -e "s/ip_[^)]*/localhost/"  tnsnames.ora
       sed -i -e "s/ip_[^)]*/localhost/"  listener.ora
* add sales_reporting
       grep sales_reporting $tnsnames_file > /dev/null
       if [ $? -eq 0 ] ; then
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
       cat $tnsnames_file
    fi
* fix permissions
    sudo chmod 644 $tnsnames_file
CODE
