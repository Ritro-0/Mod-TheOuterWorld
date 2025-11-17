@echo off
REM Build script that ensures no other builds are running and clears locks first

echo Checking for running Java processes...
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo Warning: Java processes detected. Stopping them...
    taskkill /F /IM java.exe /FI "WINDOWTITLE eq *gradle*" 2>NUL
    timeout /t 2 /nobreak >nul
)

echo Stopping Gradle daemons...
call gradlew.bat --stop 2>nul
timeout /t 2 /nobreak >nul

echo Clearing lock files...
powershell -Command "if (Test-Path '$env:USERPROFILE\scoop\apps\gradle\current\.gradle\caches\fabric-loom') { Get-ChildItem -Path '$env:USERPROFILE\scoop\apps\gradle\current\.gradle\caches\fabric-loom' -Recurse -File -Filter '*.lock' | Remove-Item -Force -ErrorAction SilentlyContinue }"

echo Waiting 3 seconds for locks to clear...
timeout /t 3 /nobreak >nul

echo.
echo Starting build with single-threaded execution...
echo This may take longer but should avoid lock conflicts.
echo.

REM Build with explicit single-threaded execution
call gradlew.bat --no-daemon --no-parallel %*

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed. If you see lock errors:
    echo 1. Make sure no other Gradle builds are running
    echo 2. Close Cursor/IDE and try again
    echo 3. Reboot if locks persist
    pause
)

