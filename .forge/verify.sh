#!/usr/bin/env bash
set -euo pipefail
echo "=== Forge Verification ==="
echo "--- Build ---"
./gradlew assembleDebug --no-daemon --quiet
echo "--- Unit Tests ---"
./gradlew testDebugUnitTest --no-daemon --quiet
echo "--- DONE ---"
