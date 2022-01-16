@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.oracle.xml -xsl xsl/run-schema11-tests.xsl -param vendorId Oracle -param vendorUrl https://www.oracle.com > ../reports/oracle_xsd11_testsuite_results.html 2>NUL
