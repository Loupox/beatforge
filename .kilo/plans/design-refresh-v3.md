# Cheminée v3.0 — Design Refresh

## État : v2.6.1 stable (build 4) — S5 DONE ✅

| Ticket | Pb | Solution |
|--------|-----|----------|
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
- Fix US-NAV-01 : TopAppBar visible en mode Live (WindowInsets/EdgeToEdge)
- Fix US-NAV-03 : Swipe cyclique wrap-around
- Tests + build

### S6 : Architecture design
- Créer `ui/theme/Color.kt` (nouvelle palette)
- Créer `ui/theme/Type.kt` (DM Mono + DM Sans)
- Mettre à jour `Theme.kt`
- Ajouter dépendances Google Fonts
- Nouveau logo vectoriel `ic_launcher_foreground.xml` (caisse claire + baguettes)
- Mettre à jour `TopBar.kt` (TopAppBar + edge-to-edge)

### S7 : Refonte Live + Metronome
- Uniformiser les layouts des 2 écrans
- Nouveau pattern : BPM → Color picker → Beat dots → Play → Son/Vibration
- ControlBar unifiée

### S8 : Finitions + Tests
- Test light/dark mode
- Test lisibilité sur téléphone réel
- Animation de transition
- Tests unitaires
- Build final v3.0

---

## Checklist livrables par session

### S5 (bugs)
- [x] US-NAV-01 : WindowInsets gérés, TopAppBar visible en Live
- [x] US-NAV-03 : Swipe wrap-around fonctionnel
- [x] Tests passent, build OK

### S6 (design foundation)
- [ ] `Color.kt` : nouvelle palette appliquée
- [ ] `Type.kt` : DM Mono + DM Sans
- [ ] `Theme.kt` : mise à jour
- [ ] `TopBar.kt` : TopAppBar Material 3, edge-to-edge
- [ ] `ic_launcher_foreground.xml` : nouveau logo (caisse claire + baguettes)
- [ ] Tests passent

### S7 (screens)
- [ ] `LivePerformanceScreen` : layout unifié
- [ ] `MetronomeScreen` : layout unifié
- [ ] ControlBar / Color picker / Boutons cohérents
- [ ] Tests passent

### S8 (finitions)
- [ ] Light/Dark mode vérifié
- [ ] Lisibilité scène validée
- [ ] Animation transitions
- [ ] Build v3.0 stable