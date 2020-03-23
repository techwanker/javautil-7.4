# From the client machine push to the amazon instance

#!~~~
set -x 
if [ -z "$amazon" ]; then
   echo "Need to set amazon to hostname"
   exit 1
fi  
echo amazon is $amazon
scp  -r ../amazon-redhat/*.sh ec2-user@$amazon:~/amazon-redhat
scp  -r ../artifacts ec2-user@$amazon:~/artfifacts
set +x 
#!~~~
#!* push the oracle files
#!~~~
echo PUSH_ORACLE $PUSH_ORACLE
set -x
if [ ! -z $PUSH_ORACLE ] ; then
    echo will push oracle
    ssh  ec2-user@$amazon mkdir -p Downloads/oracle19/ojdbc8-full
    scp   ~/Downloads/oracle19c/ojdbc8-full/* ec2-user@$amazon:Downloads/oracle19/ojdbc8-full
    scp   ~/Downloads/oracle19c/oracle-database-ee-19c-1.0-1.x86_64.rpm ec2-user@$amazon:Downloads/oracle19
else 
    echo oracle files will not be pushed 
    echo if you wish to push oracle files export PUSH_ORACLE=YES 
fi
#!~~~

#!* push home files
#!~~~
scp -r home_files/* ec2-user@$amazon:
#!~~~
#!* push bootstrap files
#!~~~
ssh   ec2-user@$amazon mkdir amazon-redhat
scp *.sh ec2-user@$amazon:amazon-redhat
scp   ../get-javautil-7.sh ec2-user@$amazon:javautil-7.sh
#!~~~

