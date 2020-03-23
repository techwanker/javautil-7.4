install maven
=============

set -x set -e maven\_ver='apache-maven-3.6.2'

file=\ :math:`{maven_ver}.zip    download_file=~/Downloads/`\ {file}

if [ ! -d ~/Downloads ] ; then echo creating Downloads mkdir ~/Downloads
fi

::

    **get the file if we do not have it **

if [ ! -f ${download\_file} ] ; then echo file getting
:math:`{download_file}         curl http://ftp.cixug.es/apache/maven/maven-3/3.6.2/binaries/apache-maven-3.6.2-bin.zip  > `\ {download\_file}
else echo using ${download\_file} fi *create /opt if it does not exist*
if [ ! -d /opt ] ; then sudo mkdir /opt fi

if [ ! -d /opt/${maven\_ver} ] ; then \* file has not been unzipped do
it \* cd /opt ls -l ~/Downloads # just show tfile an size sudo unzip
${download\_file} fi

# add to path if not already specified set +e
mvn\_path=\ ``grep ${maven_ver} ~/.bashrc`` set -e if [ -z $mvn\_path] ;
then # not specified so add to .bashrc echo adding maven path echo
"PATH=/opt/:math:`{maven_ver}/bin:\$PATH" >> ~/.bashrc    else         echo not adding `\ mvn\_path
fi . ~/.bashrc echo path is now $PATH mvn --version
