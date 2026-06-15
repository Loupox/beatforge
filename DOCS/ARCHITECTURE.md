# ARCHITECTURE

## Objectif
Fournir une vue structurée du projet Android "Cheminée" pour faciliter la compréhension du code et les interventions.

## Vue d'ensemble
Application Android Kotlin + Jetpack Compose pour préparer des setlists de morceaux et piloter un métronome visuel en live.

## Packages principaux
- `com.cheminee.metronome`
  - `MainActivity.kt` : point d'entrée de l'application.
  - `MetronomeEngine.kt` : singleton — moteur métronome (flash, son, beatTrigger).
- `com.cheminee.metronome.ui`
  - `theme/` : thème Material3, couleurs Acier & Métal, typographie.
  - `sets/` : affichage et gestion des sets.
  - `editor/` : édition d'un set et des morceaux.
  - `live/` : écran live plein écran, flash BPM, pager cyclique.
  - `metronome/` : écran métronome standalone.
  - `settings/` : paramètres.
  - `about/` : à propos.
  - `components/` : composants partagés (BeatDots, FlashColorPicker).
- `com.cheminee.metronome.data`
  - Entités Room, DAO, conversion JSON, import/export.
- `com.cheminee.metronome.repository`
  - `SetRepository.kt` : abstraction sur les sets.
- `com.cheminee.metronome.repository`
  - `SetRepository` : couche d’accès aux données et règles métier.
- `com.cheminee.metronome.metronome`
  - Moteur de tempo, logique de timing sans dérive.

## Responsabilités
- UI : `ui/`
- Données persistantes : `data/` + `repository/`
- Logique du métronome et timing : `metronome/`
- Point d’entrée et orchestration : `MainActivity.kt`

## Flux de données
1. L’utilisateur interagit avec un écran Compose dans `ui/`.
2. Les actions de l’UI appellent le `ViewModel`/la logique locale.
3. Les modifications persistantes passent par `repository/`.
4. `repository/` utilise Room dans `data/` pour sauvegarder/charger.
5. Le mode live utilise `metronome/` pour produire les impulsions de tempo.

## Navigation
- L’appli est divisée en écrans principaux : home, sets, editor, live.
- Les transitions UI se font à l’intérieur du module `ui/`.
- Pour un changement de navigation, commencer par `ui/home` puis `ui/live`.

## Points d’entrée clés
- `app/src/main/java/com/cheminee/metronome/MainActivity.kt`
- `app/src/main/java/com/cheminee/metronome/ui/home/`
- `app/src/main/java/com/cheminee/metronome/ui/sets/`
- `app/src/main/java/com/cheminee/metronome/ui/editor/`
- `app/src/main/java/com/cheminee/metronome/ui/live/`
- `app/src/main/java/com/cheminee/metronome/data/`
- `app/src/main/java/com/cheminee/metronome/repository/`
- `app/src/main/java/com/cheminee/metronome/metronome/`

## Commandes de repérage rapide
- Rechercher le `ViewModel` principal dans `ui/`
- Rechercher la base Room dans `data/`
- Rechercher le moteur de tempo dans `metronome/`
- Rechercher l’import/export JSON dans `data/`

## Règles à garder en tête
- Ne pas compiler sans Docker.
- Pour toute modification fonctionnelle, vérifier l’impact sur `repository/` et `data/`.
- Les écrans live et pagination sont dans `ui/live/`.
