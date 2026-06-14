#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
BUILD_FILE="$PROJECT_DIR/app/build.gradle.kts"

CURRENT_BUILD=$(grep 'versionCode = ' "$BUILD_FILE" | grep -o '[0-9]*')
NEW_BUILD=$((CURRENT_BUILD + 1))
CURRENT_VERSION=$(grep 'versionName = ' "$BUILD_FILE" | sed -E 's/.*versionName = "([^"]*)".*/\1/')

echo ""
echo "============================================================"
echo "               CI/CD POST-BUILD CHEMINEE"
echo "============================================================"
echo "  Version:  $CURRENT_VERSION"
echo "  Build:    $CURRENT_BUILD → $NEW_BUILD"
echo "============================================================"
echo ""

if ! grep -q "versionCode = $CURRENT_BUILD" "$BUILD_FILE"; then
    echo "ERREUR: Impossible de trouver versionCode = $CURRENT_BUILD dans $BUILD_FILE"
    exit 1
fi

sed -i '' "s/versionCode = $CURRENT_BUILD/versionCode = $NEW_BUILD/" "$BUILD_FILE"

echo "  [OK] versionCode mis à jour ($CURRENT_BUILD → $NEW_BUILD)"
echo ""

echo ">>> Build Docker en cours..."
cd "$PROJECT_DIR"
if ! docker compose run --rm build ./gradlew clean assembleDebug; then
    echo ""
    echo "============================================================"
    echo "  ERREUR: Le build a échoué!"
    echo "  Revert du versionCode..."
    sed -i '' "s/versionCode = $NEW_BUILD/versionCode = $CURRENT_BUILD/" "$BUILD_FILE"
    echo "============================================================"
    exit 1
fi
echo ""

APK="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"
APK_NEW="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK" ]; then
    echo "============================================================"
    echo "  ERREUR: APK non généré"
    echo "  Revert du versionCode..."
    sed -i '' "s/versionCode = $NEW_BUILD/versionCode = $CURRENT_BUILD/" "$BUILD_FILE"
    echo "============================================================"
    exit 1
fi

APK_TIME=$(stat -f "%Sm" -t "%Y-%m-%d %H:%M:%S" "$APK" 2>/dev/null || stat -c "%y" "$APK" 2>/dev/null | cut -d'.' -f1)
APK_SIZE=$(ls -lh "$APK" | awk '{print $5}')
echo ""
echo "  [OK] APK généré"
echo "       Fichier: $APK"
echo "       Taille:  $APK_SIZE"
echo "       Date:    $APK_TIME"
echo ""

echo ">>> Test de déploiement..."
if adb devices 2>/dev/null | grep -E "	device$" > /dev/null; then
    echo "  [OK] Téléphone détecté"
    echo "  >>> Installation en cours..."
    if adb install -r "$APK"; then
        echo "  [OK] Déploiement réussi!"
        DEPLOY_STATUS="Déployé sur téléphone"
    else
        echo "  [!] Échec du déploiement"
        DEPLOY_STATUS="APK prêt (erreur ADB)"
    fi
else
    echo "  [!] Aucun téléphone connecté"
    echo "      L'APK est prêt. Lancez './scripts/deploy.sh' quand le téléphone sera connecté."
    DEPLOY_STATUS="APK prêt (téléphone non connecté)"
fi

echo ""
echo "============================================================"
echo "                    RÉSUMÉ DU BUILD"
echo "============================================================"
echo "  App:      Cheminée"
echo "  Version:  $CURRENT_VERSION"
echo "  Build:    $NEW_BUILD"
echo "  APK:      $APK"
echo "  Status:   $DEPLOY_STATUS"
echo "============================================================"
echo ""
echo "Pour vérifier la version dans l'app:"
echo "  Écran 'À propos' → Version $CURRENT_VERSION (build $NEW_BUILD)"
echo ""