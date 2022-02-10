@echo off

REM set these variables for local environment

SET JAVA_HOME=d:/jdk1.7.0_80

SET XERCES_BUILD=d:/eclipseWorkspaces/xercesj/xerces-java-trunk/build
SET XALAN_HOME=d:/xalan-j_2_7_2

echo ##### Running W3C XML Schema 1.0 test suite, on Apache Xerces XML Schema 1.0 processor #####
echo.

echo Running NIST XML Schema 1.0 tests
echo.
call run-suite-nist.bat

echo Running Sun Microsystems XML Schema 1.0 tests
echo.
call run-suite-sun.bat

echo Running Microsoft XML Schema 1.0 tests
echo.
call run-suite-ms.bat

echo Running Boeing XML Schema 1.0 tests
echo.
call run-suite-boeing.bat

echo Generating overall XML Schema 1.0 test suite results
echo.
call generate-overall-results.bat

echo All the tests have been run successfully. The reports are available within the folder ../reports.
