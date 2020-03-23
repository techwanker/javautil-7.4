# 30-install-maven.sh
echo pwd is `pwd`

pushd ../artifacts
artifacts_directory=`pwd`
popd
ls -l $artifacts_directory
if [ ! -d $artifacts_directory ] ; then
    echo artifacts_directory $artifacts_directory does not exist >&2
    exit
fi
#!```
maven_ver='apache-maven-3.6.3'
download_file=${artifacts_directory}/apache-maven-3.6.3.zip 
if [ ! -r $download_file ] ; then
   ls -l $artifacts_directory
   echo download_file is $download_file `ls -l $download_file` but does not exist
else
   echo download_file is $download_file `ls -l $download_file`
fi
sleep 3
#!```

#!```
#! *create /opt if it does not exist*
#!~~~
echo mkdir opt
if [ ! -d /opt ] ; then
    sudo mkdir /opt
fi
#!```

#!```
cd /opt 
sudo cp $download_file .
if [ ! -d ${maven_ver} ] ; then
	echo dir ${maven_ver} not found in `pwd`, unzipping
	sudo unzip ${download_file} 
fi 
#!```

# add to path if not already specified
#!```
echo add maven to path
grep ${maven_ver} ~/.bashrc
if [  $? -ne 0 ] ; then
     echo adding maven to PATH
     echo "PATH=/opt/${maven_ver}/bin:\$PATH" >> ~/.bashrc
else
     echo not adding $mvn_path 
fi 
set -x
cat ~/.bashrc
set +x
. ~/.bashrc
echo path is now $PATH
mvn --version
#!```
