# Cheminée v3.0 — Design Refresh

## État : v3.0 S7 DONE ✅ — Prochaine : S8

| Ticket | Pb | Solution |
|--------|-----|---------|
| US-NAV-01 | La TopAppBar disparaît en mode Live | ✅ Fix: `Modifier.statusBarsPadding()` |
| US-NAV-03 | Swipe ne reboucle pas (s'arrête en bout de liste) | ✅ Fix: `snapshotFlowOfPage` + `scrollToPage` wrap-around |

---

## Direction artistique

**Style :** "Acier & Métal" — sobre, classique, métallique. Pas clinquant.
**Inspiration :** Instruments de percussion (caisse claire, baguettes, cerclages chromés)

### Palette (Light primary, Dark auto-adapté)

| Rôle | Light | Dark |
|------|-------|------|
| Primary | `#3D3D3D` (Charbon) | `#E8E4DF` (Ivoire) |
| Accent | `#C4973A` (Or mat) | `#C4973A` (Or mat) |
| Background | `#F7F4F0` (Blanc chaud) | `#111111` (Noir) |
| Surface | `#EDEAE5` | `#1C1C1C` |

### Typographie
- **Display (BPM)** : `DM Mono` — monospace technique
- **UI / Titres** : `DM Sans` — famille cohérente
- Google Fonts : https://fonts.google.com/specimen/DM+Mono + https://fonts.google.com/specimen/DM+Sans

---

## Plan de sessions

### S5 : Corrections bugs v2.6.1
- Fix US-NAV-01 : WindowInsets gérés, TopAppBar visible en Live
- Fix US-NAV-03 : Swipe wrap-around fonctionnel
- Tests + build

### S6 : Architecture design
- Créer `ui/theme/Color.kt` (nouvelle palette)
- Créer `ui/theme/Typography.kt` (DM Mono + DM Sans)
- Mettre à jour `Theme.kt`
- Ajouter dépendances Google Fonts
- Nouveau logo vectoriel `ic_launcher_foreground.xml` (caisse claire + baguettes)
- Mettre à jour `TopBar.kt` (TopAppBar Material 3, edge-to-edge)

### S7 : Refonte Live + Metronome ✅
- Composants partagés : `FlashColorPicker.kt`, `BeatDots.kt`
- `LivePerformanceScreen` : layout unifié — BPM + BeatDots hors pager
- `MetronomeScreen` : TopAppBar + layout scrollable unifié
- ControlBar cohérente

### S8 : Fix BUG-001 + Finitions
- Fix BUG-001 : centraliser le son dans `MetronomeEngine` (un seul ToneGenerator)
- Test light/dark mode
- Lisibilité scène validée
- Tests unitaires
- Build final v3.0

---

## Checklist livrables par session

### S5 (bugs)
- [x] US-NAV-01 : WindowInsets gérés, TopAppBar visible en Live
- [x] US-NAV-03 : Swipe wrap-around fonctionnel
- [x] Tests passent, build OK

### S6 (design foundation)
- [x] `Color.kt` : nouvelle palette appliquée
- [x] `Type.kt` : DM Mono + DM Sans
- [x] `Theme.kt` : mise à jour
- [x] `TopBar.kt` : TopAppBar Material 3, edge-to-edge
- [x] `ic_launcher_foreground.xml` : nouveau logo (caisse claire + baguettes)
- [x] Tests passent

### S7 (screens)
- [x] `FlashColorPicker.kt` + `BeatDots.kt` créés
- [x] `LivePerformanceScreen` : BPM + BeatDots outside pager, ControlBar cohérente
- [x] `MetronomeScreen` : TopAppBar + layout scrollable unifié
- [x] Tests passent ✅
- [x] Build OK ✅

### S8 (BUG-001 + finitions)
- [x] Fix BUG-001 : MetronomeEngine centralise ToneGenerator
- [x] Fix wrap-around : currentPageOffsetFraction (seuil ±0.5) au lieu de currentPage
- [x] Tests unitaires passent
- [x] Build APK OK

---

## Fix BUG-001 — Détail technique

**Problème :** Les 2 VMs (`LiveViewModel` + `StandaloneMetronomeViewModel`) créent chacun un `ToneGenerator` et écoutent `beatTrigger`. Les 2 collectors peuvent être actifs simultanément → 2 sons.

**Solution :** Centraliser le son dans `MetronomeEngine` (un seul `ToneGenerator`).

**Changements :**
1. `MetronomeEngine.kt` : ajouter `setContext(context)`, créer le `ToneGenerator` en interne, jouer sur `beatTrigger`
2. `LiveViewModel.kt` : supprimer `beatTrigger.collectLatest` + `toneGenerator`, garder vibration via `preferencesManager`
3. `StandaloneMetronomeViewModel.kt` : supprimer `beatTrigger.collectLatest` + `toneGenerator`, garder vibration + tap tempo

**Fichiers impacted :**
- `metronome/MetronomeEngine.kt`
- `ui/live/LiveViewModel.kt`
- `ui/metronome/StandaloneMetronomeViewModel.kt`