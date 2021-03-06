Contributions
=============

Jim Schmidt and his company Trinity Technical Services introduced and mentored
Custom Data Solutions on every technology they used since 1990.

In 1990 Chuck Schmidt, the owner and president of Custom Data Solutions was
developing software in assembly language on alphamicro computers.

Unix/Linux
----------

I introduced Chuck to Unix, starting with Interactive Unix, a Kodak supported
version of AT&T Unix System 5 in 1991.  

Thereafter Trinity became a reseller of MIPS computers and then 
Digital Equipment.

Oracle
------

Jim introduced Chuck to Oracle and provided training and technical assistance 
from 1992 to 2007. Trinity staff trained Custom Data Staff on Oracle Forms,
Oracle Reports and the Database.

C
-

Java
----

Became Employee
---------------
In 2007 I made an agreement with Custom Data Solutions to become
the CIO with the understanding that he could work remotely.  

So I sold everything I owned, two houses, two boats, two jet-skis, two ATVs
and the rest of my possessions.

When my youngest son went off to college, I drove him to Purdue and
went to Michigan to fix a few problems for a couple of months, that 
turned out to be over a year.  

During that time period

- Migrated 10 vending databases to a single database
- Upgraded from Oracle 7 to Oracle 11
- Installed Oracle Enterprise manager and taught how to use it
- Created a development and a test database. Formerly all testing was done in production.



Technical Standards
-------------------
- Estabished the use of build tools, Ant and Maven
- Instituted source version control, CVS and Subversion
- Unit Testing
- Integration Testing
- Designed and build a document management service
- Migrated off Digital Unix to Linux
- Introduced virtualization, vmware, openvz and virtualbox
- 

Products
--------

Check 21
********

Document Management
*******************

Workbook Parser
***************

Dexterous
*********



Redesign
--------

CDS had a prospective client that needed rebate process

Data Load
*********
I replaced the java program that loaded the data into a stored procedure
that loaded into staging tables.

Exception Processing
********************
I employed the javautil Exception Processing system to identify load problems.

Unknown to me at the time, Custom Data Solutions identified problems with reports.
The report modification was cumbersome, execution was slow, printing was slow 
and wasteful.

By generating metrics of minimum and maximum and standard deviations from 
the mean the identification of outlier input data was greatly improved.

Maintenance System
******************
The maintenance and control system was all written in Apex, whereas CDS was
using Oracle Forms.  My staff trained CDS on developing in Apex.

Posting
*******

*****************

Workbooks
*********

Others
------
- Servlets
- Javascripts
- Excel workbooks
- Detachable tablespaces
- Materialized Views
- Virtual Private Databases

Javautil
--------
javautil.org was Jim Schmidt's domain
javautil.com still is


History of Jim Schmidt and Custom Data Solutions
================================================

Creation
--------

Custom Data Solutions was initially called CDS for Chuck and Dee Schmidt.

Chuck is my older brother and Dee is his wife.

Chuck left his job at Michigan National Bank to offer data processing services, initially using a TRS-80 to print mailing labels.

He subsequently bought an Alpha Micro http://www.s100computers.com/Hardware%20Folder/Alpha%20Micro/History/History.htm and wrote some business applications.

On a visit to Detroit I explained to him the benefits of Unix and got him started, installing Interactive Unix from Kodak on a PC from 133 floppy disks in 1990.

Between 1983 and 1990 I worked as Vice President of International Banking Systems for RepublicBank Dallas, as a principal at a Distribution Requirements Planning Company and then at Computer Associates where I single handedly designed and wrote the ACH settlement software system in six weeks.

In 1990 I started Trinity Technical Services with two partners and ended up buying them both out.

My first contract was the selection of an ERP system for a distributor in Texas.  The chosen vendor was CIM-JIT and it was my first exposure to Oracle. I soon
became more proficient in the CIM-JIT software than the vendor and flew around 
country to modify to delivered software to comply with commitments to customers.

In the process I became quite proficient in fixing performance problems.

Convinced that Oracle was the finest relational database, I convinced Chuck to
come down to my Dallas office for a quick introduction.

He immediately saw the benefits of SQL over procedural file manipulation and 
also became an Oracle Partner.


CDS somehow happened to land a contract to handle rebates for Leaf Gum.

Technologies
------------

Unix
****

Oracle
******

Oracle Forms
&&&&&&&&&&&&

Oracle Reports
&&&&&&&&&&&&&&

PL/SQL
&&&&&&

C
*

Java
****

JSP
***

Graphics
********

Javascript 
**********

Spreadsheets
************

Digital Unix
************



Linux
*****

Build tools
***********
Build scripts were shell scripts.

I introduced standardized build tools, first ant and then Maven

Unit Tests
**********
I introduced junit for unit testing code and cobertura for code coverage analysis.

Integration Tests
*****************

Chuck Retires
-------------

Database Consolidation
&&&&&&&&&&&&&&&&&&&&&&

Each client had a separate database version 7 of Oracle.  
I consolidated 10 databases into one, eliminated database links.

Customer Matching and Address Validation
&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

There was a daemon that ran that attempted to match customers on addresses.

I utilized javautils service to call the USPS API to actually standardize the
addresses for exact matches and identify undeliverable addresses


Geo Encoding
&&&&&&&&&&&&

Exception Processing
&&&&&&&&&&&&&&&&&&&&

Spring
&&&&&&

Hibernate
&&&&&&&&&






Help
****
Make files
C
Pro*C
Forms

****
V




Business Model
==============


Relationship
************

Custom Data Services was created by my brother Chuck.  I consulted to them for over 25 years and
taught him everything he knew from linux to relational databases, website creation, c and java.

In 2007 I accepted the position of CIO when my brother sold the company.  I gave up my consulting practice
on the promise that I could work remotely after my son went to college.

After I completely revised all of the infrastructure, which was horrible in more ways than can be credibly described
including the fact that they were seven years out of date on Oracle and had 14 instances running on three different 
machines, I upgraded to Oracle 10 and migrated everything to one instance and finally into one schema.

After I sold my house and all my possessions and moved to Costa Rica, they fired me.



While CIO
---------
* Document Management
* Migrated off of Digital Unix
* Changed backup procedures 
* Introduced Software Version Control
* Created development and test databases
* Trained a cafeteria worker and a pizza delivery person to program
* Coded up the most complex spreadsheets for Frito
* Introduced virtualization vmware, openvz, virtualbox
* Upgraded to Oracle 10
* Introduced Oracle Enterprise Manager
* Materialized Views
* Detachable Tablespaces
* Virtual Private Databases
* Spring inversion of control, hibernate, aspects
* Unit Testing
* Check 21 solution
* Wikis for documentation procedures
* xml 
* dexterous
* workbook parser 

Terminated
---------- 


They claimed ownership of javautil despite the facts
* javautil.org was owned by Jim Schmidt
* javautil.com was owned by Jim Schmidt

The code base dated back to 1999.

CDS sued Jim Schmidt and claimed ownership of the code, but I would have had to have their current employees testify that
the code they were using was in production at my customer sites before I joined CDS.

A settlement was reached, I allowed them to continue to use the code as it was at them time  while I retained all rights.


History of Jim Schmidt and Custom Data Solutions
================================================

Creation
--------

Custom Data Solutions was initially called CDS for Chuck and Dee Schmidt.

Chuck is my older brother and Dee is his wife.

Chuck left his job at Michigan National Bank to offer data processing services, initially using a TRS-80 to print mailing labels.

He subsequently bought an Alpha Micro http://www.s100computers.com/Hardware%20Folder/Alpha%20Micro/History/History.htm and wrote some business applications.

On a visit to Detroit I explained to him the benefits of Unix and got him started, installing Interactive Unix from Kodak on a PC from 133 floppy disks in 1990.

Between 1983 and 1990 I worked as Vice President of International Banking Systems for RepublicBank Dallas, as a principal at a Distribution Requirements Planning Company and then at Computer Associates where I single handedly designed and wrote the ACH settlement software system in six weeks.

In 1990 I started Trinity Technical Services with two partners and ended up buying them both out.

My first contract was the selection of an ERP system for a distributor in Texas.  The chosen vendor was CIM-JIT and it was my first exposure to Oracle. I soon
became more proficient in the CIM-JIT software than the vendor and flew around 
country to modify to delivered software to comply with commitments to customers.

In the process I became quite proficient in fixing performance problems.

Convinced that Oracle was the finest relational database, I convinced Chuck to
come down to my Dallas office for a quick introduction.

He immediately saw the benefits of SQL over procedural file manipulation and 
also became an Oracle Partner.


CDS somehow happened to land a contract to handle rebates for Leaf Gum.

Technologies
------------

Unix
****

Oracle
******

Oracle Forms
&&&&&&&&&&&&

Oracle Reports
&&&&&&&&&&&&&&

PL/SQL
&&&&&

C
*

Java
****

JSP
***

Graphics
********

Javascript 
**********

Spreadsheets
************

Digital Unix
************



Linux
*****

Build tools
***********

Unit Tests
**********

Integration Tests
*****************

Chuck Retires
-------------

Database Consolidation
&&&&&&&&&&&&&&&&&&&&&&

Customer Matching
&&&&&&&&&&&&&&&&&

Address Validation
&&&&&&&&&&&&&&&&&&

Geo Encoding
&&&&&&&&&&&&

Exception Processing
&&&&&&&&&&&&&&&&&&&&

Spring
&&&&&&

Hibernate
&&&&&&&&&






Help
****
Make files
C
Pro*C
Forms

****
V




Business Model
==============

Custom Data Solutions
---------------------

http://custdata.com

Relationship
************

Custom Data Services was created by my brother Chuck.  I consulted to them for over 25 years and
taught him everything he knew from linux to relational databases, website creation, c and java.

In 2007 I accepted the position of CIO when my brother sold the company.  I gave up my consulting practice
on the promise that I could work remotely after my son went to college.

After I completely revised all of the infrastructure, which was horrible in more ways than can be credibly described
including the fact that they were seven years out of date on Oracle and had 14 instances running on three different 
machines, I upgraded to Oracle 10 and migrated everything to one instance and finally into one schema.

After I sold my house and all my possessions and moved to Costa Rica, they fired me.

They stole my javautil code, there was a lawsuit, but I would have had to have their current employees testify that
the code they were using was in production at my customer sites before I joined CDS.

A settlement was reached, I allowed them to continue to use the code while retaining all rights.


Processing Steps
----------------

Data is uploaded to the data processing service.

The file loads are analyzed at CDS using an old version of my javautil conditionidentification package.

The data is loading into prepost tables.

The data is posted

A snapshot of the data is made once a day for online web reporting in the form of spreadsheets.

Rebate programs are designed and the sales are tracked and the rebates calculated.

Rebate checks are mailed out.




