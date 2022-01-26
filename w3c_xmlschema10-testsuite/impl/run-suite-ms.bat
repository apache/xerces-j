@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.ms.xml -xsl xsl/run-schema10-tests.xsl -param vendorId Microsoft -param vendorUrl https://www.microsoft.com > ../reports/ms_xsd10_testsuite_results.html 2>NUL