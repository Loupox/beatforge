# Cheminée — Checkpoint v2.6.1

**Date:** 2026-06-14
**Version:** 2.6.0 (build 7)
**Session:** S5b

---

## ✅ LIVRÉ

### v2.5.0 — "Sound & Vibration"

| Ticket | Description | Status |
|--------|-------------|--------|
| FEAT-01 | Toggle son On/Off | ✅ Livré |
| FEAT-02 | Vibration à chaque beat (30ms, 255 amplitude) | ✅ Livré |

### v2.6.0 — "Stage Ready" 🏁 STABLE

| Ticket | Description | Status |
|--------|-------------|--------|
| US-LIVE-01 | Couleurs flash en mode Live (color picker) | ✅ Livré |
| US-LIVE-02 | Toggle son uniforme | ✅ Livré |
| US-NAV-02 | Page d'arrivée "À propos" par défaut | ✅ Livré |
| US-NAV-01 | Menu haut permanent en Live | ✅ Livré (S5) |
| US-NAV-03 | Swipe cyclique en Live | ✅ Livré (S5) |
| TECH-01 | Test `moveSet_reordersCorrectly` | ⚠️ Doc (Room Flow/Robolectric) |

### Bugs S4 → Corrigés en S5 (v2.6.1)
| Ticket | Pb | Solution |
|--------|-----|----------|
| US-NAV-01 | TopAppBar disparaît en mode Live | `Modifier.statusBarsPadding()` ajouté à la TopAppBar |
| US-NAV-03 | Swipe ne reboucle pas | `LaunchedEffect` sur `PagerState` qui intercepte les pages hors limites et reboucle |

### S5b — Investig / Tentative swipe-cyclique
| Action | Résultat |
|--------|----------|
| Tentative fix swipe-cyclique via `pointerInput` sur Pager | ❌ NOK — `pointerInput` intercepte tous les events, Pager ne scroll plus |
| Tentative détection wrap via `currentPage` transition | ❌ NOK — `HorizontalPager` ne change pas `currentPage` lors du snap-back à un bord |
| Solution conservée | ✅ Boutons Prev/Next (ControlBar) — wrap-cyclique fonctionne via `animateScrollToPage` avec modulo |

### Modifications fichiers v2.6.1
- `LivePerformanceScreen.kt` : `statusBarsPadding()` + wrap-around via `snapshotFlow` + `scrollToPage` (L166-183)
- `LiveViewModel.kt` : `setFlashColorIndex()` (ligne ~179-181)
- `AppNavGraph.kt` : `startDestination` → `Routes.ABOUT`
- `SetsListViewModelTest.kt` : Diagnostic Room Flow documenté

---

## 🚀 NEXT: v2.6.1 stable → v3.0 "Design Refresh"

**Plan détaillé :** `.kilo/plans/design-refresh-v3.md`

### Direction artistique v3.0
- **Style :** "Acier & Métal" — sobre, classique, métallique
- **Primary :** `#3D3D3D` (charbon light) / `#E8E4DF` (ivoire dark)
- **Accent :** `#C4973A` (or mat — rappel cerclages batterie)
- **Background :** `#F7F4F0` (blanc chaud light) / `#111111` (noir dark)
- **Typographie :** DM Mono (BPM display) + DM Sans (UI)
- **Logo :** Caisse claire + baguettes (vectoriel à créer)

---

## 🔧 NOTES TECHNIQUES

### Swipe Cyclique — Limitations connues
- `HorizontalPager` (Compose 1.6.x) ne remonte pas de changement de `currentPage` lors du snap-back aux bords. Les approches testées :
  - `snapshotFlow { pagerState.currentPage }` : ne détecte pas le snap-back
  - `snapshotFlow { pagerState.targetPage }` : ne descend pas en dessous de 0 (clampé)
  - `pointerInput` + `detectHorizontalDragGestures` sur le Pager : intercepte tous les events, Pager ne scroll plus
  - wrap via détection de direction de drag : même problème d'interception
- ** workaround actif** : `LaunchedEffect` sur `currentPage` qui détecte les vraies transitions (last→first, first→last) et fait `scrollToPage`
- **solution fiable** : boutons Prev/Next avec modulo (`animateScrollToPage` avec `(page + 1) % size`)

### Tests
- 72 tests, 1 ignoré (`moveSet_reordersCorrectly` — Room Flow/Robolectric, cause identifiée)
- Lancer: `docker compose run --rm shell ./gradlew testDebugUnitTest`

### Build & Deploy
```bash
./scripts/post-build.sh
```
- Incrémente versionCode automatiquement
- Build Docker + déploiement sur téléphone si connecté

### Fichiers clés
- `MetronomeEngine.kt` : Moteur de métronome (object singleton)
- `PreferencesManager.kt` : Persistance des préférences (sound, vibration, flash)
- `LivePerformanceScreen.kt` : Écran Live
- `MetronomeScreen.kt` : Écran Standalone

---

## ⚠️ POINTS D'ATTENTION

1. **Tests ignorés:** `moveSet_reordersCorrectly` — Room Flow/Robolectric (cause identifiée, non-blocking)
2. **Deprecated warnings:** VolumeUp/VolumeOff icons (utiliser AutoMirrored)
3. **Deprecated warnings:** SmallTopAppBar → TopAppBar dans TopBar.kt
4. **Swipe cyclique:** Non résolu. Boutons Prev/Next font le wrap-cyclique. Le swipe snap-back aux bords mais ne wrap pas. À revoir avec refonte graphique v3.0 ou si migration vers Compose 1.7+ avec Pager API différente.

---

## 📅 PLAN DE SESSIONS

| Session | Objectif | Livrable |
|---------|----------|----------|
| S1 | ✅ FEAT-01 + FEAT-02 | Son + Vibration OK |
| S2 | ✅ US-LIVE-01 + US-NAV-02 | Color picker + À propos default |
| S3 | ✅ US-LIVE-01 + US-NAV-02 | Color picker + À propos default |
| S4 | ✅ v2.6.0 stable (bugs découverts) | Bugs: barre haute + wrap-cyclique |
| S5 | ✅ Fix US-NAV-01 + US-NAV-03 | Bugs corrigés, tests OK, v2.6.1 |
| S5b | ⚠️ Investig swipe-cyclique | Non résolu, workaround boutons actif |
| S6 | Architecture design | Color.kt + Type.kt + Theme + Logo |
| S7 | Refonte écrans | Live + Metronome unifiés |
| S8 | Finitions + v3.0 stable | Light/dark + tests + build |

---

## 🎯 RESSOURCES

- Backlog: `DOCS/BACKLOG.md`
- Release Plan: `DOCS/RELEASE_PLAN.md`
- Build: `DOCS/BUILD.md`
- Scripts: `scripts/`