Oracle 19c Installation
=======================

ORACLE\_FILES=~/Downloads/oracle19 # Install os packages

::

        sudo yum -y install git bc binutils elfutils-libelf elfutils-libelf-devel \
            fontconfig-devel glibc glibc-devel \
            java \
            ksh libaio libaio-devel \
            libgcc libnsl librdmacm-devel libstdc++ libstdc++-devel libX11 \
            libXau libxcb libXi libXrender libXrender-devel libXtst make net-tools \
            nfs-utils python3 python3-configshell python3-rtslib  \
            smartmontools sysstat targetcli wget zip 

        if [ ! -d $ORACLE_FILES ] ; then
            echo directory $ORACLE_FILES does not exist, exiting 2>&1
            exit 1
        fi 

add helpful files
=================

oracle
------

::

    sudo mkdir -p ~oracle/bin
    sudo cp  ../home_files/bin/* ~/oracle/bin
    if [ ! grep  "addbindir" ~/oracle/.bashrc ] ; then
        sudo echo PATH=\$PATH:~/bin # addbindir >> ~/oracle/.bashrc
    fi

this user
---------

::

    if [ ! grep  "addbindir" ~/.bashrc ] ; then
    mkdir ~/bin
    sudo cp  ../home_files/bin* ~/bin
        echo PATH=\$PATH:~/bin # addbindir >> ~.bashrc
    fi 

echo \*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\* echo
about to install oracle echo
\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\* sleep 1
:sub:`~`
\*https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/checking-server-hardware-and-memory-configuration.html#GUID-DC04ABB6-1822-444A-AB1B-8C306079439C

::

    set -x
    set -e
    grep MemTotal /proc/meminfo
    grep SwapTotal /proc/meminfo
    df -h /tmp
    free
    uname -m
    df -h /dev/shm

preinstall
==========

get the package
---------------

::

    set -e 
    cd ${ORACLE_FILES}
     #**These files are required but are not available in redhat 8, so we take them from centos**
    wget http://mirror.centos.org/centos/7/os/x86_64/Packages/compat-libstdc++-33-3.2.3-72.el7.x86_64.rpm
    wget http://mirror.centos.org/centos/7/os/x86_64/Packages/compat-libcap1-1.10-7.el7.x86_64.rpm
    curl -o oracle-database-preinstall-19c-1.0-1.el7.x86_64.rpm \
        https://yum.oracle.com/repo/OracleLinux/OL7/latest/x86_64/getPackage/oracle-database-preinstall-19c-1.0-1.el7.x86_64.rpm

install it
----------

::

    sudo yum -y localinstall compat-libstdc++-33-3.2.3-72.el7.x86_64.rpm
    sudo yum -y localinstall compat-libcap1-1.10-7.el7.x86_64.rpm
    sudo yum -y localinstall oracle-database-preinstall-19c-1.0-1.el7.x86_64.rpm 

references
----------

-  https://oracle-base.com/articles/vm/aws-ec2-installation-of-oracle

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/cwsol/checking-the-oracle-database-prerequisites-packages-configuration.html#GUID-F0F28CEB-41DD-47CF-A1FE-DCF5F557A947

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/running-rpm-packages-to-install-oracle-database.html#GUID-BB7C11E3-D385-4A2F-9EAF-75F4F0AACF02

Install Oracle
==============

::

    cd ${ORACLE_FILES}
    sudo yum -y localinstall  compat-libcap1-1.10-7.el7.x86_64.rpm 

**bug, there is no digest it must be installed with rpm** :sub:`~` sudo
rpm -i --nodigest oracle-database-ee-19c-1.0-1.x86\_64.rpm export
CV\_ASSUME\_DISTID=OEL7.6 :sub:`~`

configure the database
======================

::

    sudo /etc/init.d/oracledb_ORCLCDB-19c configure

references
----------

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/install-and-upgrade.html

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/installing-the-oracle-preinstallation-rpm-from-unbreakable-linux-network.html#GUID-555F704E-BD48-4E0E-AC9D-038596601194

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/running-rpm-packages-to-install-oracle-database.html#GUID-BB7C11E3-D385-4A2F-9EAF-75F4F0AACF02


