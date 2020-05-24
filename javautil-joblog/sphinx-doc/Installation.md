# Installation

## Prerequisites 

Install git  https://www.atlassian.com/git/tutorials/install-git
Install maven https://maven.apache.org/download.cgi
Install java 
Install oracle 

### Optional 

Install 


## Get the code 
.. code

git clone https://github.com/techwanker/javautil-6.git

## Amazon Web Services Example

### Create an EC2 instance

login into AWS Management Console

https://console.aws.amazon.com/console/home?region=us-east-1

launch a virtual machine

https://console.aws.amazon.com/ec2/home?region=us-east-1#LaunchInstanceWizard

Red Hat Enterprise Linux version 8 (HVM), EBS General Purpose (SSD) Volume Type 64-bit (x86)

select a t3a.medium

create a javautil-6.pem private key
:
save the key in ~/.ssh

http://xmodulo.com/how-to-specify-private-key-file-in-ssh.html

add an entry in ~/.ssh/config 

ssh ec2-user@HOSTNAME

Host *the host name*
  IdentityFile ~/.ssh/javautil-6.pem

make sure config has 600 permissions
    chmod 600 ~/.ssh/config 
    chmod 600 ~/.ssh/javautil-6.pem 

launch the install

In a few minutes you should have an instance up and running

click view instances

ssh 

   sudo yum install git 
   sudo yum install maven
   sudo yum install postgresql 
   sudo yum useradd postgres 
git clone https://github.com/techwanker/javautil-6.git
cd javautil-6

put oracle jdbc in the path 

download the jdbc 

scp scp oracle-jdbc8-18.jar ec2-user@ec2-34-201-13-174.compute-1.amazonaws.com:artifacts

./BUILD developer.properties 

THE hb_conf that needs to be changed is in /var/lib/pgsql
