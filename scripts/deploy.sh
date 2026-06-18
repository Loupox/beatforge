#!/usr/bin/env bash
set -euo pipefail

APK="${1:-app/build/outputs/apk/debug/app-debug.apk}"

if [ ! -f "$APK" ]; then
    echo ">> APK non trouvé: $APK"
    echo ">> Lance d'abord: docker compose run --rm build"
    exit 1
fi

echo ">> Installation de $APK sur l'appareil..."

if adb install -r "$APK" 2>/dev/null; then
    echo ">> [OK] Mise à jour réussie (données conservées)"
else
    echo ">> [!] Signature différente détectée — désinstallation puis réinstall..."
    adb uninstall com.cheminee.metronome
    adb install "$APK"
    echo ">> [OK] App réinstallée"
fi
echo ">> Terminé !"