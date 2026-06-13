# AGENT_GUIDE

## Objectif
Donner aux agents une méthode claire pour reprendre le code du projet sans perdre de temps.

## Ordre de lecture recommandé
1. `README.md`
2. `DOCS/ARCHITECTURE.md`
3. `DOCS/BUILD.md` (build Docker)
4. `DOCS/DEPLOY.md` (installation sur téléphone)

## Règles de base
- Utiliser `README.md` comme point d’entrée.
- Lire `DOCS/ARCHITECTURE.md` avant de modifier la structure ou la navigation.
- Lire `DOCS/BUILD.md` avant toute compilation.
- Lire `DOCS/DEPLOY.md` avant de donner une procédure d’installation.

## Où chercher pour chaque type de tâche

- UI / écrans : `app/src/main/java/com/cheminee/metronome/ui/`
- Données persistantes : `app/src/main/java/com/cheminee/metronome/data/`
- Accès aux données / règles : `app/src/main/java/com/cheminee/metronome/repository/`
- Mécanique du métronome : `app/src/main/java/com/cheminee/metronome/metronome/`
- Point d’entrée : `app/src/main/java/com/cheminee/metronome/MainActivity.kt`

## Points importants à ne pas oublier
- Le build se fait via Docker, pas en local.
- L’APK debug se trouve dans `app/build/outputs/apk/debug/app-debug.apk`.
- Le déploiement utilise `./scripts/deploy.sh` ou `adb install -r`.
- Les écrans live sont dans `ui/live/`.
- L’import/export JSON est géré dans `data/`.

## Comment limiter les tokens
- Ne pas envoyer tout le fichier quand une section suffit.
- Préférer des extraits ciblés plutôt qu’un dump complet.
- Quand tu dois modifier une fonctionnalité, citer le module et le chemin exact.

## Exemple de workflow agent
1. Lire la tâche.
2. Vérifier si besoin de build/deploy.
3. Ouvrir `DOCS/ARCHITECTURE.md` pour la structure.
4. Ouvrir uniquement le dossier concerné.
5. Appliquer la modification et tester avec Docker si nécessaire.
