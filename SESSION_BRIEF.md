# Cheminée — Session Brief

## Session en cours : NEXT SESSION
**Dernière mise à jour :** 2026-06-15
**Version :** 3.0.0
**VersionCode :** 10
**État :** ✅ Build OK, Tests OK (72 tests, 1 ignoré)

---

### PROCHAINE SESSION : S9 — Fix buildDir deprecated + Cleanup

**Livrables S9 :**
- [ ] Corriger `buildDir` → `layout.buildDirectory` dans `app/build.gradle.kts` (Jacoco)
- [ ] Tests + build OK

---

## Contexte projet

### Version actuelle
- **versionName :** 3.0.0
- **versionCode :** 10
- **Dernier commit :** `387063c` — v3.0 S8: Fix BUG-001 (double son) + swipe cyclique + composants partagés

### Stack
- Kotlin + Jetpack Compose (BOM 2024.06.00 / Compose 1.6.x)
- Room + Hilt (injection)
- ViewModel + StateFlow
- Navigation Compose
- CI/CD via Docker (`docker compose`)

### Build & Test
```bash
docker compose run --rm shell ./gradlew assembleDebug      # Build
docker compose run --rm shell ./gradlew testDebugUnitTest  # Tests
```

### Fichiers clés du projet
| Fichier | Rôle |
|---------|------|
| `MetronomeEngine.kt` | Moteur singleton — start/stop/tick/flash/sound (ToneGenerator centralisé) |
| `PreferencesManager.kt` | Persistance prefs (sound, vibration, flash, BPM) |
| `LivePerformanceScreen.kt` | Écran Live avec Pager, BPM + BeatDots hors pager, wrap-around swipe |
| `LiveViewModel.kt` | VM Live : bind, playFor, toggle |
| `MetronomeScreen.kt` | Écran Standalone, TopAppBar + layout scrollable |
| `AppNavGraph.kt` | NavHost, `startDestination = Routes.ABOUT` |
| `ui/components/FlashColorPicker.kt` | Sélecteur couleur partagé |
| `ui/components/BeatDots.kt` | Indicateurs de beat animés partagés |

### Architecture Live / Metronome
- `MetronomeEngine` (singleton object) : moteur partagé, UN seul `ToneGenerator` créé par `setContext()`
- Le son est joué dans l'engine via `playSoundIfEnabled()`, plus de double son possible
- Les VMs ne jouent plus le son ni la vibration individuellement

---

## Bugs / Limitations connues

### Bug double son (BUG-001) — FIXED ✅
- Cause : 2 `ToneGenerator` dans les VMs → 2 sons lors de navigation
- Fix : centralisé dans `MetronomeEngine` (S8)

### Swipe cyclique — FIXED ✅
- Utilise `currentPageOffsetFraction` (seuil ±0.5) pour détecter swipe au-delà des bords

### Tests ignorés
- `moveSet_reordersCorrectly` — Room Flow + Robolectric (non-blocking)

### Deprecated à corriger
- [x] `VolumeUp/VolumeOff` → `AutoMirrored` icons ✅ S6
- [x] `SmallTopAppBar` → `TopAppBar` ✅ S6
- [x] BUG-001 double son ✅ S8
- [ ] `buildDir` → `layout.buildDirectory` dans `app/build.gradle.kts` (Jacoco)

---

## Direction artistique v3.0
- **Style :** "Acier & Métal" — sobre, classique, métallique
- **Primary :** `#3D3D3D` (charbon) / `#E8E4DF` (ivoire dark)
- **Accent :** `#C4973A` (or mat)
- **Background :** `#F7F4F0` (blanc chaud) / `#111111` (noir)
- **Typographie :** FontFamily.Monospace (BPM) + FontFamily.SansSerif (UI)

---

## Historique des sessions

| Session | Livrable | Commit |
|---------|----------|--------|
| S1-S3 | Son + Vibration + Color picker + À propos | - |
| S4 | v2.6.0 stable | - |
| S5 | Fix US-NAV-01 + US-NAV-03 (boutons cycliques) | `8c38c9e` |
| S6 | v3.0 design foundation (palette, TopBar, logo) | `734aefb` |
| S7 | Refonte Live + Metronome (layouts unifiés) | `387063c` |
| S8 | Fix BUG-001 (double son) + swipe cyclique + composants partagés | `387063c` |
| **S9** | **Fix buildDir deprecated + v3.0 stable** | |