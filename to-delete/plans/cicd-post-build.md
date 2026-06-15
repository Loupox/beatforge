# Plan: CI/CD Post-Build pour Cheminée

## Objectif

Créer un script CI/CD local simple qui s'exécute automatiquement après chaque build pour :
- Incrémenter le numéro de build
- Mettre à jour les infos de version dans l'app
- Afficher clairement les infos de version/build dans les retours
- Déployer sur téléphone si connecté (avec notification sinon)

## Étapes d'implémentation

### 1. Créer le script `scripts/post-build.sh`

Le script doit :
- Lire et incrémenter le versionCode dans `app/build.gradle.kts`
- Afficher les infos de version (VERSION_NAME, BUILD_NUMBER)
- Builder avec Docker (`docker compose run --rm build ./gradlew clean assembleDebug`)
- Vérifier la génération de l'APK
- Tenter le déploiement ADB si téléphone connecté
- Afficher un résumé complet du build

### 2. Rendre le script exécutable

```bash
chmod +x scripts/post-build.sh
```

### 3. Mettre à jour `AGENTS.md`

Ajouter section CI/CD:
- Documenter l'utilisation de `./scripts/post-build.sh` après chaque build
- Expliquer que le script incrémente automatiquement le versionCode

### 4. Documenter dans le checkpoint-guide

- Après chaque build réussi, les infos de version sont affichées
- Mettre à jour le session-checkpoint.md avec le numéro de build

## Fichiers à créer/modifier

| Fichier | Action |
|---------|--------|
| `scripts/post-build.sh` | Créer |
| `AGENTS.md` | Modifier (ajouter section CI/CD) |
| `.kilo/checkpoint-guide.md` | Modifier (documenter le comportement CI/CD) |

## Résultats attendus

1. **Agent**: Le script affiche clairement les infos de version après chaque build
2. **Utilisateur**: Les infos "À propos" sont toujours à jour avec le bon build
3. **Déploiement auto**: Si téléphone branché, l'app est installée automatiquement
4. **Pas de téléphone**: Message clair que l'APK est prêt
