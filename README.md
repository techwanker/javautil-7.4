# javautil-7


## Building a host from scratch

   see [redhat 8](./amazon-redhat/README.md)


## Prerequisites

You will need two jar files not in any maven repository

The first is located in ./artifacts/jcmdline-1.0.3.jar


## BUILD.sh

### oracle ojdbc8.jar

From https://www.oracle.com/database/technologies/appdev/jdbc-ucp-19c-downloads.html get the ojdbc8.jar file and place in ~/artifacts/oracle

Push to target_host

    orajar=~/artifacts/oracle/ojdbc8.jar
    ssh ${user}@${host} "mkdir -p ~/artifacts/oracle"
    scp $orajar ${user}@${host}:~/artifacts/oracle


Put the file into your repository

    pushd ~/artifacts/oracle

    mvn install:install-file \
    -DgroupId=com.oracle -DartifactId=oracle-jdbc8 -Dversion=19 -Dpackaging=jar -Dfile=./ojdbc8.jar


### install

On target host 

    git clone --depth=1 https://github.com/techwanker/diamond-javautil-2020.git
    cd javautil-2020    
    ./BUILD.sh

feel free to examine the shell script and do each of these manually

examine

    cd javautil-2020/amazon-redhat
   
run any necessary scripts

build the code:

   cd ~/javautil-2020
   ./BUILD.sh


