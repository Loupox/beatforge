# DEPLOY

## Objectif

Expliquer comment installer l'application sur un téléphone Android depuis le projet.

## Prérequis

1. Activer les options développeur sur le téléphone.
2. Activer le débogage USB.
3. Brancher le téléphone en USB sur l'ordinateur.
4. Vérifier que le téléphone est détecté :

```bash
adb devices
```

## Déploiement recommandé

Le projet contient deux scripts de déploiement :

```bash
./scripts/build-and-deploy.sh  # Build + incrémente versionCode + deploy (recommandé)
./scripts/deploy.sh            # Deploy seul (APK déjà compilé)
```

**`build-and-deploy.sh`** — pipeline complet :
1. Incrémente `versionCode` automatiquement
2. Build Docker (`assembleDebug`)
3. Deploy sur le téléphone via `adb` si connecté
4. Revert automatique du `versionCode` si le build échoue

**`deploy.sh`** — installe uniquement l'APK déjà compilé dans `app/build/outputs/apk/debug/app-debug.apk`.

## Déploiement manuel

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

- `-r` permet de réinstaller l'application sans la désinstaller.
- Si l'installation échoue, vérifier que le téléphone est bien listé par `adb devices`.

## Conseils

- Si le téléphone n'apparaît pas, tester :

```bash
adb kill-server
adb start-server
adb devices
```

- Sur certains téléphones, valider la connexion USB sur l'écran du téléphone.
- Si le téléphone est déjà installé avec une version précédente, l'option `-r` conserve les données utilisateur.