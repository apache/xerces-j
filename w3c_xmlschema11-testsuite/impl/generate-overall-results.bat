@echo off

%JAVA_HOME%/bin/java -Djava.endorsed.dirs=%XALAN_HOME% org.apache.xalan.xslt.Process -in xsl/generate_aggregate_results.xsl -xsl xsl/generate_aggregate_results.xsl > ../reports/overall_xsd11_testsuite_results.html 2>NUL
