select * from files where md5 in (select md5 from files group by md5 having count(*) > 1) order by md5;
