@echo off
echo ========================================
echo  LMS Build Script — PostgreSQL Version
echo  SochTech | Niraj Patel
echo ========================================

if not exist bin mkdir bin

echo Compiling Java files...
javac -cp "lib/*" -d bin -sourcepath src src/lms/Main.java

if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED!
    echo Common fixes:
    echo   1. Make sure postgresql-xx.jar is in lib/ folder
    echo   2. Make sure Java JDK is installed - java -version
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Starting LMS...
java -cp "bin;lib/*" lms.Main

pause
