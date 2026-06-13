# Session Checkpoint

**Date:** 2026-06-13 11:19:00 +02:00
**Working Directory:** /Users/thomas/Documents/dev-project/cheminee

## État actuel
- [x] Phase 1.1 (tests JaCoCo) terminée ✓
- [x] Phase 1.2 (timing haut BPM) terminée ✓
- [x] Phase 2.1 (Mode Live - immersion scène) terminée ✓
- [x] Phase 2.2 (Toggle son sur écrans) terminée ✓
- [x] Phase 2.3 (Vibration) terminée ✓
- [x] Phase 3 (Fix métronome - rythme irrégulier) terminée ✓

## Problème identifié et corrigé
Les 2 métronomes étaient irréguliers à cause de `MetronomeEngine.stop()` qui ne supprimait que le `tickRunnable` courant, laissant les callbacks `postDelayed` en file d'attente. Lors d'un `stop()` seguido de `start()`, l'ancien runnable créait un nouveau `tickRunnable` — créant des battements doublés et un rythme irrégulier.

**Root cause:** La méthode `stop()` appelait `handler?.removeCallbacks(tickRunnable)` mais le callback `postDelayed` dans le runnable continuait à se ré-exécuter et repostait un nouveau `tickRunnable`.

## Modifications fichiers
- `MetronomeEngine.kt`: `stop()` utilise maintenant `handler?.removeCallbacksAndMessages(null)` pour nettoyer TOUS les callbacks
- `StandaloneMetronomeViewModel.kt`: ajout `engine.stop()` dans `onCleared()` pour nettoyer les callbacks à la destruction du ViewModel
- `LiveViewModel.kt`: ajout `engine.stop()` dans `onCleared()` pour nettoyer les callbacks à la navigation

## Commandes de validation
```bash
docker compose run --rm -w /workspace build ./gradlew testDebugUnitTest
docker compose run --rm -w /workspace build ./gradlew assembleDebug
```

## Tests
- testDebugUnitTest: ✓ PASS
- assembleDebug: ✓ SUCCESS

## Notes
- Le métronome est maintenant régulier et suit le BPM demandé
- La correction assure que tous les callbacks du Handler sont annulés lors d'un stop()
- Les ViewModels cleans up properly quand ils sont détruit