# Cheminée — métronome visuel pour batteur en live

> **Note pour les agents** : Pour compiler ce projet, utiliser **obligatoirement** Docker :
> ```bash
> docker compose run --rm build ./gradlew assembleDebug
> ```
> Ne pas tenter de compiler en local avec `./gradlew` directement (Java/Android SDK non disponibles sur l'agent).
>
> Pour les informations de structure et de build, consulter également :
> - `DOCS/ARCHITECTURE.md`
> - `DOCS/BUILD.md`
> - `DOCS/DEPLOY.md`
> - `DOCS/AGENT_GUIDE.md`
> - `DOCS/CODEMAP.md`

Application Android (Kotlin + Jetpack Compose) permettant à un batteur de :
- Préparer des **setlists nommées** avec des morceaux et leur BPM.
- Lancer un **métronome visuel plein écran** qui flashe au tempo.
- **Naviguer entre les morceaux** par swipe gauche/droite pendant la prestation.

## Stack
- Kotlin 1.9 + Jetpack Compose
- Room (SQLite) pour la persistance
- minSdk 26 / targetSdk 34
- Gradle 8.7

## Build via Docker (sans Android Studio)

L'APK debug se construit dans un conteneur isolé qui télécharge le SDK Android.

### Premier build

```powershell
# Construit l'image (≈ 5-10 min la 1re fois — télécharge JDK, SDK Android, Gradle)
docker compose build build

# Lance le build de l'APK debug
docker compose run --rm build
```

L'APK généré se trouve dans :

```
app/build/outputs/apk/debug/app-debug.apk
```

### Installer sur un téléphone

```powershell
# Activer les options développeur + débogage USB sur le téléphone
# Brancher en USB puis :
./scripts/deploy.sh
```

Ou manuellement (le flag `-r` réinstalle sans désinstaller) :
```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Re-builder rapidement

Les caches Gradle et SDK Android sont persistés dans des volumes Docker
(`gradle-cache`, `android-cache`), donc les builds suivants sont plus rapides
(quelques dizaines de secondes).

### Shell interactif dans le conteneur

```powershell
docker compose run --rm shell
```

Puis dans le conteneur :

```bash
./gradlew test            # tests unitaires
./gradlew assembleDebug   # APK debug
./gradlew assembleRelease # APK release (non signé)
./gradlew clean           # nettoie
```

## Structure

```
app/src/main/java/com/cheminee/metronome/
├── MainActivity.kt
├── data/           # Entités Room, DAOs, AppDatabase, import/export JSON
├── repository/     # SetRepository
├── metronome/      # MetronomeEngine (timing sans dérive)
└── ui/
    ├── theme/
    ├── home/       # Écran d'accueil / menu principal
    ├── sets/       # Écran liste des sets
    ├── editor/     # Éditeur de set (morceaux + BPM)
    ├── live/       # Mode live (swipe + flash plein écran)
    └── components/ # Composants réutilisables (Cards, Buttons, TopBar)
```

## Fonctionnalités (v2.3)

- [x] **Menu d'accueil** — Navigation principale vers Sets, Métronome, Paramètres
- [x] Créer / renommer / supprimer des sets
- [x] Ajouter / éditer / supprimer / réordonner des morceaux (nom + BPM saisis manuellement)
- [x] Mode live plein écran avec flash visuel synchronisé au BPM
- [x] Navigation entre morceaux par swipe (HorizontalPager)
- [x] **Rebouclage automatique** — Le pager reboucle sur le premier morceau à la fin
- [x] Le métronome redémarre automatiquement au changement de morceau
- [x] **Autoplay** — Lancer automatiquement le métronome en mode live
- [x] Verrouillage en portrait + écran maintenu allumé en mode live
- [x] Persistance Room (les sets survivent à la fermeture de l'app)
- [x] **Import JSON** d'un set avec preview des doublons
- [x] **Export JSON** des sets (partage de fichiers .json)
- [x] Theme Material3 (Light/Dark) avec palette moderne
- [x] CI GitHub: assembleDebug + unit tests
- [x] **Métronome standalone** — Écran libre avec slider BPM (30-300), tap tempo et entrée manuelle du BPM
- [x] **Popup BPM** — Tape sur le BPM central pour saisir manuellement avec le clavier numérique

## À venir

- [ ] Persister les sets même après désinstallation de l'application

## Checklist avant release

**À faire à chaque mise à jour :**

- [ ] Incrémenter `versionCode` dans `app/build.gradle.kts`
- [ ] Incrémenter `versionName` dans `app/build.gradle.kts`
- [ ] Mettre à jour le numéro de version dans la section "Fonctionnalités" du README
- [ ] Ajouter les nouvelles fonctionnalités dans le README
- [ ] Supprimer les fonctionnalités acomplies de la liste "À venir"
- [ ] Lancer les tests : `./gradlew test`
- [ ] Builder l'APK : `./gradlew assembleDebug`
- [ ] Vérifier que l'APK s'installe et fonctionne sur le téléphone

## Exemple de modèle JSON pour l’import d’un set




```json
{
  "setName": "Set 1",
  "songs": [
    {
      "title": "Billie Jean (Michael Jackson)",
      "bpm": 117,
      "comments": ""
    },
    {
      "title": "Give Me the Night (George Benson)",
      "bpm": 110,
      "comments": ""
    },
    {
      "title": "Isn't She Lovely (Stevie Wonder)",
      "bpm": 119,
      "comments": ""
    },
    {
      "title": "It's my life (No Doubt)",
      "bpm": 126,
      "comments": ""
    },
    {
      "title": "Lonely boy (Black Peas)",
      "bpm": 165,
      "comments": ""
    },
    {
      "title": "Modern love (Bowie)",
      "bpm": 183,
      "comments": ""
    },
    {
      "title": "Off the Wall (Michael Jackson)",
      "bpm": 119,
      "comments": ""
    },
    {
      "title": "Superchérie (M)",
      "bpm": 124,
      "comments": ""
    },
    {
      "title": "Feeling good (Nina Simone)",
      "bpm": null,
      "comments": ""
    },
    {
      "title": "Up Side Down (Diana Ross)",
      "bpm": 108,
      "comments": ""
    },
    {
      "title": "Superstitous (Stevie Wonder)",
      "bpm": 101,
      "comments": ""
    }
  ]
}
```
```

