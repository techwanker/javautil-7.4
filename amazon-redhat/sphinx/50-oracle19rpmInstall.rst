Oracle 19c Installation
=======================

Install os packages
===================

::

    sudo yum -y install git bc binutils elfutils-libelf elfutils-libelf-devel \
        fontconfig-devel glibc glibc-devel \
        java \
        ksh libaio libaio-devel \
        libgcc libnsl librdmacm-devel libstdc++ libstdc++-devel libX11 \
        libXau libxcb libXi libXrender libXrender-devel libXtst make net-tools \
        nfs-utils python3 python3-configshell python3-rtslib  \
        smartmontools sysstat targetcli wget zip 


    echo *****************************
    echo about to install oracle
    echo *****************************
    sleep 1
    *https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/checking-server-hardware-and-memory-configuration.html#GUID-DC04ABB6-1822-444A-AB1B-8C306079439C
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

    cd ~/Downloads/oracle19
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

references
----------

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/install-and-upgrade.html

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/installing-the-oracle-preinstallation-rpm-from-unbreakable-linux-network.html#GUID-555F704E-BD48-4E0E-AC9D-038596601194

-  https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/running-rpm-packages-to-install-oracle-database.html#GUID-BB7C11E3-D385-4A2F-9EAF-75F4F0AACF02

   cd ~/Downloads/oracle19

   sudo yum -y localinstall compat-libcap1-1.10-7.el7.x86\_64.rpm

**bug, there is no digest it must be installed with rpm**

::

    sudo rpm -i --nodigest oracle-database-ee-19c-1.0-1.x86_64.rpm

    export CV_ASSUME_DISTID=OEL7.6

configure the database
======================

::

    sudo /etc/init.d/oracledb_ORCLCDB-19c configure

