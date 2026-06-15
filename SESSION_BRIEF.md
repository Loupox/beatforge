# Cheminée — Session Brief

## Session en cours : NEXT SESSION
**Dernière mise à jour :** 2026-06-15
**Version :** 3.0.0-dev (design refresh S7 done)
**État :** ✅ Build OK, Tests OK (72 tests, 1 ignoré)

---

### PROCHAINE SESSION : S8 — Fix BUG-001 + Finitions v3.0

**Plan détaillé :** `.kilo/plans/design-refresh-v3.md`

**Livrables S8 :**
- [ ] Fix BUG-001 : double son Live après Métronome (centraliser son dans MetronomeEngine)
- [ ] Tests passent
- [ ] Build APK v3.0 stable

---

## Contexte projet

### Version actuelle
- **versionName :** 3.0.0-dev
- **versionCode :** 9
- **Dernier commit :** `8c38c9e` — v3.0 design refresh (S7): layouts unifiés Live + Metronome

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
| `MetronomeEngine.kt` | Moteur singleton — start/stop/tick/flash/sound |
| `PreferencesManager.kt` | Persistance prefs (sound, vibration, flash, BPM) |
| `LivePerformanceScreen.kt` | Écran Live avec Pager, BPM + BeatDots hors pager |
| `LiveViewModel.kt` | VM Live : bind, playFor, toggle, vibration |
| `MetronomeScreen.kt` | Écran Standalone, TopAppBar + layout scrollable |
| `AppNavGraph.kt` | NavHost, `startDestination = Routes.ABOUT` |
| `ui/components/FlashColorPicker.kt` | Sélecteur couleur partagé |
| `ui/components/BeatDots.kt` | Indicateurs de beat animés partagés |

### Architecture Live / Metronome
- `MetronomeEngine` (singleton object) : moteur métronome partagé entre Live et Standalone
- Les VMs écoutent `beatTrigger` et jouent le son (BUG-001 : les 2 VMs peuvent être actifs simultanément)
- Les VMs jouent aussi la vibration individuellement

---

## Bugs / Limitations connues

### Bug double son Live après Métronome (BUG-001) — EN COURS
- **Description :** Quand on lance d'abord le métronome puis on passe en mode Live, il y a 2 sons très rapprochés à chaque beat
- **Cause :** Les 2 VMs (`LiveViewModel` + `StandaloneMetronomeViewModel`) créent chacun un `ToneGenerator` et écoutent `beatTrigger`. Lors de la navigation, les 2 collectors peuvent être actifs temporairement → 2 sons.
- **Fix prévu :** Centraliser le son dans `MetronomeEngine` (un seul `ToneGenerator`). Les VMs ne jouent plus le son — l'engine le fait.

### Swipe cyclique (FIXED in S8)
- Utilise `currentPageOffsetFraction` pour détecter le swipe au-delà des bords (seuil ±0.5)
- Wrap vers dernière/première page via `animateScrollToPage`

### Tests ignorés
- `moveSet_reordersCorrectly` — Room Flow + Robolectric (non-blocking)

### Deprecated à corriger
- [x] `VolumeUp/VolumeOff` → `AutoMirrored` icons ✅ S6
- [x] `SmallTopAppBar` → `TopAppBar` ✅ S6
- [ ] `buildDir` dans `app/build.gradle.kts` (Jacoco)

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
| S7 | Refonte Live + Metronome (layouts unifiés, composants partagés) | à commiter |
| **S8** | **Fix BUG-001 (double son) + Finitions v3.0 stable** | |