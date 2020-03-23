# 70-cp-sys-scripts

 copies sql scripts that must be run as sys to ~oracle/oracle/ddl/sys
    set -x
    sys_scripts_dir_dest=~oracle/oracle/ddl/sys
    sudo mkdir -p $sys_scripts_dir_dest 
    sudo cp  ../oracle/ddl/sys/* $sys_scripts_dir_dest
    sudo chown -R oracle ~oracle/oracle

