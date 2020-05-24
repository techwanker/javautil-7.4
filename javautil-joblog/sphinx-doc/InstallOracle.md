#Install Oracle

## Download
### upload the jdbc drivers

go to https://www.oracle.com/database/technologies/appdev/jdbc-ucp-19c-downloads.html
https://www.oracle.com/database/technologies/appdev/jdbc-ucp-19c-downloads.html#license-lightbox 


### Get the rdbms
go to https://www.oracle.com/database/technologies/oracle-database-software-downloads.html 
and select linux x86-64 

save it in ~/Downloads/oracle19c



unfortunately this cannot be curled, so you have to use browser and then upload it to amazon
as you do not want to this every time you fire up an instance save in an s3 container

or, just make things simpler upload it scp ~/Downloads/oracle19/LINUX.X64_193000_db_home.zip ec2-user@$amazon:~/Downloads

Time may vary from a few minutes to hours depending on your network connection to upload 2.8 gb

Snap off your install 

TODO create a base EC2 instance 

https://docs.oracle.com/en/database/oracle/oracle-database/19/ladbi/index.html

## Store in S3

Create a vault and upload the file 

https://docs.aws.amazon.com/AmazonS3/latest/user-guide/upload-objects.html

Follow the directions

Create vault: oracle-licensed-code

# Copy the files from s3 to your ecb
see https://n2ws.com/blog/how-to-guides/how-to-copy-data-from-s3-to-ebs
https://n2ws.com/blog/how-to-guides/how-to-copy-data-from-s3-to-ebs

aws create-access-key glacier


# Installing
 

## Docs
https://docs.oracle.com/en/database/oracle/oracle-database/19/install-and-upgrade.html

# Installing with Ansible
see https://medium.com/oracledevs/devops-series-automate-oracle-19c-rdbms-installations-with-ansible-github-43cfdf344a4a

## 

Create a user 
https://console.aws.amazon.com/iam/home?region=us-east-1#/users
