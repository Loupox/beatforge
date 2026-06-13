# DEPLOY

## Objectif

Expliquer comment installer l’application sur un téléphone Android depuis le projet.

## Prérequis

1. Activer les options développeur sur le téléphone.
2. Activer le débogage USB.
3. Brancher le téléphone en USB sur l’ordinateur.
4. Vérifier que le téléphone est détecté :

```bash
adb devices
```

## Déploiement recommandé

Le projet contient un script de déploiement simple :

```bash
./scripts/deploy.sh
```

Ce script utilise l’APK debug généré dans `app/build/outputs/apk/debug/app-debug.apk`.

## Déploiement manuel

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

- `-r` permet de réinstaller l’application sans la désinstaller.
- Si l’installation échoue, vérifier que le téléphone est bien listé par `adb devices`.

## Conseils

- Si le téléphone n’apparaît pas, tester :

```bash
adb kill-server
adb start-server
adb devices
```

- Sur certains téléphones, valider la connexion USB sur l’écran du téléphone.
- Si le téléphone est déjà installé avec une version précédente, l’option `-r` conserve les données utilisateur.
