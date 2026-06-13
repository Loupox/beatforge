# Compilation

- Compiler avec Docker : `docker compose run --rm build ./gradlew assembleDebug`
- Docker a déjà tout l'environnement configuré. Ne pas vérifier/redemarrer les conteneurs.
- Les APK sont générées dans `app/build/outputs/apk/debug/`