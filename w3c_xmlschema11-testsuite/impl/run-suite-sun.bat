@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.sun.xml -xsl xsl/run-schema11-tests-2.xsl -param vendorId "Sun Microsystems" -param vendorUrl https://www.oracle.com > ../reports/sun_xsd11_testsuite_results.html 2>NUL
