@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.nist.xml -xsl xsl/run-schema10-tests.xsl -param vendorId NIST -param vendorUrl https://www.nist.gov > ../reports/nist_xsd10_testsuite_results.html 2>NUL