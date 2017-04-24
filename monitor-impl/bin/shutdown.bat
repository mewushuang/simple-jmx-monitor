@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  monitor-impl startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and MONITOR_IMPL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Dapp.home=%~dp0.." "-Duser.dir=%~dp0.." "-Dlog4j.configuration=file:///%~dp0../conf/log4j.xml" "-Dapplication.confDir=%~dp0../conf/" "-Dcom.van.monitor.server.action=restart"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASS_PATH=%APP_HOME%\lib\monitor-impl-1.0-release.jar;%APP_HOME%\lib\jackson-annotations-2.2.1.jar;%APP_HOME%\lib\jackson-core-2.2.1.jar;%APP_HOME%\lib\jackson-databind-2.2.1.jar;%APP_HOME%\lib\libsigar-amd64-freebsd-6.so;%APP_HOME%\lib\libsigar-amd64-linux.so;%APP_HOME%\lib\libsigar-amd64-solaris.so;%APP_HOME%\lib\libsigar-ia64-hpux-11.sl;%APP_HOME%\lib\libsigar-ia64-linux.so;%APP_HOME%\lib\libsigar-pa-hpux-11.sl;%APP_HOME%\lib\libsigar-ppc-aix-5.so;%APP_HOME%\lib\libsigar-ppc-linux.so;%APP_HOME%\lib\libsigar-ppc64-aix-5.so;%APP_HOME%\lib\libsigar-ppc64-linux.so;%APP_HOME%\lib\libsigar-s390x-linux.so;%APP_HOME%\lib\libsigar-sparc-solaris.so;%APP_HOME%\lib\libsigar-sparc64-solaris.so;%APP_HOME%\lib\libsigar-universal-macosx.dylib;%APP_HOME%\lib\libsigar-universal64-macosx.dylib;%APP_HOME%\lib\libsigar-x86-freebsd-5.so;%APP_HOME%\lib\libsigar-x86-freebsd-6.so;%APP_HOME%\lib\libsigar-x86-linux.so;%APP_HOME%\lib\libsigar-x86-solaris.so;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\sigar-amd64-winnt.dll;%APP_HOME%\lib\sigar-x86-winnt.dll;%APP_HOME%\lib\sigar-x86-winnt.lib;%APP_HOME%\lib\sigar.jar;%APP_HOME%\lib\slf4j-api-1.7.6.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.6.jar;%APP_HOME%\lib\monitor-api-1.0-release.jar
@setlocal enabledelayedexpansion
set CLASS_PATH=.
for  %%i in (%APP_HOME%\lib\*) do (
 set CLASS_PATH=!CLASS_PATH!;%%i
)

@rem Execute monitor-impl
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MONITOR_IMPL_OPTS%  -classpath "%CLASS_PATH%" com.van.monitor.example.SimpleServer %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MONITOR_IMPL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MONITOR_IMPL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
