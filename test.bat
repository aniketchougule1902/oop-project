@echo off
call build.bat
if errorlevel 1 exit /b 1
if exist out-test rmdir /s /q out-test
mkdir out-test
dir /s /b test\*.java > build\test-sources.txt
javac --release 17 -cp out -d out-test @build\test-sources.txt
if errorlevel 1 exit /b 1
java -cp "out;out-test" com.sportbooking.ProjectSelfTest
