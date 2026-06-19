# Cheminée — Agent Guide

**Repo:** https://github.com/Loupox/beatforge

## Démarrage d'une session

**Lire en premier :** `SESSION_BRIEF.md` — contient l'état du projet, la prochaine tâche, et le contexte nécessaire.

```
SESSION_BRIEF.md → .kilo/plans/design-refresh-v3.md → livrables visés
```

---

## Build & Test

```bash
./scripts/build-and-deploy.sh    # Build + incrémente versionCode + deploy (recommandé)
./scripts/deploy.sh              # Deploy seul (APK déjà compilé)
```

**Workflow détaillé :**

```bash
# Tests seulement
docker compose run --rm shell ./gradlew testDebugUnitTest  # 72 tests

# Build APK seulement
docker compose run --rm shell ./gradlew assembleDebug
```

**APK généré dans :** `app/build/outputs/apk/debug/app-debug.apk`

---

## Version actuelle
- **versionName:** 3.1.0
- **versionCode:** 13

---

## Checkpoint en fin de session

1. Commit les changements
2. Mettre à jour `SESSION_BRIEF.md`
3. Push sur `origin/main`

---

## Fichiers clés

| Fichier | Rôle |
|---------|------|
| `MetronomeEngine.kt` | Moteur singleton — flash, son, beatTrigger (UN ToneGenerator) |
| `PreferencesManager.kt` | Persistance prefs (sound, vibration, flash, BPM) |
| `LivePerformanceScreen.kt` | Écran Live avec Pager, wrap-around swipe cyclique |
| `LiveViewModel.kt` | VM Live : bind, playFor, toggle |
| `MetronomeScreen.kt` | Écran Standalone, TopAppBar + layout scrollable |
| `ui/components/FlashColorPicker.kt` | Sélecteur couleur partagé |
| `ui/components/BeatDots.kt` | Indicateurs de beat animés partagés |
| `AppNavGraph.kt` | NavHost |
| `AppViewModelFactory.kt` | Factory VMs |

## Stack
- Compose BOM 2024.06.00 (1.6.x)
- Room + Hilt
- Navigation Compose
- StateFlow + LaunchedEffect