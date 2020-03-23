# brazil

## Usage
     . brazil.sh to load the function
## brazil ssh *alias*
## brazil list 
 show the aliases and hostnames 

## brazil add
 * Adds or replaces the entry in ~/.amazon
 * adds an entry to ~/.ssh/config 
 ### example
    brazil add aliasName hostname pemfile
 
 
## brazil ssh *alias*

 * ssh ec2-user@hostname

## brazil sethost *alias*
 export host=**hostname**

    brazil() {
        alias_file=~/.amazon
    
        cmd=$1
        export alias=$2
        echo cmd is $cmd alias is $alias
        the_hostnm=`awk -F: '{if ($1 == "'$alias'")  {print $2; exit}}' ~/.amazon`
        if [ $cmd == 'add' ] ; then  
                if [ $# -ne 4 ] ; then 
                    echo usage add alias hostname pemfile 2>&1
                    return 0
                fi
                alias=$2
                host_nm=$3
                pem=$4
                grep ${alias}  ~/.amazon
* ensure the identify file exist and set permissions

                identity_file=~/.ssh/${pem}
                if [ ! -f $identity_file ] ; then
                    echo $identity_file does not exist 2>&1
                    return 1
                else 
                    chmod 600 $identity_file
                fi
  
                if [ $? -eq 1 ] ; then # not present add 
                   echo adding $alias $hostname $pem
                   echo ${alias}:${host_nm}  >> ~/.amazon
                   echo Host ${host_nm} >> ~/.ssh/config
                   echo "    IdentityFile $identity_file 
                   echo "    IdentityFile $pem >> ~/.ssh/config 
                else 
                   echo changing hostname for  $alias $hostname $pem
                   sed -i -e "s/:.*/:${host_nm}/" ~/.amazon
                   sed -i -e "s/:.*/:${host_nm}/" ~/.ssh/config
                fi
                chmod 600 ~/.ssh/config 
                echo "~/.amazon"
                cat ~/.amazon
                echo "~/.ssh/config"
                cat ~/.ssh/config
        else 
            if [ -z $the_hostnm ] ; then
                 set +x 
                 echo alias $alias not found in >&2
                 cat ~/.amazon >&2
            else 
                echo the_hostnm is $the_hostnm
                case $cmd in 
                   "ssh")
                        if [ $# =ne 2 ] ; then
                           echo usage brazil ssh alias 2>&1
                           return 1
                        fi
                        echo connecting to $alias ${the_hostnm}
                        ssh ec2-user@${the_hostnm} ;;
                   "list") 
                        cat ~/.amazon ;;
                   "edit")
                        vim ~/.amazon
                        . ~/bin/brazil.sh ;;
                   "scp") 
                       set -x
                       last_arg=${@:$#}
                       arg_count=$# 
                       echo arg_count ${arg_count}
                       leading_arg_count=`expr ${arg_count} - 2`
                       echo leading_arg_count ${leading_arg_count}
                       leading_args=${@:1:${leading_arg_count}}
                       echo leading_args ${leading_args}
                       echo last_arg $last_arg
                       echo scp $leading_args ec2-user@${the_hostnm}:${last_arg} ;;
                   "sethost")
                       export amazon="${the_hostnm}"
                       echo host set to ${the_hostnm} ;;
                   *) 
                       echo usage "brazil command args"
                       echo "command is sethost edit list ssh add" ;;
              esac
           fi
       fi
    }
   
alias braziladd="brazil add" 
alias brazilssh="brazil ssh"
alias brazilhost="brasil sethost"
alias brazillist="brazil list"
