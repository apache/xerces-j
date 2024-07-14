@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.wg.xml -xsl xsl/run-schema11-tests-wg.xsl -param vendorId WG -param vendorUrl https://www.w3.org/XML/Schema > ../reports/xmlschema_wg_xsd11_testsuite_results.html 2>NUL
