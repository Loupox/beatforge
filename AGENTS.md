# Cheminée — Agent Guide

## Démarrage d'une session

**Lire en premier :** `SESSION_BRIEF.md` — contient l'état du projet, la prochaine tâche, et le contexte nécessaire.

```
SESSION_BRIEF.md → .kilo/plans/design-refresh-v3.md → livrables visés
```

---

## Build & Deploy

```bash
./scripts/post-build.sh          # Build Docker + incrémente versionCode + deploy téléphone
```

Sans incrémenter :
```bash
docker compose run --rm build ./gradlew assembleDebug
```

---

## Tests

```bash
docker compose run --rm shell ./gradlew testDebugUnitTest
```
**72 tests, 1 ignoré** (`moveSet_reordersCorrectly` — Room Flow/Robolectric)

---

## Checkpoint

À chaque fin de session : mettre à jour `SESSION_BRIEF.md` (section "Session en cours") et `DOCS/CHECKPOINT_v2.6.0.md`.

---

## Fichiers clés

| Fichier | Rôle |
|---------|------|
| `MetronomeEngine.kt` | Moteur singleton |
| `PreferencesManager.kt` | Persistance |
| `LivePerformanceScreen.kt` | Écran Live |
| `LiveViewModel.kt` | VM Live |
| `MetronomeScreen.kt` | Écran Standalone |
| `AppNavGraph.kt` | NavHost |

## Stack
- Compose BOM 2024.06.00 (1.6.x)
- Room + Hilt
- Navigation Compose
- StateFlow + LaunchedEffect

## Limitations connues
- Swipe cyclique `HorizontalPager` : non résolu → boutons Prev/Next (cf. `SESSION_BRIEF.md`)