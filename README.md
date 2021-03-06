# Enhort: Genomic Position Profiling

The rise of high-throughput methods in genomic research greatly expanded our 
knowledge about the functionality of the genome. At the same time, the amount 
of available genomic position data increased massively, e.g., through genome-wide 
profiling of protein binding, virus integration or DNA methylation. 
However, there is no specialized software to investigate integration site profiles 
of virus integration or transcription factor binding sites by correlating the sites 
with the diversity of available genomic annotations.

Here we present Enhort, a user-friendly software tool for relating large sets of 
genomic positions to a variety of annotations. It functions as a statistics based 
genome browser, not focused on a single locus but analyzing many genomic positions 
simultaneously. Enhort provides comprehensive yet easy-to-use methods for statistical 
analysis, visualization, and the adjustment of background models according to 
experimental conditions and scientific questions.



# Availability

Enhort is publicly available online at www.enhort.mni.thm.de and published under GNU General Public License.

The application was published in 2019: https://peerj.com/articles/cs-198/


# Use pre-build

A seperate SETUPINSTR.md file is available containing instructions to run Enhort on your own infrastructure.

# Build

A JDK with Java 8 or higher is needed, as well as Maven.

Build steps:
- Download the sources from https://git.thm.de/mmnz21/Enhort
- cd into the enhortlib folder
- run 'mvn package' to build the library
- run the following to add the library to the local .m2 folder and repository:
    mvn install:install-file -Dfile=Enhortlib-1.01.jar -DpomFile=pom.xml
- cd back and into the /backend
- run 'mvn package -Dmaven.test.skip=true'
- Tests are skipped because several test-files are needed to run them 
- cd back and into the /frontend directory
- run 'mvn package' 
- go up one directory
- rename both jars with: 
    mv backend/target/Enhort-1.01-jar-with-dependencies.jar enhort.jar
    mv frontend/target/Enhort-1.01-jar frontend.jar
- continue with the set up as described in the SETUPINSTR.md file 


# VM

There is a VirtualBox image with Enhort set up for reference and to test with your own data:

https://zenodo.org/record/2597397
https://doi.org/10.5281/zenodo.2597397

DOI: 10.5281/zenodo.2597397



