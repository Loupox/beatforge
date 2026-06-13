#!/usr/bin/env bash
set -euo pipefail

cd /workspace

if [ ! -f ./gradlew ]; then
    echo ">> Génération du wrapper Gradle..."
    gradle wrapper --gradle-version "${GRADLE_VERSION:-8.7}" --no-daemon
fi

chmod +x ./gradlew

if [ "$#" -eq 0 ]; then
    exec ./gradlew assembleDebug --no-daemon --stacktrace
else
    exec "$@"
fi
