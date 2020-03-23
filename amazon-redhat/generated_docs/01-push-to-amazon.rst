From the client machine push to the amazon instance
===================================================

::

    set -x 
    if [ -z "$amazon" ]; then
       echo "Need to set amazon to hostname"
       exit 1
    fi  
    echo amazon is $amazon
    scp  -r ../amazon-redhat/*.sh ec2-user@$amazon:~/amazon-redhat
    scp  -r ../artifacts ec2-user@$amazon:~/artfifacts
    set +x 

-  push the oracle files :sub:`~` echo PUSH\_ORACLE
   :math:`PUSH_ORACLE set -x if [ ! -z `\ PUSH\_ORACLE ] ; then echo
   will push oracle ssh
   ec2-user@\ :math:`amazon mkdir -p Downloads/oracle19/ojdbc8-full     scp   ~/Downloads/oracle19c/ojdbc8-full/* ec2-user@`\ amazon:Downloads/oracle19/ojdbc8-full
   scp ~/Downloads/oracle19c/oracle-database-ee-19c-1.0-1.x86\_64.rpm
   ec2-user@$amazon:Downloads/oracle19 else echo oracle files will not
   be pushed echo if you wish to push oracle files export
   PUSH\_ORACLE=YES fi :sub:`~`

-  push home files :sub:`~` scp -r home\_files/\* ec2-user@$amazon:
   :sub:`~`
-  push bootstrap files :sub:`~` ssh
   ec2-user@\ :math:`amazon mkdir amazon-redhat scp *.sh ec2-user@`\ amazon:amazon-redhat
   scp ../get-javautil-7.sh ec2-user@$amazon:javautil-7.sh :sub:`~`


