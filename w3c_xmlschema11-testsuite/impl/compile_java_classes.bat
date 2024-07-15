@echo off

REM set these variables for local environment

SET JAVA_HOME=d:/jdk-1.8

SET XERCES_BUILD=d:/eclipseWorkspaces/xercesj/xml-schema-1.1-dev/build

%JAVA_HOME%/bin/javac -classpath %XERCES_BUILD%/xercesImpl.jar;%XERCES_BUILD%/xml-apis.jar *.java