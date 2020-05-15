    create or replace
    function format_timestamp(p_timestamp in timestamp)
    return varchar 
    is
	my_timestamp varchar(256) :=  to_char (current_timestamp, 'YYYY-MM-DD HH24:MI:SSXFF');
    begin
        my_timestamp := replace(my_timestamp,' ','T');
        return my_timestamp;
    end format_timestamp;
    /
