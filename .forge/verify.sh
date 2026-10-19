#!/usr/bin/env bash
# FORGE verification script — the same checks CI runs (.github/workflows/ci.yml)
# Exit 0 = pass, non-zero = fail
set -euo pipefail

cd "$(dirname "$0")/.."

echo "=== ForgeStarter verification ==="

# Every shared module. Add new ones here (and in settings.gradle.kts).
MODULES=(domain data design ui)

# 1. Android: every shared module plus the app (the app is where the shared
#    modules' public APIs are actually consumed).
echo "[1/3] Compiling Android (shared modules + androidApp)..."
android_tasks=()
for m in "${MODULES[@]}"; do android_tasks+=(":shared:$m:compileAndroidMain"); done
./gradlew "${android_tasks[@]}" :androidApp:compileDebugKotlin --no-daemon --quiet

# 2. iOS: every shared module, so iosMain code and iOS-only dependency wiring
#    (e.g. MainViewController in :shared:ui) are compiled too.
echo "[2/3] Compiling iOS (iosSimulatorArm64, all shared modules)..."
ios_tasks=()
for m in "${MODULES[@]}"; do ios_tasks+=(":shared:$m:compileKotlinIosSimulatorArm64"); done
./gradlew "${ios_tasks[@]}" --no-daemon --quiet

# 3. Unit tests — and proof that they ran. iOS test tasks are skipped on
#    Linux, so a module whose tests only target iOS "passes" with zero tests.
#    Stale results are removed first so every count below is from this run.
echo "[3/3] Running unit tests..."
rm -rf shared/*/build/test-results
./gradlew allTests --no-daemon --quiet

missing=()
total=0
for m in "${MODULES[@]}"; do
  if ! find "shared/$m/src" -path "*Test/*" -name "*.kt" 2>/dev/null | grep -q .; then
    continue   # no test sources in this module
  fi
  count=0
  while IFS= read -r xml; do
    n=$(grep -o 'tests="[0-9]*"' "$xml" | head -1 | grep -o '[0-9]*')
    count=$((count + ${n:-0}))
  done < <(find "shared/$m/build/test-results" -name 'TEST-*.xml' 2>/dev/null)
  echo "      :shared:$m — $count tests"
  total=$((total + count))
  if [ "$count" -eq 0 ]; then missing+=(":shared:$m"); fi
done
if [ ${#missing[@]} -gt 0 ]; then
  echo "FAIL: test sources exist but no tests ran in: ${missing[*]}"
  echo "      (enable a host-runnable test target, e.g. withHostTest {} in androidLibrary)"
  exit 1
fi
echo "      $total tests ran"

echo "=== Verification passed ==="
