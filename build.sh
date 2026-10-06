#!/usr/bin/env bash
set -euo pipefail
rm -rf out build
mkdir -p out build
find src -name "*.java" | sort > build/sources.txt
javac --release 17 -d out @build/sources.txt
jar --create --file build/sports-booking.jar --main-class com.sportbooking.app.Main -C out .
echo "Build successful: build/sports-booking.jar"
