# Agent: Checkpoint Best Practices

## Méthode obligatoire pour les checkpoints

**TOUJOURS utiliser le tool `write` (ou `edit`) pour les checkpoints**, jamais `bash` avec heredoc.

### Pourquoi
- Les commandes bash avec `cat > file << 'EOF'` peuvent ne pas fonctionner correctement
- Le tool `write` garantit l'écriture correcte du contenu
- Le fichier `.kilo/session-checkpoint.md` doit toujours être à jour

### Template standard

```markdown
# Session Checkpoint

**Date:** [YYYY-MM-DD HH:MM:SS +02:00]
**Working Directory:** [chemin complet]
**Session Resume From:** [session ID si applicable]

## État actuel
- [ ] Tâche en cours
- [ ] Tâches complétées
- [ ] Problèmes identifiés

## Actions en cours
- [ ] Action 1
- [ ] Action 2

## Prochaines étapes
1. [ ] Étape 1
2. [ ] Étape 2

## Modifications fichiers
- `path/vers/fichier.ext`: description du changement

## Commandes de validation
```bash
docker compose run --rm -w /workspace build ./gradlew [commande]
```

## Notes
- Notes additionnelles
```

### Timing
- Créer un checkpoint initial en début de session
- Mettre à jour après chaque étape majeure (achèvement, erreur, changement de direction)
- Créer un checkpoint final avant fin de session