# Compilation

## Build standard (sans incrémenter le versionCode)

```bash
docker compose run --rm build ./gradlew assembleDebug
```

## CI/CD Post-Build (recommandé)

Après chaque modification de code, utiliser le script CI/CD qui :
- Incrémente automatiquement le versionCode
- Compile l'APK via Docker
- Déploie sur téléphone si connecté (sinon indique que l'APK est prêt)

```bash
./scripts/post-build.sh
```

L'agent DOIT afficher le résumé du build avec :
- Numéro de version (VERSION_NAME)
- Numéro de build (VERSION_CODE)
- Statut du déploiement

Les APK sont générées dans `app/build/outputs/apk/debug/`