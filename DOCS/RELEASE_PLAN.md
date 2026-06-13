# Plan Release — Cheminée v3.0

## Objectif
Structurer le backlog en phases réalistes, avec critères de validation et dépendances.

---

## PHASE 1 — Fondations (qualité + Fiabilité)

### 1.1 Couverture de tests (target 70% global)

**Objectif :** Poser une base CI qui mesure et enforce la couverture.

- [ ] Configurer JaCoCo dans `app/build.gradle.kts`
- [ ] Configurer un seuil minimum dans Gradle (failIfBelowMissed = 70%)
- [ ] Écrire tests unitaires sur `MetronomeEngine` (core)
- [ ] Écrire tests unitaires sur `SetRepository`
- [ ] Écrire tests sur les convertisseurs JSON (`data/exporter/`, `data/importer/`)
- [ ] Vérifier que CI GitHub publie le rapport de coverage

**Definition of Done :** `./gradlew test` passe, `jacocoTestReport` > 70% global.

---

### 1.2 Correction du timing à haut BPM

**Objectif :** Rythme régulier jusqu'à 300 BPM.

- [ ] Analyser `MetronomeEngine` — identifier la cause (Thread.sleep vs Handler vs Coroutines)
- [ ] Remplacer le mécanisme de timing par un `CountDownTimer` ou coroutine avec `delay` compensé
- [ ] Tester à 230, 260, 300 BPM sur appareil réel
- [ ] Vérifier que la correction ne casse pas la vibration ni le son

**Definition of Done :** Test manuel à 300 BPM = battements parfaitement réguliers sur 30 secondes.

---

## PHASE 2 — Mode Live + Vibration

### 2.1 Visibilité scène (Live Screen)

**Objectif :** Lisible en conditions concert (lumière, distance).

- [ ] Auditer la taille des éléments (`LivePerformanceScreen.kt`)
- [ ] Augmenter la fonte du BPM à 120sp minimum
- [ ] Agrandir les boutons Play/Pause, Prev/Next (min 64dp touch target, icônes 48sp)
- [ ] Forcer le contraste fort (fond noir, texte blanc)
- [ ] Vérifier la lisibilité sur appareil réel (test en conditions)

**Definition of Done :** Lisible à 3 mètres sur un téléphone classique.

---

### 2.2 Toggle son sur les écrans métronome

**Objectif :** Permettre d'activer/désactiver le son rapidement.

- [ ] Ajouter un `IconToggle` (haut-parleur) sur `LivePerformanceScreen.kt`
- [ ] Ajouter le même toggle sur `MetronomeScreen.kt` (métronome standalone)
- [ ] Stocker la préférence dans `PreferencesManager`
- [ ] Persister l'état du son entre les écrans

**Definition of Done :** Toggle audible/on-off fonctionne, icône mise à jour.

---

### 2.3 Vibration pour utilisation en poche

**Objectif :** Vibrer en rythme quand le son n'est pas utilisé.

- [ ] Ajouter la permission `VIBRATE` dans `AndroidManifest.xml`
- [ ] Créer `VibrationEngine.kt` dans `metronome/`
- [ ] Intégrer la vibration dans `MetronomeEngine` (si activé)
- [ ] Ajouter un toggle Vibration dans `SettingsScreen.kt` et `LivePerformanceScreen.kt`
- [ ] Tester sur appareil réel (différents modèles)

**Definition of Done :** Téléphone dans la poche = vibration perceptible au bon rythme.

---

## PHASE 3 — Signatures Rythmiques

### 3.1 Bip différent au 1er beat (Phase 1 uniquement)

**Objectif :** Différencier le premier temps dans 4/4 vs 3/4, etc.

- [ ] Ajouter une enum `TimeSignature` (2/4, 3/4, 4/4, 5/4, 6/8, 7/8)
- [ ] Stocker la signature par `Song` dans Room
- [ ] Modifier `MetronomeEngine` pour jouer un son différent sur le beat 1
- [ ] Ajouter les assets sonores pour l'accent (un son plus fort/différent)
- [ ] Ne pas toucher à l'UI de sélection (on garde 4/4 pour l'instant)

**Definition of Done :** En 4/4 on entend "TICK-tick-tick-tick", en 3/4 on entend "TICK-tick-tick".

---

## PHASE 4 — Refonte UI/UX

### 4.1 Revoir le menu principal

**Objectif :** Accessible depuis tous les écrans, design moderne fonctionnel.

- [ ] Auditer `AppTopBarMenu.kt` et `home/`
- [ ] Proposer un pattern : BottomNavigation ou rail latéral
- [ ] Rendre accessible depuis Live, Editor, Settings
- [ ] Conserver la navigation avec 4 items : Home, Sets, Métronome, Paramètres

**Definition of Done :** Menu accessible en 1 tap depuis n'importe quel écran.

---

### 4.2 Refonte design complète (Moderne fonctionnel)

**Objectif :** Cohérence, lisibilité, modernité.

- [ ] Créer `DOCS/DESIGN_GUIDE.md` (palette, typography, spacing)
- [ ] Rafraîchir le thème dans `ui/theme/` (Palette 2024, spacing 8dp base)
- [ ] Uniformiser les composants (`Buttons.kt`, `Cards.kt`, `TopBar.kt`)
- [ ] Revoir `SetsListScreen`, `SetEditorScreen`, `LivePerformanceScreen`
- [ ] Tester Light/Dark mode
- [ ] Vérifier l'accessibilité (contraste, touch targets)

**Definition of Done :** Design cohérent sur tous les écrans, lisible, moderne.

---

## PHASE 5 — Release

### Checklist release

- [ ] Incrémenter `versionCode` / `versionName` dans `app/build.gradle.kts`
- [ ] Mettre à jour `README.md` avec nouvelles fonctionnalités
- [ ] `./gradlew clean test` — tous les tests passent
- [ ] Coverage >= 70% sur JaCoCo
- [ ] Build APK : `docker compose run --rm build`
- [ ] Test sur appareil réel (live + standalone + vibration)
- [ ] Déployer via `./scripts/deploy.sh`

---

## Résumé des Phases

| Phase | Tickets | Focus |
|-------|---------|-------|
| 1 | 1.1, 1.2 | Qualité + Fiabilité |
| 2 | 2.1, 2.2, 2.3 | Live + Vibration |
| 3 | 3.1 | Signatures Rythmiques (bip diff) |
| 4 | 4.1, 4.2 | Refonte UI/UX |
| 5 | Release | Mise en production |

**Estimation :** 4 à 6 sessions de dev selon la complexité de la refonte design.

---

## Critères de validation transversaux

- Tous les tests passent (`./gradlew test`)
- Coverage >= 70% (JaCoCo)
- Build APK réussi via Docker
- Installation et test sur appareil réel
- Aucune régression sur les fonctionnalités existantes (sets, live, import/export)