# 30-install-maven.sh

 This will download apache maven 3.6.2 and install it in /opt/apache-maven-3.6.2
 maven is a build tool see [maven](https://maven.apache.org/)

```
set -x
set -e 
maven_ver='apache-maven-3.6.2'

file=${maven_ver}.zip
download_file=~/Downloads/${file}

if [ ! -d ~/Downloads ] ; then
    echo creating Downloads
    mkdir ~/Downloads
fi
```

 **get the file if we do not have it **
```
if [ ! -f ${download_file} ] ; then
     echo file getting ${download_file}
	    curl http://ftp.cixug.es/apache/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.zip  > ${download_file}
else
     echo using ${download_file}
fi
```

 *create /opt if it does not exist*
~~~
if [ ! -d /opt ] ; then
	   sudo mkdir /opt
fi

if [ ! -d /opt/${maven_ver} ] ; then #! * file has not been unzipped do it *
   cd /opt
   ls -l ~/Downloads   # just show tfile an size 
   sudo unzip ${download_file}
fi 
```

```
# add to path if not already specified
```
set +e 
mvn_path=`grep ${maven_ver} ~/.bashrc`
set -e
if [ -z $mvn_path] ; then  #  not specified so add to .bashrc
#    echo adding maven path
	    echo "PATH=/opt/${maven_ver}/bin:\$PATH" >> ~/.bashrc
else
     echo not adding $mvn_path 
fi 
. ~/.bashrc
echo path is now $PATH
mvn --version
```
