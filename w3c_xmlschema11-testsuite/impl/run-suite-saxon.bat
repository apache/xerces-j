@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XERCES_BUILD%;%XALAN_HOME% org.apache.xalan.xslt.Process -in suite.saxon.xml -xsl xsl/run-schema11-tests.xsl -param vendorId Saxonica -param vendorUrl https://www.saxonica.com > ../reports/saxon_xsd11_testsuite_results.html 2>NUL
