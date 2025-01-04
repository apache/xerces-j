@echo off
rem
rem ==========================================================================
rem = Licensed to the Apache Software Foundation (ASF) under one or more
rem = contributor license agreements.  See the NOTICE file distributed with
rem = this work for additional information regarding copyright ownership.
rem = The ASF licenses this file to You under the Apache License, Version 2.0
rem = (the "License"); you may not use this file except in compliance with
rem = the License.  You may obtain a copy of the License at
rem =
rem =     http://www.apache.org/licenses/LICENSE-2.0
rem =
rem = Unless required by applicable law or agreed to in writing, software
rem = distributed under the License is distributed on an "AS IS" BASIS,
rem = WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem = See the License for the specific language governing permissions and
rem = limitations under the License.
rem ==========================================================================
rem
rem     build.bat: Build xml-commons using Ant 
rem     Usage: build [ant-options] [targets]
rem     Setup:
rem         - you should set JAVA_HOME
echo xml-commons Build
echo -----------------

if "%JAVA_HOME%" == "" goto error

set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%JAVA_HOME%\lib\classes.zip
set LOCALCLASSPATH=%LOCALCLASSPATH%;.\tools\ant.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;.\tools\ant-nodeps.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;.\tools\ant-launcher.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;.\tools\ant-junit.jar

echo Building with ant classpath %LOCALCLASSPATH%
echo Starting Ant...
"%JAVA_HOME%\bin\java.exe" -Dant.home="./tools" -classpath "%LOCALCLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:error
echo "ERROR: JAVA_HOME not found in your environment."
echo "Please, set the JAVA_HOME variable in your environment to match the"
echo "location of the Java Virtual Machine you want to use."

:end
set LOCALCLASSPATH=
@echo on
