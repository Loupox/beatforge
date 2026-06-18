# Cheminée — Session Brief

**Repo:** https://github.com/Loupox/beatforge

## Session en cours : S12 — Signatures rythmiques
**Dernière mise à jour :** 2026-06-18
**Version :** 3.0.1
**VersionCode :** 12
**État :** ✅ Build OK, Tests OK (72 tests, 1 ignoré)

---

### PROCHAINE SESSION : S13 — ???

**Livrables S12 :**
- [ ] Support 3/4, 4/4, 6/8, 5/4, 7/8...
- [ ] Pattern d'accent par signature
- [ ] UI picker signature

---

## Contexte projet

### Version actuelle
- **versionName :** 3.0.1
- **versionCode :** 12
- **Dernier commit S10 :** `38291f1` (Bottom Navigation + theme toggle + vibration fix + nav fix)

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
docker compose run --rm shell ./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk  # Build + deploy
```

### Fichiers clés du projet
| Fichier | Rôle |
|---------|------|
| `MetronomeEngine.kt` | Moteur singleton — start/stop/tick/flash/sound/vibration (ToneGenerator + Vibrator centralisés) |
| `PreferencesManager.kt` | Persistance prefs (sound, vibration, flash, BPM, darkTheme) |
| `LivePerformanceScreen.kt` | Écran Live avec Pager, BPM + BeatDots hors pager, wrap-around swipe |
| `LiveViewModel.kt` | VM Live : bind, playFor, toggle |
| `MetronomeScreen.kt` | Écran Standalone, modifier paramétrable pour padding bottom bar |
| `AppNavGraph.kt` | NavHost, `startDestination = Routes.SETS` |
| `BottomNavBar.kt` | Bottom Navigation Bar — Sets, Métronome, Plus |
| `AppTopBarMenu.kt` | Overflow menu (Settings, About, Import) |
| `ui/components/FlashColorPicker.kt` | Sélecteur couleur partagé |
| `ui/components/BeatDots.kt` | Indicateurs de beat animés partagés |

### Architecture Live / Metronome
- `MetronomeEngine` (singleton object) : moteur partagé, UN seul `ToneGenerator` + UN seul `Vibrator` créés par `setContext()`
- Son joué via `playSoundIfEnabled()`, vibration via `playVibrationIfEnabled()` à chaque beat
- Les VMs ne jouent plus le son ni la vibration individuellement

### Scripts CI/CD
| Script | Rôle |
|--------|------|
| `scripts/post-build.sh` | Incrémente versionCode, build Docker, deploy ADB (gestion smart install) |
| `scripts/deploy.sh` | Deploy APK existant sur téléphone (gestion smart install) |

### Docker — Volume keystore debug
Le docker-compose monte `${HOME}/.android:/root/.android` pour partager le keystore debug avec le host. Après `docker compose down`, le build suivant utilise le bon keystore et `adb install -r` préserve les données.

---

## Bugs / Limitations connues

### Bug double son (BUG-001) — FIXED ✅
- Cause : 2 `ToneGenerator` dans les VMs → 2 sons lors de navigation
- Fix : centralisé dans `MetronomeEngine` (S8)

### Swipe cyclique — FIXED ✅
- Utilise `currentPageOffsetFraction` (seuil ±0.5) pour détecter swipe au-delà des bords

### Tests ignorés
- `moveSet_reordersCorrectly` — Room Flow + Robolectric (non-blocking)

### Résolu en v3.0
- [x] `VolumeUp/VolumeOff` → `AutoMirrored` icons ✅ S6
- [x] `SmallTopAppBar` → `TopAppBar` ✅ S6
- [x] BUG-001 double son ✅ S8
- [x] `buildDir` → `layout.buildDirectory` ✅ S9

### Résolu en v3.0.1
- [x] Hamburger menu → Bottom Navigation Bar ✅ S10
- [x] Start destination: `ABOUT` → `SETS` ✅ S10
- [x] Theme toggle Dark/Light dans Paramètres (persistant) ✅ S10
- [x] Vibration: ajoutée dans MetronomeEngine (cassée depuis S8) ✅ S10
- [x] MetronomeScreen: accepte modifier pour padding bottom bar ✅ S10
- [x] Vibration icon: `Vibration` activé / `DoNotDisturb` désactivé ✅ S10
- [x] MetronomeScreen: retiré statusBarsPadding() (espace noir) ✅ S10
- [x] MetronomeScreen: retiré label "BPM" et spacer inutile ✅ S10
- [x] Play button réduit (96dp → 72dp) ✅ S10
- [x] docker-compose: volume `${HOME}/.android` pour keystore debug ✅ S10

### S11 — Accent 1er beat (DONE ✅)
- [x] Beat 1 = son + vibration + flash STRONGER que beats 2-N
- [x] Toggle dans Settings: "Accentuer le 1er beat" (défaut: ON)
- [x] Persistant via PreferencesManager
- [x] Impact: MetronomeEngine, Settings (ajout toggle)
- [x] Flash rouge (index 6) sur beat 1 quand accent activé ✅ `a29dde8`

### S12 — Signatures rythmiques (PLANNED)
- [ ] 4/4, 3/4, 6/8, 5/4, 7/8...
- [ ] Pattern d'accent: beat 1 toujours accentué, reste configurable
- [ ] UI: dropdown/picker signature
- [ ] Persistant

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
| S9 | Fix buildDir deprecated + v3.0 stable | `f61cf99`, `5d3747d` |
| S10 | Bottom Nav + theme toggle + vibration fix + deploy pipeline | `38291f1` |
| S10b | Cleanup: old docs + ignore screenshot/ | `f326848` |
| S11 | Accent first beat (sound/vibration/flash stronger on beat 1) + toggle in Settings | `a29dde8` |