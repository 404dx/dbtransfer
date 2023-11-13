@echo off

set FILE_PATH=%1
set CURRENT_PATH=%cd%
set ABSOLUTE_FILE_PATH=%CURRENT_PATH%/%FILE_PATH%
set RUN_ARGS=%FILE_PATH%


set JAVA_HOME=%JAVA_HOME%

if "%FILE_PATH%" == "" (
    echo "ERROR: Please specify the configuration file"
    goto end
)

if not exist %ABSOLUTE_FILE_PATH% (
    if not exist %FILE_PATH% goto noFile
) else (
    set RUN_ARGS=%ABSOLUTE_FILE_PATH%
)

if "%JAVA_HOME%" == "" goto noJavaHome

goto run

:noFile
echo "ERROR: file does not exist; error in file path: %FILE_PATH%"
goto end

:noJavaHome
echo "ERROR: No JAVA_HOME was found"
goto end

:run
java -classpath transfer.jar main.co.qingyu.Application %RUN_ARGS%

:end