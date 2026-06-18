# Cheminée — Product Backlog

**Dernière mise à jour:** 2026-06-18
**Version actuelle:** 3.0.1 (build 12)
**Repo:** https://github.com/Loupox/beatforge

---

## 🎯 SESSIONS LIVRÉES

| Session | Livrable | Commit |
|---------|----------|--------|
| S1-S3 | Son + Vibration + Color picker | - |
| S4 | v2.6.0 stable | - |
| S5-S9 | Refonte UX, Live, swipe cyclique, CI/CD | - |
| S10 | Bottom Nav + theme toggle + vibration fix | `38291f1` |
| S11 | Accent 1er beat (son + flash) + toggle | `a29dde8` |

---

## 📦 PRODUCT BACKLOG

### 🔒 SÉCURITÉ & FIABILITÉ

| ID | Titre | Estimation | Priorité | Status |
|----|-------|------------|----------|--------|
| TECH-01 | Couverture tests 70% (JaCoCo) | 2 sessions | P1 | - |
| TEST-FLAKY-01 | Test `moveSet_reordersCorrectly` instable (Room Flow) | 0.5 session | P2 | Ignoré |

---

### 🎯 FONCTIONNALITÉS

| ID | Titre | Description | Estimation | Priorité | Status |
|----|-------|-------------|------------|----------|--------|
| FEAT-04 | Signatures rythmiques | 3/4, 5/4, 6/8, 7/8... | 2 sessions | P1 | S12 |
| FEAT-SOUND-01 | Choix du son du métronome | Stick, shaker, click, woodblock, beep... | 1 session | P1 | BACKLOG |
| FEAT-VIBRA-PATTERN | Pattern de vibration personnalisé | Durée + intensité par beat | 1 session | P2 | - |
| FEAT-TAP-TEMPO | Tap tempo | Tap pour détecter le BPM | 0.5 session | P2 | - |
| FEAT-BPM-FINE | Ajustement fin BPM | ±1 BPM au lieu de 5 | 0.5 session | P2 | - |

---

### 🎨 UI/UX

| ID | Titre | Estimation | Priorité | Status |
|----|-------|------------|----------|--------|
| UI-DESIGN-REVIEW | Revue design Acier & Métal | 0.5 session | P2 | - |
| UI-ABOUT-REFRESH | Refresh page À propos | 0.5 session | P3 | - |

---

## 📋 US — FEAT-SOUND-01 : Choix du son du métronome

### US-SOUND-01: Bibliothèque de sons

**En tant que** músico,
**Je veux** choisir le son du métronome parmi plusieurs options,
**Afin de** travailler avec un son qui me convient (stick, shaker, click...).

**Options de son:**

| ID | Nom | Description |
|----|-----|-------------|
| SOUND-01 | Click (défaut) | Bip court classique |
| SOUND-02 | Stick | Claquement de baguettes sur fût |
| SOUND-03 | Shaker | Bruit de maracas |
| SOUND-04 | Woodblock | Bloc de bois |
| SOUND-05 | Rim shot | Coup sur le bord de la caisse claire |
| SOUND-06 | Hi-hat closed | Charley fermé |

**Critères d'acceptation:**
- [ ] Liste déroulante ou picker dans Settings → Métronome
- [ ] Preview du son à la sélection
- [ ] Choix persistant entre les sessions
- [ ] Son différencié uniquement si accent premier beat activé (beat 1 garde sa propre tonalité/accent)
- [ ] Retour au son par défaut = option "Click"

**Estimation:** 1 session | **Story Points:** 2

---

### US-SOUND-02: Son du beat 1 distinct

**En tant que** músico,
**Je veux** que le premier beat sonne différemment des autres (comme un "accent"),
**Afin de** me repérer naturellement dans la mesure.

**Critères d'acceptation:**
- [ ] Beat 1 = son/accent différent, même si choix utilisateur = "stick"
- [ ] Toggle "Accentuer le 1er beat" (défaut: ON) dans Settings
- [ ] Comportement cohérent avec FEAT-SOUND-01

**Estimation:** 0.5 session (intégré à S12/S13) | **Story Points:** 1

---

## 📊 MATRICE IMPACT / COMPLEXITÉ

```
                     Complexité
                        ↑
     Haute              │  FEAT-04 (S12)
                        │
     Moyenne            │  FEAT-SOUND-01   TECH-01
                        │  FEAT-VIBRA-PATTERN
                        │
     Basse              │
                        └─────────────────────>
                             Basse    Haute
                                     Impact
```

---

## 🚀 ROADMAP

### v3.1.0 — "Signatures Rythmiques" (S12)

| Ticket | Description |
|--------|-------------|
| FEAT-04 | 3/4, 5/4, 6/8, 7/8 + patterns d'accent |

**DoD:** Picker de signature rythmique, beat 1 accentué, persistant.

### v3.2.0 — "Son Custom" (S13-S14)

| Ticket | Description |
|--------|-------------|
| FEAT-SOUND-01 | Stick, shaker, click, woodblock... |
| FEAT-VIBRA-PATTERN | Patterns vibration |

### v3.3.0 — "Professional Tools"

| Ticket | Description |
|--------|-------------|
| TECH-01 | Couverture tests 70% |
| FEAT-TAP-TEMPO | Tap tempo |

---

## 🔗 RÉFÉRENCES

- `SESSION_BRIEF.md` — Suivi des sessions de dev