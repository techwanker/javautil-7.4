
# Postgres 12 centos

#!* https://computingforgeeks.com/how-to-install-postgresql-12-on-centos-7/
#!* https://www.shernet.com/postgresql/upgrading-to-postgresql-10-on-centos-7/



# postgres

    sudo yum -y install https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm
    sudo yum -y install epel-release yum-utils
    sudo yum-config-manager --enable pgdg12
    sudo yum install -y postgresql12-server postgresql12
    sudo /usr/pgsql-12/bin/postgresql-12-setup initdb
    set -x 
    #sudo amazon-linux-extras install postgresql10 vim epel -y
    #sudo yum install -y postgresql-server postgresql-devel
    #sudo /usr/bin/postgresql-setup --initdb

## nasty hack for permissions, read the docs in sphinxdoc
   sudo sed -i -e "s/ident/trust/" /var/lib/pgsql/data/pg_hba.conf
   sudo sed -i -e "s/peer/trust/" /var/lib/pgsql/data/pg_hba.conf

## enable and start
    sudo systemctl enable --now postgresql-12
    sudo systemctl start postgresql
   # sudo systemctl enable postgresql
   # sudo systemctl start postgresql

## create the user and database
   sudo su - postgres -c 'createuser ec2-user'
   sudo su - postgres -c "createdb --owner ec2-user sales_reporting"
 

## test
   
   psql sales_reporting
