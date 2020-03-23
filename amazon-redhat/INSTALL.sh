# Server Configure    

    ./20-install-packages.sh
    ./25-install-postgres.sh
    ./30-install-maven.sh
#    ./40-clone-the-code
    ./50-oracle19rpmInstall.sh
    ./70-cp-sys_scripts.sh
    cd ../exports/postgres/sales_reporting && ./create_and_load_sales_reporting.sh
