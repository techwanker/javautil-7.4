set -e 
oracle_jdbc_jar=~/artifacts/oracle/ojdbc8.jar  

message() {
        set +x
        echo "**************************************************"
        echo $*
        echo "**************************************************"
        set -x 
}

#!# Build Overview

if [ $# != 1 ] ; then 
  echo publish.properties is for jjs to do full integration test
  echo you should probably use developer.properties 
  echo since nothing was specified I will do that
  property_file=developer.properties 
else
  property_file=$1
  if [ ! -f $property_file ] ; then
      echo property_file $property_file does not exist
      exit 1
  fi
fi
  echo got here 3
  echo using properties from $property_file
  . $property_file
  message  using the following properties 
  cat $property_file
  sleep 3


   message *** maven_offline is $maven_offline

if [ ! -z $maven_offline ] ; then 
   if  [ $maven_offline != "-o" ] ; then 
   	message maven_offline is $maven_offline
   fi 
fi


build_home=$(pwd)

install-oracle-jdbc() {
    if [ -f $oracle_jdbc_jar ] ; then
            pushd ~/artifacts/oracle
	    mvn install:install-file \
	       -DgroupId=com.oracle -DartifactId=oracle-jdbc8 -Dversion=19 -Dpackaging=jar -Dfile=./ojdbc8.jar
            popd
     else
            echo not installing oracle into repository
	    echo ~/artifacts/oracle/ojdbc8.jar is a required file, upload and try again >&2
	    exit 1

    fi
}

maven() {
   mvn ${maven_offline} $*
}

# if the orac19 ojdbc jar is found install - not 



#create_database() {
#    if [ $create_database = true ] ; then
#      set -x
#      sudo su  postgres -c "./create_diamond_postgres.sh $USER $database"
#      set +e
#      mkdir ../tmp
#      set -e
#      zcat  exports/aerospace_postgres.sql.gz > ../tmp/aerospace_postgres.sql
#      psql -e $database <  ../tmp/aerospace_postgres.sql
#    fi
#}

install_open_artifacts() {
        set -x
        maven  install:install-file \
		-DgroupId=jcmdline \
		-DartifactId=jcmdline \
		-Dversion=1.0.3 \
		-Dpackaging=jar \
    	-DgeneratePom=true \
		-Dfile=artifacts/jcmdline-1.0.3.jar 
}

test_home() {
	if [ ! -f ~/connections_java.yaml ] ; then
          message  please make a connections yaml
          sleep 5
	fi
}

if [ ! -f .gitignore ] ; then
   echo no .gitignore
   exit 1
fi

#! ## install_javautil_notest
#! compile with no tests, many unit tests require a database

install-javautil-notest() {
   echo install_javautil $install_javautil
   message Installing javautil
   sleep 1
   if [ $install_javautil = true ] ; then
      pushd javautil-pom
      maven clean install -DskipTests=${javautil_skipTests}
      popd 
   fi
}

install_test_database() {
	pushd exports/postgres/vending_sales
        ./load_sales_reporting.sh
        popd
}





install_pdssr() {
    echo build_pdssr $build_pdssr
    if [ $build_pdssr = true ] ; then 
        pushd pdssr && maven clean install
    fi
}


git_commit() {
    if [ ${git_commit} = true ] ; then
       git add -A .
       git commit -a
    else
       message  git_commit is ${git_commit} not committing
    fi

}

#!## Install home files
#! * connections_java.yaml
#! * .qcd.data  

install_home_files() {
    cp -r home_files/* ~
    chmod 600 ~/connections_java.yaml
}


set -x
set +e
#sudo useradd postgres
set -e
install-oracle-jdbc
install_open_artifacts
install-oracle-jdbc
install-javautil-notest
install_home_files
install_test_database
cd javautil-pom && mvn clean install
#test_home

#fix_properties
#install_pdssr
#git add -A
#git_commit
