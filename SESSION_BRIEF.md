# Cheminée — Session Brief

## Session en cours : NEXT SESSION
**Dernière mise à jour :** 2026-06-14
**Version :** 2.6.1 stable
**État :** ✅ Build OK, Tests OK (72 tests, 1 ignoré)

---

### PROCHAINE SESSION : S6 — Architecture Design (v3.0)

**Plan détaillé :** `.kilo/plans/design-refresh-v3.md`

**Livrables S6 :**
- [ ] `ui/theme/Color.kt` : nouvelle palette "Acier & Métal" (Primary `#3D3D3D`, Accent `#C4973A`, BG `#F7F4F0`)
- [ ] `ui/theme/Type.kt` : DM Mono (BPM display) + DM Sans (UI)
- [ ] `Theme.kt` : mise à jour avec nouvelle palette
- [ ] Dépendances Google Fonts : DM Mono + DM Sans
- [ ] `ic_launcher_foreground.xml` : logo caisse claire + baguettes
- [ ] `TopBar.kt` : TopAppBar Material 3, edge-to-edge avec `statusBarsPadding()`
- [ ] Build + Tests passent

---

## Contexte projet

### Version actuelle
- **versionName :** 2.6.0 (inchangé — pas de bump pour fix-only)
- **versionCode :** 7
- **Dernier commit :** `8c38c9e` — v2.6.1: Fix top bar, about screen default, prev/next cyclic nav

### Stack
- Kotlin + Jetpack Compose (BOM 2024.06.00 / Compose 1.6.x)
- Room + Hilt (injection)
- ViewModel + StateFlow
- Navigation Compose
- CI/CD via Docker (`docker compose`)

### Build & Test
```bash
./scripts/post-build.sh          # Build + incrémente versionCode + deploy
docker compose run --rm shell ./gradlew testDebugUnitTest
```

### Fichiers clés du projet
| Fichier | Rôle |
|---------|------|
| `MetronomeEngine.kt` | Moteur singleton — start/stop/tick/flash |
| `PreferencesManager.kt` | Persistance prefs (sound, vibration, flash, BPM) |
| `LivePerformanceScreen.kt` | Écran Live avec Pager |
| `LiveViewModel.kt` | VM Live : bind, playFor, toggle, setFlashColorIndex |
| `MetronomeScreen.kt` | Écran Standalone |
| `AppNavGraph.kt` | NavHost, `startDestination = Routes.ABOUT` |
| `SetsListViewModelTest.kt` | Tests avec diagnostic Room Flow |

### Architecture Live / Metronome
- `MetronomeEngine` (singleton object) : moteur métronome partagé entre Live et Standalone
- `LiveViewModel` : injecte `PreferencesManager`, `SetRepository`, crée `ToneGenerator`
- `LivePerformanceScreen` : `HorizontalPager` sur les songs du set, `ControlBar` avec Prev/Play/Next

---

## Bugs / Limitations connues

### Swipe cyclique (DEFERRED → v3.0)
- `HorizontalPager` (Compose 1.6.x) ne change pas `currentPage` lors du snap-back aux bords
- `snapshotFlow { pagerState.currentPage }` ne détecte rien sur snap-back
- `pointerInput` + `detectHorizontalDragGestures` intercepte TOUS les events → Pager ne scroll plus
- **Workaround actif** : boutons Prev/Next dans `ControlBar` avec modulo → `animateScrollToPage`

### Tests ignorés
- `moveSet_reordersCorrectly` — Room Flow + Robolectric (cause identifiée, non-blocking)

### Deprecated à corriger
- `VolumeUp/VolumeOff` → `AutoMirrored` icons
- `SmallTopAppBar` → `TopAppBar` dans `TopBar.kt`
- `buildDir` dans `app/build.gradle.kts` (lignes 117, 120, 127, 139)

---

## Direction artistique v3.0
- **Style :** "Acier & Métal" — sobre, classique, métallique
- **Primary :** `#3D3D3D` (charbon) / `#E8E4DF` (ivoire dark)
- **Accent :** `#C4973A` (or mat)
- **Background :** `#F7F4F0` (blanc chaud) / `#111111` (noir)
- **Typographie :** DM Mono (BPM) + DM Sans (UI)

---

## Historique des sessions

| Session | Livrable |
|---------|----------|
| S1-S3 | Son + Vibration + Color picker + À propos |
| S4 | v2.6.0 stable (bugs découverts) |
| S5 | Fix US-NAV-01 + US-NAV-03 (boutons cycliques) |
| **S6** | **→ Architecture design v3.0** |
| S7 | Refonte Live + Metronome |
| S8 | Finitions + v3.0 stable |