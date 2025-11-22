@echo off
echo Clearing locks before build...
call gradlew.bat --stop >nul 2>&1
timeout /t 2 /nobreak >nul

echo Killing any Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo Starting client build...
call gradlew.bat runClient --no-daemon

