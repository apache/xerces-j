@echo off

REM set these variables for local environment

SET JAVA_HOME=d:/jdk1.7.0_80

SET XERCES_BUILD=d:/eclipseWorkspaces/xercesj/xerces-java-xml-schema-1.1-dev/build
SET XALAN_HOME=d:/xalan-j_2_7_2

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.sun.xml -xsl xsl/run-schema11-tests-2.xsl -param vendorId "Sun Microsystems" -param vendorUrl https://www.oracle.com > ../reports/sun_xsd11_testsuite_results.html 2>NUL