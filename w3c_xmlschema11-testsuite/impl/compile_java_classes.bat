@echo off

REM set these variables for local environment

SET JAVA_HOME=d:/jdk1.7.0_80

SET XERCES_BUILD=d:/eclipseWorkspaces/xercesj/xerces-java-xml-schema-1.1-dev/build

%JAVA_HOME%/bin/javac -classpath %XERCES_BUILD%/xercesImpl.jar;%XERCES_BUILD%/xml-apis.jar *.java