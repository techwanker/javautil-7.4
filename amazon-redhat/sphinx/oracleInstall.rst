https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/checking-server-hardware-and-memory-configuration.html#GUID-DC04ABB6-1822-444A-AB1B-8C306079439C
====================================================================================================================================================================

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

    cd ~/Downloads/oracle19
    curl -o oracle-database-preinstall-19c-1.0-1.el7.x86_64.rpm https://yum.oracle.com/repo/OracleLinux/OL7/latest/x86_64/getPackage/oracle-database-preinstall-19c-1.0-1.el7.x86_64.rpm

install it
----------

# must use-skip broken because of /etc/redhat-version dependency

sudo yum -y --skip-broken localinstall
oracle-database-preinstall-19c-1.0-1.el7.x86\_64.rpm

preinstall
==========

::

    # https://oracle-base.com/articles/vm/aws-ec2-installation-of-oracle
    # https://docs.oracle.com/en/database/oracle/oracle-database/19/cwsol/checking-the-oracle-database-prerequisites-packages-configuration.html#GUID-F0F28CEB-41DD-47CF-A1FE-DCF5F557A947

#
https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/running-rpm-packages-to-install-oracle-database.html#GUID-BB7C11E3-D385-4A2F-9EAF-75F4F0AACF02

install oracle
==============

#
https://docs.oracle.com/en/database/oracle/oracle-database/19/install-and-upgrade.html
#
https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/installing-the-oracle-preinstallation-rpm-from-unbreakable-linux-network.html#GUID-555F704E-BD48-4E0E-AC9D-038596601194

#
https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/running-rpm-packages-to-install-oracle-database.html#GUID-BB7C11E3-D385-4A2F-9EAF-75F4F0AACF02

cd ~/Downloads/oracle19

sudo yum -y localinstall compat-libcap1-1.10-7.el7.x86\_64.rpm sudo yum
-y localinstall oracle-database-ee-19c-1.0-1.x86\_64.rpm
