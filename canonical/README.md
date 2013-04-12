IHTSDO Snomed Publication Tools
===============================

Canonical Form
--------------

This program takes an an input 2 text files:

1. Set of concepts of this form:

        CONCEPTID  CONCEPTSTATUS	FULLYSPECIFIEDNAME	CTV3ID	SNOMEDID	ISPRIMITIVE
        280844000	0	Entire body of seventh thoracic vertebra (body structure)	Xa1Y9	T-11875	1
        280845004	0	Entire body of eighth thoracic vertebra (body structure)	Xa1YA	T-11876	1
        etc.

2. Set of relationships of this form:

        RELATIONSHIPID  CONCEPTID1	RELATIONSHIPTYPE	CONCEPTID2	CHARACTERISTICTYPE	REFINABILITY	RELATIONSHIPGROUP
        100000028	280844000	116680003	71737002	0	0	0
        100001029	280845004	116680003	280737002	0	0	0
        etc.
    
and produces an output file called 'canonical form' of this form:

    CONCEPTID1  RELATIONSHIPTYPE	CONCEPTID2	RELATIONSHIPGROUP
    280844000	116680003	71737002	0
    280845004	116680003	280737002	0

The rules for the transformation taking place can be found in [this PDF document](https://github.com/sparkling/snomed-publish/blob/master/doc/doc1_CanonicalTableGuide_Current-en-US_INT_20130131.pdf?raw=true) [PDF].

When you run this program, you have the option of using either a disk based embedded database (H2), or an in-memory database.
The disk based database is slower to use, but has a smaller memory footprint. The in-memory database requires about 
2Gb of heap space ('-Xmx2000m').

You will need to have the Java 7 JRE and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the project directory and type:

    mvn clean package
    
The distribution jar file can be found at target/canonical.jar after this. No other file is required in order to run the program,
and can be distributed as a single file.

For help on how to run the program, type:

    java -jar canonical.jar -h
    
You will then see this output:

    -h, --help      Print this help menu
    -t. --triples   File containing all the relationships that you want to process, aka 'Relationships_Core'
    -c, --concepts  File containing all the concepts referenced in the relationships file, aka 'Concepts_Core'
    -o, --output    Destination file to write the canonical output results to
    -d, --database  Optional. Specify location of database file. If not specified, 
                    defaults to an in-memory database (minium 2Gb of heap space required)

For example, to launch with an in-memory database, use this command:

    java -Xmx2000m -jar -t relationships.input.file.txt -c concepts.input.file.txt -o canonical.out.txt

Don't forget the '-Xmx=2000m' option, or the program will not complete. 

Or, to launch with a disk backed databse, you can use this command:

    java -jar -t relationships.input.file.txt -c concepts.input.file.txt -o canonical.out.txt -d /tmp/canonical.tmp.db
    
The output from the console will look something like this:

    2013-04-02 18:34:20,677 INFO [com.ihtsdo.snomed.canonical.Main] - Using an in-memory database
    2013-04-02 18:34:20,677 INFO [com.ihtsdo.snomed.canonical.Main] - Initialising database
    2013-04-02 18:34:20,718 DEBUG [org.jboss.logging] - Logging Provider: org.jboss.logging.Log4jLoggerProvider
    2013-04-02 18:34:22,482 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Populating database
    2013-04-02 18:34:22,482 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Populating Concepts
    2013-04-02 18:34:26,298 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Populated [397787] concepts
    2013-04-02 18:34:26,298 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Populating relationships
    2013-04-02 18:34:39,003 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Populated [1454681] relationships
    2013-04-02 18:34:39,005 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Creating isA hierarchy
    2013-04-02 18:35:14,570 INFO [com.ihtsdo.snomed.canonical.HibernateDatabaseImporter] - Created [542486] isA relationships
    2013-04-02 18:35:14,571 INFO [com.ihtsdo.snomed.canonical.Main] - Completed import in 52 seconds
    2013-04-02 18:35:14,571 INFO [com.ihtsdo.snomed.canonical.Main] - Running algorithm
    2013-04-02 18:35:15,239 INFO [com.ihtsdo.snomed.canonical.Main] - Calculating unshared defining characteristics
    2013-04-02 18:35:38,237 INFO [com.ihtsdo.snomed.canonical.Main] - Found 510392 unshared defining characteristics
    2013-04-02 18:35:38,237 INFO [com.ihtsdo.snomed.canonical.Main] - Calculating immidiate primitive concepts
    2013-04-02 18:35:46,365 INFO [com.ihtsdo.snomed.canonical.Main] - Found 38692 immidiate primitive concepts
    2013-04-02 18:35:46,449 INFO [com.ihtsdo.snomed.canonical.Main] - Completed algorithm in 31 seconds
    2013-04-02 18:35:46,449 INFO [com.ihtsdo.snomed.canonical.Main] - Writing results to target/output.txt
    2013-04-02 18:35:47,468 INFO [com.ihtsdo.snomed.canonical.Main] - Wrote 549086 lines
    2013-04-02 18:35:47,471 INFO [com.ihtsdo.snomed.canonical.Main] - Overall program completion in 86 seconds
    2013-04-02 18:35:47,471 INFO [com.ihtsdo.snomed.canonical.Main] - Closing database

where the results would be stored in canonical.out.txt, as specified above