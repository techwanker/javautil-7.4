# SqlSplitter

SqlSplitter takes an input stream or file (suffix sr.sql by convention)

and returns a list of SQL statements

statements are terminated with 

    ;--

Other types of source files can be supported by commenting out non-sql. For example
Sql*Plus

   set echo on
   set define off

can be processed as SQL

The splitter breaks the file down into blocks as follows

    if (trimmed.startsWith("--#<")) {
        return COMMENT_BLOCK_BEGIN;
    }
    if (trimmed.startsWith("--#>")) {
        return COMMENT_BLOCK_END;
    }
    if (trimmed.startsWith("--#")) {
        return COMMENT;
    }
    if (trimmed.startsWith("--::<")) {
        return MARKDOWN_BLOCK_BEGIN;
    }
    if (trimmed.startsWith("--::>")) {
        return MARKDOWN_BLOCK_END;
    }
    if (trimmed.equals("/")) {
        return PROCEDURE_BLOCK_END;
    }
    if (trimmed.startsWith("--/<")) {
        return PROCEDURE_START_DIRECTIVE;
    }
    if (trimmed.startsWith(";--")) {
        return STMT_END_NO_SQL;
    }
    if (trimmed.toUpperCase().startsWith("--@NAME")) {
        return STATEMENT_NAME;
    }


   --::<
   set echo on
   set define off
   --::>

The above block will not be processed as SQL but the directives are iterpreted by Sql*Plus as comments.
The SQL*Plus directives are visible to SQL*PLUS

Markdown blocks are extracted for markdown and the SQL indendented 4.  Hence the source code is the documentation
and they are always in sync.
