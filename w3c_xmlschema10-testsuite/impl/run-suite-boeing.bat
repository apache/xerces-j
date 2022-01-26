@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.boeing.xml -xsl xsl/run-schema10-tests.xsl -param vendorId Boeing -param vendorUrl https://www.boeing.com > ../reports/boeing_xsd10_testsuite_results.html 2>NUL