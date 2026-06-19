# BeatForge — Brief Redesign UI/UX

## Contexte

Application Android de métronome pour usage scène et répétition. Utilisateur : batteur professionnel.  
Nom de l'app : **BeatForge** · Version actuelle : 3.1.0  
Stack actuelle : Android natif (Kotlin/Compose supposé).

---

## Architecture de l'app (inchangée)

3 onglets dans une bottom navigation bar :

| Tab | Icône | Rôle |
|-----|-------|------|
| Sets | `playlist` | Liste des setlists |
| Métronome | `metronome` | Métronome standalone |
| Plus | `dots` | Menu secondaire |

---

## Thèmes

L'app supporte deux thèmes switchables via les Paramètres.

### Thème Dark — "Bronze & Charbon"

| Token | Valeur |
|-------|--------|
| Background | `#181210` |
| Surface | `#231b14` |
| Surface 2 | `#2d2318` |
| Topbar / Tabbar | `#0f0d0a` |
| Border | `#3a2a1a` |
| Text primary | `#f0e6d3` |
| Text muted | `#8a7460` |
| Text dim | `#4a3d2e` |
| Accent (bronze) | `#cd7f32` |
| Accent light | `#e8a654` |
| Accent dark | `#8b5520` |

### Thème Light — "Bois Clair"

| Token | Valeur |
|-------|--------|
| Background | `#f5efe6` |
| Surface | `#ede3d5` |
| Surface 2 | `#e0d4c4` |
| Topbar / Tabbar | `#d9cbb8` |
| Border | `#c4b49e` |
| Text primary | `#2a1f12` |
| Text muted | `#7a6248` |
| Text dim | `#b09878` |
| Accent (bronze foncé) | `#8b4a0a` |
| Accent light | `#c06810` |
| Accent dark | `#a85c12` |

---

## Composants globaux

### Topbar

- Fond : couleur `topbar` du thème, `border-bottom: 0.5px`
- **Côté gauche** : titre de l'écran ou `[← icône] Nom du set` en mode player
- **Côté droit** : toujours présents sur les écrans de jeu (Métronome + Player live) :
  - Icône `volume` — bronzée si son activé, grisée si désactivé
  - Icône `vibrate` — bronzée si vibration activée, grisée si désactivée
  - Ces icônes sont de simples indicateurs d'état (non interactifs dans la topbar). Le toggle se fait dans les Paramètres.

### Bottom navigation bar

- 3 tabs : Sets · Métronome · Plus
- Tab active : accent bronze, tabs inactives : `text dim`
- Fond : couleur `topbar`, `border-top: 0.5px`

---

## Écrans

### 1. Métronome (standalone)

**Topbar** : "BeatForge" + icônes son/vibration

**Corps (de haut en bas) :**

#### Bloc BPM
```
[ − ]   132   [ + ]
       B P M
```
- Les boutons `−` et `+` sont des cercles (32px) en `Surface 2`, bordure fine, texte accent light
- Le BPM est affiché en très grande typographie (≈56sp), gras, couleur `text primary`
- "B P M" en dessous, petite taille, espacement lettres large, couleur accent bronze

#### Signature rythmique
- 3 pills horizontales : `4/4` · `3/4` · `2/4`
- Pill active : fond accent bronze, texte background
- Pills inactives : fond Surface 2, bordure fine, texte muted
- À droite des pills : bouton carré arrondi `+` (Surface 2, bordure fine)
  - Au tap → ouvre un **AlertDialog** existant (déjà codé) : "Signature personnalisée" avec deux champs numériques séparés par `/`, boutons Annuler / OK

#### Visualiseur de beats
- N cercles (selon la signature) disposés en ligne, espacés
- Beat 1 (accent) : fond `accent light` `#e8a654` / `#c06810`, bordure plus claire
- Beats actifs (en cours de lecture) : fond `accent bronze`
- Beats inactifs : fond `Surface`, bordure fine
- En dessous : rangée de 8 petits points (subdots) représentant les subdivisions — les actifs en accent bronze semi-transparent, les inactifs en `dim`

#### Bouton couleur flash
- Bouton compact pleine largeur, fond `Surface 2`, bordure fine, coins arrondis
- Contenu : [swatch circulaire de la couleur sélectionnée] + label "COULEUR FLASH" (petites caps, espacement lettres) + chevron bas
- Au tap → ouvre une **popover/bottom sheet inline** (pas de dialog modal) avec :
  - Label "COULEUR DU FLASH"
  - Grille de 8 swatches circulaires, couleurs saturées scène :
    - Rouge `#ff0040`
    - Orange `#ff6600`
    - Jaune `#ffee00`
    - Vert `#00ff88`
    - Cyan `#00cfff`
    - Violet `#cc00ff`
    - Rose `#ff00cc`
    - Blanc `#ffffff`
  - Swatch sélectionné : outline 2px blanc avec offset
  - La sélection ferme la popover et met à jour le swatch du bouton

#### Bouton TAP
- Pleine largeur, fond `Surface 2`, bordure fine, texte "T A P" en petites caps espacées, couleur muted

#### Slider tempo
- Track 2px, fond `Surface 2`
- Fill couleur accent bronze
- Thumb 11px, couleur `accent light`, bordure 2px couleur background
- Labels min `45` / max `250` en dessous, petite taille, couleur dim

#### Contrôles de lecture
- 3 éléments en ligne : `|◀` · bouton play/pause central · `▶|`
- Bouton central : cercle 42px, fond `Surface 2`, bordure 1.5px accent bronze, icône `accent light`
- Flèches prev/next : icônes `text muted`

> **Note :** le flash visuel (fond de l'écran qui clignote à chaque beat en couleur choisie) est une feature existante — ne pas modifier la logique, uniquement l'UI du picker.

---

### 2. Player live (set en cours)

Activé quand l'utilisateur lance un set depuis la liste des sets.

**Topbar** : `[✕]` bronze + nom du set en muted · icônes son/vibration

**Corps :**
- Grand BPM centré (sans les +/−)
- Nom du morceau en cours (font-weight 500)
- Métadonnées : `[commentaire] · [signature]` en muted
- Visualiseur de beats (même composant que standalone, sans les subdots)
- **Mini-liste des morceaux** (4 lignes visibles, scrollable) :
  - Chaque ligne : `[numéro] [titre] [note] [BPM]`
  - Morceau actif : fond `Surface 2`, bordure accent bronze, numéro en cercle accent bronze, BPM bronze
  - Morceaux suivants : fond `Surface`, bordure fine, tout en muted/dim
  - Navigation : swipe gauche/droite OU boutons prev/next (comportement existant conservé)
- Contrôles prev/pause/next en bas (même composant)

---

### 3. Liste des sets

**Topbar** : "BeatForge" (sans actions à droite)

**Corps :**
- Liste de cards, une par setlist
- Chaque card (fond `Surface`, bordure fine, coins arrondis 8dp) :
  - Gauche : nom du set (font-weight 500) + nombre de morceaux en muted dessous
  - Droite : icône `share` · icône `edit` · bouton cercle play (fond `Surface 2`, bordure fine, icône bronze)
- **Suppression** : swipe gauche sur la card → révèle bouton rouge supprimer (comportement Android natif)
- **FAB** `+` en bas à droite : cercle accent bronze, icône couleur background

---

### 4. Détail d'un set (liste des morceaux)

**Topbar** : `[←]` + nom du set + bouton `[▶]` play à droite

**Corps :**
- Liste de cards morceaux (scrollable)
- Chaque card : nom · BPM · commentaire (note de jeu) · flèches haut/bas pour réordonner · bouton Modifier · icône poubelle
- Bouton Modifier → **AlertDialog existant** (Nom du morceau / BPM / Commentaire)
- FAB `+` en bas à droite pour ajouter un morceau

> Cet écran conserve la structure existante, seul le style visuel change (thème appliqué).

---

### 5. Menu Plus (onglet "Plus")

Écran simple, pas de topbar title particulier.

**Corps :**
Liste de 3 entrées dans un groupe card arrondi :

| Icône | Label | Sous-titre |
|-------|-------|------------|
| `upload` | Importer un set | Depuis un fichier |
| `settings` | Paramètres | Son, vibration, flash… |
| `info-circle` | À propos | Version 3.1.0 |

Chaque entrée : padding 10px 12px, séparateur 0.5px entre les lignes, icône accent bronze, label `text primary`, sous-titre muted.

---

### 6. Paramètres

**Topbar** : `[←]` + "Paramètres"

**Corps :** deux groupes de settings en cards arrondies.

#### Groupe Apparence
- Mode sombre — `toggle` (fond accent bronze si ON, fond dim si OFF)

#### Groupe Métronome
- Son — "Click à chaque beat" — `toggle`
- Vibration — "1er beat uniquement" — `toggle`
- Flash visuel — "Fond clignote au beat" — `toggle`
- Accent 1er beat — "Son + flash plus fort" — `toggle`

Séparateurs 0.5px entre chaque ligne.  
Version en bas de l'écran, texte dim centré.

---

## Comportements UX à conserver (non modifier)

- Le flash visuel change la couleur de fond de l'écran entier à chaque beat
- La vibration ne se déclenche que sur le 1er temps
- L'accent du 1er beat amplifie son + flash
- Navigation swipe gauche/droite entre morceaux dans le player live
- AlertDialog signature personnalisée existant (réutiliser tel quel)
- AlertDialog édition de morceau existant (réutiliser tel quel)
- Import de set depuis fichier externe

---

## Typographie

- Police système Android (`font-family: default`)
- Poids utilisés : `400` (regular) et `500` (medium) uniquement — pas de bold 700
- BPM : `56sp`, weight `700` (exception volontaire pour la lisibilité scène)
- Titres écrans : `14sp`, weight `500`
- Labels cards : `12sp`, weight `500`
- Métadonnées / sous-titres : `10–11sp`, weight `400`
- Micro-labels (B P M, T A P, lettrage) : `9sp`, letter-spacing `3–4sp`

---

## Notes pour l'implémentation

- Tous les radius de cards : `8dp`
- Tous les radius de pills/boutons ronds : `20dp` (pills) ou `50%` (cercles)
- Épaisseur des bordures : `0.5dp` partout, sauf bordure active du morceau en cours (`1dp`)
- Le thème est stocké en préférence persistante et appliqué au démarrage
- Les états des toggles (son, vibration, flash, accent) sont persistés
- La couleur de flash sélectionnée est persistée par morceau ou globalement (à décider selon l'implémentation existante)
