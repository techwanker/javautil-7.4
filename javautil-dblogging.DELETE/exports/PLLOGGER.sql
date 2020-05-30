--------------------------------------------------------
--  DDL for Package PLLOGGER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE "PLLOGGER" AS
        G_SEVERE       CONSTANT PLS_INTEGER := 1 ;
        G_WARNING      CONSTANT PLS_INTEGER := 2 ;
        G_INFO         CONSTANT PLS_INTEGER := 4 ;
        G_SNAP         CONSTANT PLS_INTEGER := 5 ;
        G_ENTERING     CONSTANT PLS_INTEGER := 6 ;
        G_EXITING      CONSTANT PLS_INTEGER := 6 ;
        G_FINE         CONSTANT PLS_INTEGER := 7 ;
        G_FINER        CONSTANT PLS_INTEGER := 8 ;
        G_FINEST       CONSTANT PLS_INTEGER := 9 ;
        G_NONE         CONSTANT PLS_INTEGER := 10 ;


    procedure create_set (p_set_nm    in varchar,
                        p_default_level     in number);

    procedure define_logger_level(p_set_nm    in varchar,
                        p_logger_nm in varchar,
                        p_level     in number);

       procedure set_job_token(p_job_token in varchar);



        -- setup must open_log_file, 
        -- must close when done
    	--%```    
        FUNCTION open_log_file(p_file_name  in varchar,
                               p_headers    in boolean default true)
        return varchar;
    	--%```    


    	--%```    
        PROCEDURE set_dbms_output_level
	(
            p_level  IN        PLS_INTEGER 
	);
    	--%```    

    	--%```    
        PROCEDURE set_filter_level (  
            p_level  IN PLS_INTEGER ) ;
    	--%```    

    	--%```    
        PROCEDURE set_record_level (
            p_level  IN        PLS_INTEGER ) ;
    	--%```    
            -- Write messages

    procedure log_msg (
        p_log_msg        in   varchar,
        p_level          in   pls_integer default G_INFO,
        p_unit           in   varchar     default null,
        p_line           in   pls_integer default null,
      p_record_stack   in   BOOLEAN       default false
    );

        --
        -- Write messages

    	--%```    
        PROCEDURE severe (
            p_unit          IN VARCHAR2,
            p_line          IN PLS_INTEGER,
            p_log_msg       IN VARCHAR2 DEFAULT '',
            p_record_stack  IN BOOLEAN DEFAULT FALSE ) ;
    	--%```    

    	--%```    
        PROCEDURE warning (
            p_unit          IN        VARCHAR2,
            p_line          IN        PLS_INTEGER,
            p_log_msg       IN        VARCHAR2 DEFAULT '',
            p_record_stack  IN        BOOLEAN DEFAULT FALSE ) ;
    	--%```    

    	--%```    
        PROCEDURE info (
            p_unit          IN        VARCHAR2,
            p_line          IN        PLS_INTEGER,
            p_log_msg       IN        VARCHAR2 DEFAULT '',
            p_record_stack  IN        BOOLEAN DEFAULT FALSE ) ;
    	--%```    

    	--%```    
        PROCEDURE entering (
            p_unit         IN VARCHAR2,
            p_line         IN PLS_INTEGER,
            p_log_msg      IN VARCHAR2 DEFAULT '',
            p_record_stack IN BOOLEAN DEFAULT FALSE,
            p_set_action   IN BOOLEAN DEFAULT TRUE ) ;
    	--%```    

    	--%```    
        PROCEDURE exiting (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
    	--%```    

    	--%```    
        PROCEDURE fine (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
    	--%```    

    	--%```    
        PROCEDURE finer (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
    	--%```    

    	--%```    
        PROCEDURE finest (
            p_unit         IN        VARCHAR2,
            p_line         IN        PLS_INTEGER,
            p_log_msg      IN        VARCHAR2 DEFAULT '',
            p_record_stack IN        BOOLEAN DEFAULT FALSE ) ;
    	--%```    

        --
        -- close when done
    	--%```    
        PROCEDURE close_log_file;
    	--%```    

    	--%```    
        function get_directory_path return varchar;
    	--%```    

    	--%```    
        function basename (p_full_path in varchar2,
                           p_suffix    in varchar2 default null,
                           p_separator in char default '/') return varchar2;
    	--%```    

        procedure set_job_msg_id (p_job_msg_id in pls_integer);     

   procedure use_logger_set(p_set_nm in varchar) ;

    END pllogger ;

/
