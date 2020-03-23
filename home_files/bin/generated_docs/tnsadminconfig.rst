tnsadminconfig.sh
=================

::

    if [ $# -ne 1 ] ; then
       echo "usage: tnsadminconfig ORACLE_SID" >&2
       exit 1
    else
       SID=$1

-  find the files oracle\_home=\ ``~/bin/getoraclehome.sh $SID`` = #sudo
   updatedb
   tns\_admin=\ :math:`{oracle_home}/network/admin        tnsnames_file=`\ {oracle\_home}/network/admin/tnsnames.ora
   echo tnsnames\_file:
   :math:`tnsnames_file        if [ ! -d `\ tnsnames\_admin ] ; then
   echo tns\_admin
   :math:`tns_admin            echo directory does not exist `\ tnsnames\_admin
   exit 1 fi

   cd :math:`tns_admin    pwd    echo tnsnames_file `\ tnsnames\_file
   before is: cat $tnsnames\_file
-  if amazon names are present, set to localhost sed -i -e
   "s/ip\_[^)]*/localhost/" tnsnames.ora sed -i -e
   "s/ip\_[^)]*/localhost/" listener.ora
-  add sales\_reporting grep sales\_reporting
   :math:`tnsnames_file > /dev/null        if [ `? -eq 0 ] ; then echo
   sales\_reporting already in
   :math:`tnsnames_file        else            cat >> `\ tnsnames\_file
   <<:EOF: sales\_reporting= (DESCRIPTION = (ADDRESS = (PROTOCOL =
   TCP)(HOST = localhost)(PORT = 1521)) (CONNECT\_DATA = (SERVER =
   DEDICATED) (SERVICE\_NAME = sales\_reporting) ) ) :EOF: fi cat
   $tnsnames\_file fi
-  fix permissions sudo chmod 644 $tnsnames\_file CODE


