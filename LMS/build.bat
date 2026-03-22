@echo off
REM ============================================================
REM  LMS Build & Run Script for Windows
REM  Usage: Double-click build.bat OR run from cmd
REM ============================================================

SET SRC_DIR=src
SET BIN_DIR=bin
SET LIB_DIR=lib
SET MAIN_CLASS=lms.Main
SET JAR=%LIB_DIR%\mysql-connector-java-8.0.33.jar

echo ============================================
echo  LMS - Learning Management System
echo ============================================

REM Create output directory
if not exist %BIN_DIR% mkdir %BIN_DIR%
echo [1/3] Output directory ready.

REM Find all .java files and compile
echo [2/3] Compiling sources...
dir /s /b %SRC_DIR%\*.java > sources.txt
javac -cp %JAR% -d %BIN_DIR% @sources.txt

IF ERRORLEVEL 1 (
    echo Compilation FAILED!
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo Compilation successful!

REM Run
echo [3/3] Starting LMS...
java -cp "%BIN_DIR%;%JAR%" %MAIN_CLASS%
pause
