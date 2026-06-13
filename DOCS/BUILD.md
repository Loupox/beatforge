# BUILD

## Principe
La compilation de l’application se fait uniquement via Docker. Ne pas utiliser `./gradlew` en local, car l’environnement Android/Java n’est pas garanti disponible.

## Commandes Docker

### 1. Construire l’image de build

```bash
docker compose build build
```

### 2. Exécuter la compilation debug

```bash
docker compose run --rm build
```

### 3. Emplacement de l’APK

L’APK debug se trouve ici :

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Volumes de cache

Le projet utilise deux volumes Docker persistants pour accélérer les builds suivants :

- `gradle-cache`
- `android-cache`

Ces volumes évitent de retélécharger le SDK et les dépendances à chaque construction.

## Shell interactif dans le conteneur

Pour ouvrir un shell directement dans le conteneur :

```bash
docker compose run --rm shell
```

Puis, dans le conteneur :

```bash
./gradlew test
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew clean
```

## Cas d’usage rapide

- Build complet : `docker compose run --rm build`
- Build après modification : `docker compose run --rm build`
- Lancer des tests unitaires depuis le conteneur : `docker compose run --rm shell` puis `./gradlew test`

## Attention

- Ne pas exécuter `./gradlew` en local si l’agent ou l’environnement ne contient pas le SDK Android.
- Toujours utiliser les commandes Docker documentées ici.
