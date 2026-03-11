
# Xerces Java Build Instructions

This document contains instructions to build Xerces-J.

Before building Xerces-J, users need the source package and tools package 
available from the [Apache Xerces-J project's distribution web page](https://xerces.apache.org/xerces2-j/).

To build a particular released version, download both 
the Xerces-J-src.X.Y.Z.zip and Xerces-J-tools.X.Y.Z.zip 
files for that Xerces-J release (where "X.Y.Z" is the version
number) and extract them in the same directory. If you're using a Unix 
variant like Linux, download the equivalent .tar.gz files instead of 
the .zip files.

To build from head, clone this GitHub repository. 

You also need to have the Java Development Kit (JDK) version 1.8 or
higher installed on your system. The latest Xerces-J codebase on this GitHub 
repo has Java language maximum source/target level requirement of 1.8.
 
Before initiating any part of the build, set the JAVA_HOME environment 
variable to the installation directory of your JDK.

[Ant](https://ant.apache.org/) 1.10.2 or later is needed to build everything in Xerces-J, including
the documentation. This tool, and the others needed (besides the
pre-requisite JDK) are contained within the tools package. To
make building Xerces-J packages easier, a Windows batch file and a Linux 
shell script are included.

If you only want to compile the source code and make the JAR files,
run the following command on Windows:

```
    build.bat jars
```

or from Linux (make sure that build.sh is executable):

```
    ./build.sh jars
```

This compiles all the source code and generates the JAR
files that are available as part of the binary package. After
these Xerces-J builds, the build results are located in the "build" directory.

If all results of Xerces-J build are needed, including the documentation,
run the build batch file or shell script specifying an Ant build "all"
target instead of "jars".

We use the [JProfiler](https://www.ej-technologies.com/jprofiler) tool for Java software run-time 
analysis and optimization of Xerces-J software. 


Sincerely,
Apache Xerces Team
