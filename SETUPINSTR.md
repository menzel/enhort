# Basic

The following guide is written for a Linux based system. Enhort runs on Java 8 and needs a SQlite database to store information about available tracks.
It is recommended to download and use the pre-build database.

For full usage it is recommended to have server with about 32 GB Mem for the current database. However, Enhort is capable of beeing run on a small PC or server with less annotation tracks.

Enhort is build to run on two different servers, a computation back-end server and a visibile front-end server. However, it is possible to run both on the same server. Use 127.0.0.1 as ip then.

# Database 

### Use pre-build database
Download the SQlite database and the corresponding .bed tracks from .... Save the database and the data files in a known directory.

### Build own database
TODO 

# Server setup
Download the enhort.jar from ....

Create a empty directory /logs in user home directory for logfiles


### Run the server
The server is run with the following command:

    java -jar -Xmx32g -XX:StringTableSize=1000003 /path/to/server/jar/enhort.jar /path/to/data/directory/  /path/to/database.db


- The -Xmx32g flag raises the available memory to 32 GB, please specify your available memory here. 
- The StringTableSize improves start up speed for loading the data
- Currently the port is fixed to 42412


# Frontend setup
Download the frontend archive containing the .jar and the contig sizes for hg19 and hg38.
Create a empty directory /logs in user home directory for logfiles

The frontend is run with the following command:

    java -Xmx2g -Dmultipart.maxFileSize=20MB -Dmultipart.maxRequestSize=20MB -Dspring.profiles.active=production -jar frontend.jar 127.0.0.1 /path/to/contig/size/files /path/to/statistics/file

- You can specify the maximum allowed upload file size
- You need to specify the address of the backend server. If both are run on the same server use 127.0.0.1
- The path to the contig sizes .bed file is needed
- A usage statistics file is written at the given location of the statistics file



# Misc

### Encryption
TODO 


### Known issues

- The sample button does not work on custom systems because the .bed-file used for the sample run is not available
