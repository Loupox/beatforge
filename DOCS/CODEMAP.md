# CODEMAP

## Fichiers et dossiers essentiels

### Racine du projet
- `README.md` : guide d'entrée et résumé du projet.
- `SESSION_BRIEF.md` : état du projet, session en cours, prochaine tâche.
- `AGENTS.md` : guide agent à jour (remote GitHub, stack, fichiers clés).
- `docker-compose.yml` : configuration des services Docker pour build et shell.
- `Dockerfile` : image Docker de build Android.

### Documentation interne
- `DOCS/ARCHITECTURE.md` : architecture du projet.
- `DOCS/BUILD.md` : instructions de build Docker.
- `DOCS/DEPLOY.md` : instructions de déploiement sur téléphone.
- `DOCS/AGENT_GUIDE.md` : mode d'emploi pour les agents.
- `DOCS/CODEMAP.md` : repérage des fichiers et dossiers.

## Build et déploiement
- `docker-compose.yml` : définit les services `build` et `shell`.
- `Dockerfile` : installe Java 17, Android SDK 34, Build Tools 34.
- `scripts/deploy.sh` : installe `app/build/outputs/apk/debug/app-debug.apk` sur le téléphone.
- `app/build/outputs/apk/debug/app-debug.apk` : emplacement de l’APK debug généré.

## Code Android principal

### Point d’entrée
- `app/src/main/java/com/cheminee/metronome/MainActivity.kt`
  - Point d’entrée de l’application Android.
  - Déclenche la navigation Compose et la configuration globale.

### Navigation et UI globales
- `app/src/main/java/com/cheminee/metronome/ui/AppNavGraph.kt`
  - Définit le graphe de navigation des écrans.
- `app/src/main/java/com/cheminee/metronome/ui/AppTopBarMenu.kt`
  - Menu supérieur de l’application.
- `app/src/main/java/com/cheminee/metronome/ui/AppViewModelFactory.kt`
  - Usine de création de ViewModels.

### Thème et design
- `app/src/main/java/com/cheminee/metronome/ui/theme/Theme.kt`
- `app/src/main/java/com/cheminee/metronome/ui/theme/Spacing.kt`
- `app/src/main/java/com/cheminee/metronome/ui/theme/Typography.kt`

### Composants réutilisables
- `app/src/main/java/com/cheminee/metronome/ui/components/Buttons.kt`
- `app/src/main/java/com/cheminee/metronome/ui/components/Cards.kt`
- `app/src/main/java/com/cheminee/metronome/ui/components/TopBar.kt`
- `app/src/main/java/com/cheminee/metronome/ui/common/NameDialog.kt`

## Écrans principaux

### Sets
- `app/src/main/java/com/cheminee/metronome/ui/sets/SetsListScreen.kt`
- `app/src/main/java/com/cheminee/metronome/ui/sets/SetsListViewModel.kt`

### Live performance
- `app/src/main/java/com/cheminee/metronome/ui/live/LivePerformanceScreen.kt`
- `app/src/main/java/com/cheminee/metronome/ui/live/LiveViewModel.kt`

### Éditeur de set
- `app/src/main/java/com/cheminee/metronome/ui/editor/SetEditorScreen.kt`
- `app/src/main/java/com/cheminee/metronome/ui/editor/SetEditorViewModel.kt`

### Métronome standalone
- `app/src/main/java/com/cheminee/metronome/ui/metronome/MetronomeScreen.kt`
- `app/src/main/java/com/cheminee/metronome/ui/metronome/StandaloneMetronomeViewModel.kt`

### Paramètres et autres écrans
- `app/src/main/java/com/cheminee/metronome/ui/settings/SettingsScreen.kt`
- `app/src/main/java/com/cheminee/metronome/ui/settings/SettingsViewModel.kt`

## Couche données
- `app/src/main/java/com/cheminee/metronome/data/AppDatabase.kt`
- `app/src/main/java/com/cheminee/metronome/data/PreferencesManager.kt`
- `app/src/main/java/com/cheminee/metronome/data/SetList.kt`
- `app/src/main/java/com/cheminee/metronome/data/SetListDao.kt`
- `app/src/main/java/com/cheminee/metronome/data/Song.kt`
- `app/src/main/java/com/cheminee/metronome/data/SongDao.kt`
- `app/src/main/java/com/cheminee/metronome/data/exporter/` : export JSON
- `app/src/main/java/com/cheminee/metronome/data/importer/` : import JSON

## Repository
- `app/src/main/java/com/cheminee/metronome/repository/SetRepository.kt`
  - Point d’accès principal aux données.
  - Gère la logique métier entre UI et Room.

## Moteur tempo
- `app/src/main/java/com/cheminee/metronome/metronome/MetronomeEngine.kt`
  - Logique de tempo et timing sans dérive.

## Fichiers de build
- `build.gradle.kts` : configuration Gradle racine.
- `settings.gradle.kts` : définition des modules.
- `app/build.gradle.kts` : configuration Gradle du module Android.
- `gradle.properties` : propriétés globales Gradle.
- `gradle/wrapper/gradle-wrapper.properties` : version du wrapper Gradle.

## À savoir pour les agents
- Utiliser `DOCS/ARCHITECTURE.md` pour comprendre la structure globale.
- Utiliser `DOCS/BUILD.md` pour lancer un build Docker.
- Utiliser `DOCS/DEPLOY.md` pour l’installation sur appareil.
- Utiliser `DOCS/AGENT_GUIDE.md` pour la procédure standard.
- Chercher les écrans dans `ui/`, la persistence dans `data/`, la logique métier dans `repository/`, et le tempo dans `metronome/`.
