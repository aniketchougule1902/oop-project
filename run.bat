@echo off
if not exist build\sports-booking.jar call build.bat
if errorlevel 1 exit /b 1
java -jar build\sports-booking.jar
