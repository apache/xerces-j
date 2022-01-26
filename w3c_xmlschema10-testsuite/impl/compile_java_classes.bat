@echo off

REM set these variables for local environment

SET JAVA_HOME=d:/jdk1.7.0_80

SET XERCES_BUILD=d:/xerces-2_12_2

%JAVA_HOME%/bin/javac -classpath %XERCES_BUILD%/xercesImpl.jar;%XERCES_BUILD%/xml-apis.jar *.java