create table files (
  source_name varchar2(255),
  directory_name varchar(255),
 file_name varchar(255),  
  base_file_name varchar2(255), 
  file_extension varchar(32), 
  mod_date varchar(10),
  mod_time varchar(8), 
  bytes int, 
  md5 varchar(32));

create index files_ndx_md5 on files(md5);

create index files_ndx_file_name on files(file_name);

insert into files
select * from csvread('/common/scratch/pictures3.csv');
