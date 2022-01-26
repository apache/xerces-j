@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.sun.xml -xsl xsl/run-schema10-tests.xsl -param vendorId "Sun Microsystems" -param vendorUrl https://www.oracle.com > ../reports/sun_xsd10_testsuite_results.html 2>NUL