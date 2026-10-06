#!/usr/bin/env bash
set -euo pipefail
if [ ! -f build/sports-booking.jar ]; then
  ./build.sh
fi
java -jar build/sports-booking.jar
