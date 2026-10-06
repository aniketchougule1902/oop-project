@echo off
setlocal
if exist out rmdir /s /q out
if exist build rmdir /s /q build
mkdir out
mkdir build
dir /s /b src\*.java > build\sources.txt
javac --release 17 -d out @build\sources.txt
if errorlevel 1 exit /b 1
jar --create --file build\sports-booking.jar --main-class com.sportbooking.app.Main -C out .
if errorlevel 1 exit /b 1
echo Build successful: build\sports-booking.jar
