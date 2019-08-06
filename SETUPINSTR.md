# Basic

The following guide is written for a Linux based system. Enhort runs on Java 8 and needs a SQlite database to store information about available tracks.
It is recommended to download and use the pre-build database.

For full usage it is recommended to have server with about 32 GB Mem for the current database. However, Enhort is capable of beeing run on a small PC or server with less annotation tracks.

Enhort is build to run on two different servers, a computation back-end server and a visibile front-end server. However, it is possible to run both on the same server. Use 127.0.0.1 as ip then.


# Database 

### Use pre-build database
Download the minimal SQlite database and the corresponding .bed-tracks from 
    https://homepages.thm.de/~mmnz21/minimal.db 
    https://homepages.thm.de/~mmnz21/enhort_bed_files_hg19.tar.gz

Save the database and unpack .bed-files in a known directory.

### Build your own database
TODO 

# Server setup
Download the enhort.jar from 
    https://homepages.thm.de/~mmnz21/enhort.jar


### Run the server
The server is run with the following command:

    java -jar -Xmx4g -XX:StringTableSize=1000003 /path/to/server/jar/enhort.jar --data-path /path/to/data/directory/  --db /path/to/database.db [-p PORTNUMBER --custom /path/to/custom/tracks/without/db]


- The -Xmx4g flag raises the available memory to 4 GB, please specify your available memory here. 
- The StringTableSize improves start up speed for loading the data
- -p sets the port to listen on. Default is: 42412
- The server takes about 2 minutes to start, when the message "Still loading track files. Stopping now" the server is up and running


# Frontend setup
Download the frontend archive containing the .jar and the contig sizes for hg19 and hg38 from 
    https://homepages.thm.de/~mmnz21/frontend.tar.gz
Create a empty directory /logs in user home directory for logfiles

The frontend is run with the following command:

    java -Xmx2g -Dmultipart.maxFileSize=20MB -Dmultipart.maxRequestSize=20MB -Dspring.profiles.active=production -jar frontend.jar --ip 127.0.0.1 --contigs-path /path/to/contig/size/files

- You should specify the maximum allowed upload file size
- You need to specify the address of the backend server. If both are on the same server use 127.0.0.1
- The path to the contig sizes file (which is included in the frontend-archive)
- A usage statistics file is written at the given location of the statistics file or at /tmp


# Bed test file

You can get a custom build test file containing some integration sites for hg19 here:

    https://homepages.thm.de/~mmnz21/test.bed


# Misc

### Encryption on your own server
TODO 


### Package Frontend.tar.gz
 tar cvf frontend.tar frontend.jar ../../con/

####  Content: 
-  5 frontend.jar
-  6 con/
-  7 con/contigs_GRCh38
-  8 con/contigs_hg19
-  9 con/contigs_hg18                                                                                                


### Known issues

- The sample button does not work on custom systems because the .bed-file used for the sample run is not available
