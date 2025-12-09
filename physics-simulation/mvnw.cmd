@REM Maven Wrapper script for Windows
@REM This script runs Maven commands

@echo off
setlocal

set MAVEN_CMD=mvn

where %MAVEN_CMD% >nul 2>nul
if %ERRORLEVEL% == 0 (
    %MAVEN_CMD% %*
) else (
    echo Maven not found in PATH.
    echo Please install Maven or set the PATH environment variable.
    echo Download Maven from: https://maven.apache.org/download.cgi
    exit /b 1
)

endlocal
