# From the client machine push to the amazon instance
    set -x 
    if [ -z "$amazon" ]; then
       echo "Need to set amazon to hostname"
    exit 1
    fi  
    echo amazon is $amazon
    scp  -r ../amazon-redhat ec2-user@$amazon:~/amazon-redhat
    scp  -r ../artifacts ec2-user@$amazon:~/artfifacts
    set +x 
    ssh  ec2-user@$amazon mkdir -p Downloads/oracle19/ojdbc8-full
    scp   ~/Downloads/oracle19/ojdbc8-full/* ec2-user@$amazon:Downloads/oracle19/ojdbc8-full
    scp   ../doit.sh ec2-user@$amazon:doit.sh
