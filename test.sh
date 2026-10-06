#!/usr/bin/env bash
set -euo pipefail
./build.sh
rm -rf out-test
mkdir -p out-test
find test -name "*.java" | sort > build/test-sources.txt
javac --release 17 -cp out -d out-test @build/test-sources.txt
java -cp "out:out-test" com.sportbooking.ProjectSelfTest
