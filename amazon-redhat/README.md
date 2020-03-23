# Installing on Amazon

## configure ssh

**~/.ssh/config**

   chmod 600 ~/.ssh.config 

::
   ServerAliveInterval 120
   Host ec2-3-91-222-79.compute-1.amazonaws.com
       IdentityFile ~/.ssh/javautil-6.pem

## set environment variable for host

   export amazon=ec2-3-91-222-79.compute-1.amazonaws.com

## push artifacts 
from your local computer

cd $JAVAUTIL_HOME/amazon-redhat

run this on your local computer

./01-push-to-amazon.sh

It is expected that you have the following files in your ~/Downloads/oracle19c directory

    jcmdline-1.0.3.jar
    oracle-jdbc8-18.jar
    oracle-jdbc8-18-javadoc.jar.lastUpdated
    oracle-jdbc8-18-javadoc.jar-not-available
    oracle-jdbc8-18.pom
    oracle-jdbc8-18-sources.jar.lastUpdated
    oracle-jdbc8-18-sources.jar-not-available

## ssh into your amazon instance

run the following in order

   * cd amazon-redhat

   * 20-install-packages.sh

   * 25-install-postgres.sh

   * 30-install-maven.sh

   * 40-clone-the-code

