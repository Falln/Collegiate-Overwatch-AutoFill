@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  CollegiateOW_AutoFill startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and COLLEGIATE_OW_AUTO_FILL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\CollegiateOW_AutoFill-1.0-SNAPSHOT.jar;%APP_HOME%\lib\google-api-services-sheets-v4-rev20220927-2.0.0.jar;%APP_HOME%\lib\google-api-client-2.0.0.jar;%APP_HOME%\lib\google-oauth-client-jetty-1.34.1.jar;%APP_HOME%\lib\json-20210307.jar;%APP_HOME%\lib\jsoup-1.14.3.jar;%APP_HOME%\lib\htmlunit-2.53.0.jar;%APP_HOME%\lib\google-oauth-client-java6-1.34.1.jar;%APP_HOME%\lib\google-oauth-client-1.34.1.jar;%APP_HOME%\lib\google-http-client-gson-1.42.1.jar;%APP_HOME%\lib\google-http-client-apache-v2-1.42.1.jar;%APP_HOME%\lib\google-http-client-1.42.1.jar;%APP_HOME%\lib\opencensus-contrib-http-util-0.31.1.jar;%APP_HOME%\lib\guava-31.1-jre.jar;%APP_HOME%\lib\httpmime-4.5.13.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\httpcore-4.4.15.jar;%APP_HOME%\lib\xalan-2.7.2.jar;%APP_HOME%\lib\commons-lang3-3.12.0.jar;%APP_HOME%\lib\commons-text-1.9.jar;%APP_HOME%\lib\htmlunit-core-js-2.53.0.jar;%APP_HOME%\lib\neko-htmlunit-2.53.0.jar;%APP_HOME%\lib\htmlunit-cssparser-1.10.0.jar;%APP_HOME%\lib\commons-io-2.10.0.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-net-3.8.0.jar;%APP_HOME%\lib\dec-0.1.2.jar;%APP_HOME%\lib\salvation2-3.0.0.jar;%APP_HOME%\lib\websocket-client-9.4.43.v20210629.jar;%APP_HOME%\lib\gson-2.9.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.12.0.jar;%APP_HOME%\lib\error_prone_annotations-2.11.0.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\opencensus-api-0.31.1.jar;%APP_HOME%\lib\serializer-2.7.2.jar;%APP_HOME%\lib\xercesImpl-2.12.0.jar;%APP_HOME%\lib\jetty-client-9.4.43.v20210629.jar;%APP_HOME%\lib\websocket-common-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-http-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-io-9.4.43.v20210629.jar;%APP_HOME%\lib\jetty-util-9.4.43.v20210629.jar;%APP_HOME%\lib\grpc-context-1.27.2.jar;%APP_HOME%\lib\xml-apis-1.4.01.jar;%APP_HOME%\lib\websocket-api-9.4.43.v20210629.jar


@rem Execute CollegiateOW_AutoFill
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %COLLEGIATE_OW_AUTO_FILL_OPTS%  -classpath "%CLASSPATH%" CollegiateSheetsAutoFill %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable COLLEGIATE_OW_AUTO_FILL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%COLLEGIATE_OW_AUTO_FILL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
