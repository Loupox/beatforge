# Cheminée — Product Backlog & Roadmap

**Dernière mise à jour:** 2026-06-14  
**Version actuelle:** 2.5.0 (build 0)  
**Objectif:** Sessions de dev courtes, bugs minimum, itérations fréquentes

---

## 📋 ÉTAT ACTUEL

### ✅ Livré en v2.5.0
- FEAT-01: Toggle son On/Off (Live + Standalone)
- FEAT-02: Vibration à chaque beat, courte et intense

### ✅ Livré en v2.4.2
- Fix timing métronome (tickId cancellation)
- Fix double-beep
- CI/CD post-build opérationnel

### 🔴 Problèmes ouverts
| ID | Problème | Priorité |
|----|----------|----------|
| BUG-01 | ?? | - |
| TEST-FLAKY-01 | Test `moveSet_reordersCorrectly` instable (Room Flow) — ignoré, à fixer dans TECH-01 | P2 |

---

## 🎯 FEATURES & USER STORIES

### FEAT-LIVE-01 — Parité Live/Standalone ⚡ P0

**Objectif:** Uniformiser l'expérience entre le mode Live et le mode Standalone.

---

#### US-LIVE-01: Choix des couleurs de flash en mode Live

**En tant que** músico,  
**Je veux** pouvoir choisir la couleur du flash en mode Live,  
**Afin de** personnalisé mon expérience scène comme dans le mode Standalone.

**Critères d'acceptation:**
- [ ] Le picker de couleurs existe sur l'écran Live
- [ ] Les couleurs disponibles sont les mêmes qu'en Standalone
- [ ] Le choix est persistant entre les sessions
- [ ] UI responsive sur tous les écrans Live

**Estimation:** 0.5 session | **Story Points:** 2

---

#### US-LIVE-02: Toggle son uniforme entre Live et Standalone

**En tant que** músico,  
**Je veux** que le contrôle du son soit identique en mode Live et Standalone,  
**Afin de** passer d'un mode à l'autre sans réapprendre l'interface.

**Critères d'acceptation:**
- [ ] Même icône HP utilisée en Live et Standalone
- [ ] Même position du toggle dans l'UI
- [ ] État du son synchronisé entre les modes
- [ ] Persistance de la préférence au niveau app

**Estimation:** 0.5 session | **Story Points:** 1

---

### FEAT-NAV-01 — Navigation Live & UX ⚡ P0

**Objectif:** Améliorer la navigation et l'expérience utilisateur sur mobile.

---

#### US-NAV-01: Menu haut permanent en mode Live

**En tant que** músico sur scène,  
**Je veux** toujours voir le menu supérieur en mode Live,  
**Afin de** naviguer rapidement sans chercher le menu.

**Critères d'acceptation:**
- [ ] Menu haut visible sur tous les écrans Live
- [ ] Menu accessible sans quitter la vue principale
- [ ] Pas d'interférence avec le contenu principal
- [ ] Test sur LivePerformanceScreen, SetPlayerScreen

**Estimation:** 0.5 session | **Story Points:** 1

---

#### US-NAV-02: Page d'arrivée "À propos"

**En tant que** nouvel utilisateur,  
**Je veux** arriver sur la page "À propos" au lancement de l'app,  
**Afin de** découvrir l'application et ses fonctionnalités avant d'explorer les sets.

**Critères d'acceptation:**
- [ ] "À propos" est la destination par défaut au lancement
- [ ] La navigation reste accessible depuis "À propos"
- [ ] Les sets restent accessibles via le menu
- [ ] Deep linking vers Sets fonctionne toujours

**Estimation:** 0.5 session | **Story Points:** 1

---

#### US-NAV-03: Swipe cyclique en mode Live

**En tant que** músico,  
**Je veux** que la navigation par swipe reboucle dans le mode Live,  
**Afin de** naviguer dans ma liste de chansons de façon fluide et continue.

**Critères d'acceptation:**
- [ ] Swipe droite en fin de liste → retour à la 1ère chanson
- [ ] Swipe gauche en début de liste → aller à la dernière chanson
- [ ] Animation de transition fluide
- [ ] Indicateur visuel de position (pagination ou indicateurs)

**Estimation:** 0.5 session | **Story Points:** 2

---

## 📦 PRODUCT BACKLOG (complet)

### 🔒 SÉCURITÉ & FIABILITÉ

| ID | Titre | Description | Estimation | Priorité |
|----|-------|-------------|------------|----------|
| TECH-01 | Couverture tests 70% | JaCoCo + tests MetronomeEngine, SetRepository, JSON | 2 sessions | P0 |
| TECH-02 | Tests non-régression | Automatiser tests core avant chaque build | 1 session | P1 |

### 🎯 FONCTIONNALITÉS

| ID | Titre | Description | Estimation | Priorité | Status |
|----|-------|-------------|------------|----------|--------|
| FEAT-01 | Toggle son On/Off | Icône HP sur Live/Métronome, persistance | 0.5 session | P0 | ✅ Done |
| FEAT-02 | Vibration | Vibre au rythme quand son OFF, à chaque beat | 1 session | P0 | ✅ Done |
| FEAT-03 | Bip 1er beat | Accent temps 1 (TICK vs tick) | 1 session | P1 | - |
| FEAT-04 | Signatures rythmiques | 3/4, 5/4, 6/8... | 2 sessions | P2 | - |
| FEAT-05 | Visibilité scène | Taille, contraste, lisibilité 3m | 1 session | P1 | - |
| FEAT-LIVE-01 | Parité Live/Standalone | US-LIVE-01 + US-LIVE-02 | 1 session | P0 | - |
| FEAT-NAV-01 | Navigation Live & UX | US-NAV-01 + US-NAV-02 + US-NAV-03 | 1.5 session | P0 | - |

### 🎨 UI/UX

| ID | Titre | Description | Estimation | Priorité |
|----|-------|-------------|------------|----------|
| UI-06 | Refonte Menu | BottomNav accessible | 1 session | P1 |
| UI-07 | Refonte design | Design guide, palette moderne | 2 sessions | P2 |
| UI-08 | Light/Dark mode | Cohérence thèmes | 0.5 session | P2 |

---

## 🚀 PROPOSITION DE RELEASES

### ✅ v2.5.0 — "Sound & Vibration" (2-3 sessions) — LIVRÉ

| Ticket | Description |
|--------|-------------|
| FEAT-01 | Toggle son On/Off |
| FEAT-02 | Vibration (chaque beat, intense) |

**DoD:** Son désactivable, vibration perceptible sur chaque beat, préférence persistée

**Livré le:** 2026-06-14

---

### v2.6.0 — "Stage Ready" (2-3 sessions)

| Ticket | Description |
|--------|-------------|
| FEAT-LIVE-01 | Parité Live/Standalone (US-LIVE-01, US-LIVE-02) |
| FEAT-NAV-01 | Navigation Live & UX (US-NAV-01, US-NAV-02, US-NAV-03) |

**DoD:** 
- Couleurs flash disponibles en mode Live
- Toggle son uniforme sur tous les écrans
- Menu haut permanent en mode Live
- Page "À propos" comme accueil
- Swipe cyclique fonctionnel

---

### v3.0.0 — "Professional" (3-4 sessions)

| Ticket | Description |
|--------|-------------|
| TECH-01 | Couverture tests 70% |
| FEAT-03 | Bip 1er beat |
| FEAT-04 | Signatures rythmiques |
| FEAT-05 | Visibilité scène |
| UI-06 | Refonte Menu |
| UI-07 | Refonte design globale |

**DoD:** JaCoCo ≥ 70%, accents beat 1, mode scène optimal, design moderne, navigation unifiée

---

## 📊 MATRICE IMPACT/COMPLEXITÉ

```
                 Complexité
                    ↑
     Haute         │  FEAT-04    FEAT-NAV-01
                   │  FEAT-03    TECH-01
                   │
     Moyenne       │  FEAT-02    FEAT-LIVE-01
                   │  FEAT-05    FEAT-01
                   │
     Basse         │  
                   └─────────────────────→
                        Basse    Haute
                                Impact
```

---

## 🔗 RÉFÉRENCES

- `DOCS/RELEASE_PLAN.md` — Plan release détaillé
- `DOCS/CICD_POST_BUILD.md` — Procédure CI/CD
- `DesignSpec.md` — Specs design
