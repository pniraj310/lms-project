#!/bin/bash
# ============================================================
#  LMS Build & Run Script
#  Usage: bash build.sh
# ============================================================

# Project paths
SRC_DIR="src"
BIN_DIR="bin"
LIB_DIR="lib"
MAIN_CLASS="lms.Main"

# MySQL Connector JAR must be placed in lib/
# Download from: https://dev.mysql.com/downloads/connector/j/
CONNECTOR_JAR="$LIB_DIR/mysql-connector-java-8.0.33.jar"

echo "============================================"
echo " LMS - Learning Management System"
echo "============================================"

# Step 1: Create bin directory
mkdir -p $BIN_DIR
echo "[1/3] Output directory ready: $BIN_DIR/"

# Step 2: Compile all Java files
echo "[2/3] Compiling Java sources..."
find $SRC_DIR -name "*.java" > sources.txt

javac -cp "$CONNECTOR_JAR" -d $BIN_DIR @sources.txt

if [ $? -ne 0 ]; then
    echo "❌ Compilation FAILED. Fix errors above."
    rm sources.txt
    exit 1
fi

rm sources.txt
echo "✅ Compilation successful!"

# Step 3: Run the application
echo "[3/3] Starting LMS..."
echo ""
java -cp "$BIN_DIR:$CONNECTOR_JAR" $MAIN_CLASS

# ── Windows note ──
# Replace ':' with ';' in the classpath on Windows CMD:
# java -cp "bin;lib/mysql-connector-java-8.0.33.jar" lms.Main
