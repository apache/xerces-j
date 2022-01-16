@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.ibm.xml -xsl xsl/run-schema11-tests.xsl -param vendorId IBM -param vendorUrl https://www.ibm.com > ../reports/ibm_xsd11_testsuite_results.html 2>NUL
