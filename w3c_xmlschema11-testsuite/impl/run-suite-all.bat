@echo off

echo ##### Running W3C XML Schema 1.1 test suite, on Apache Xerces XSD 1.1 processor #####
echo.

echo Running IBM XML Schema 1.1 tests
echo.
call run-suite-ibm.bat

echo Running Saxonica XML Schema 1.1 tests
echo.
call run-suite-saxon.bat

echo Running Oracle XML Schema 1.1 tests
echo.
call run-suite-oracle.bat

echo Running Sun Microsystems XML Schema 1.1 tests
echo.
call run-suite-sun.bat

echo Running Microsoft XML Schema 1.1 tests
echo.
call run-suite-MS.bat

echo Running WG XML Schema 1.1 tests
echo.
call run-suite-wg.bat

echo All the tests have been run successfully. The reports are available within the folder ../reports.
