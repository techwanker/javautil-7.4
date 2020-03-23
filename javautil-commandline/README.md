# javautil-commandline

Create command line arguments by creating an annotated commandline bean

## Getting Started

clone this repository and *mvn clean install*

### Prerequisites

What things you need to install the software and how to install them

```
Give examples


### Annotations

*  @AcceptableValues(values = {"a", "b"}) private String text;
*  @DirectoryExists private File inputDirectory;


* AcceptableValues

* DependentField

* DirectoryArguments

* DirectoryExists

* DirectoryReadable

* DirectoryWritable

* ExclusiveArguments

* Exclusive

* FieldValue

* FileExists

* FileReadable

* FileWritable

* Hidden

* InheritenceArguments

* IntegerArguments

* MultiValueBean

* MultiValue

* Optional

* OptionalRequiredArguments

* Required

* RequiredUnlessArguments

* RequiredUnless

* RequiresArguments

* Requires

* StringSet


## Create an argument bean

TestBean.java

example: 

	package org.javautil.cli;
	
	import java.io.File;
	
	import org.javautil.commandline.annotations.Required;
	import org.javautil.commandline.CommandLineHandler;
	import org.javautil.commandline.annotations.Optional;
	
	public class SqlCsvExporterArguments {
	
    @Optional
    private File outFile;
	
    @Required
    private String selectStatement;
	
    @Required
    private String JDBCURL;
	
    @Required
    private String userNamePassword;
	
	
	
	
    public File getOutFile() {
        return outFile;
    }
	
    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }
	
    public String getSelectStatement() {
        return selectStatement;
    }
	
    public void setSelectStatement(String selectStatement) {
        this.selectStatement = selectStatement;
    }
	
    public String getJDBCURL() {
        return JDBCURL;
    }
	
    public void setJDBCURL(String jDBCURL) {
        JDBCURL = jDBCURL;
    }
	
    public String getUserNamePassword() {
        return userNamePassword;
    }
	
    public void setUserNamePassword(String userNamePassword) {
        this.userNamePassword = userNamePassword;
    }
	
    public void parseArguments(String[] args) {
        new CommandLineHandler(this).evaluateArguments(args);
    }
	
	}

annotate the fields with 

## Create a properties file

This file should be named after the argument bean and located in the appropriate src/main/resources 
directory, that is, the java package name with "." replaced with "/".

For the above bean that would be src/main/resources/org/javautil/cli/SqlCsvExporterArguments.properties

There should be an entry for **application.name** and **application.HelpText**

Each argument should have an entry of the *argname*.description

For example
example:

    application.name=SqlCsvExporter
    application.HelpText=Exports the results of a sql query to a file in CSV format
    outputFile.description=The name of the output file. If not specified, writes to stdout
    JDBCURL.description=The name of an environment variable that contains the JDBC URL of the database eg "jdbc:postgresql://localhost/sales_reporting_db" 
    usernamePassword.description=The name of an environment variable that contains the username and password separated by a slash  e.g. username/password

## add a main method

    public static void main(String[] args) throws SQLException, IOException, ParseException {
		for (String arg :args) { 
		   logger.info("arg " + arg);
		}
		SqlCsvExporterCLI invocation = new SqlCsvExporterCLI();
		SqlCsvExporterArguments arguments = new SqlCsvExporterArguments();
		arguments.parseArguments(args);
		invocation.process(arguments);
	}

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
