#!/usr/bin/env bash
set -euo pipefail

APK="${1:-app/build/outputs/apk/debug/app-debug.apk}"

if [ ! -f "$APK" ]; then
    echo ">> APK non trouvé: $APK"
    echo ">> Lance d'abord: docker compose run --rm build"
    exit 1
fi

echo ">> Installation de $APK sur l'appareil..."
adb install -r "$APK"
echo ">> Terminé !"