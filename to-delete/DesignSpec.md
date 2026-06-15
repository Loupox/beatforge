Design Spec — Material Modern (minimal)

But
- Refonte UI Material3 minimal pour améliorer lisibilité, cohérence et réutilisabilité.

Thème
- Palette Light/Dark (primary: #1565C0 / #90CAF9), onPrimary #FFFFFF, background light #FAFAFB, surface #FFFFFF.
- Typographie: Display 40sp, Title 20sp, Headline 16sp, Body 14sp, Label 12sp.
- Spacing: base 8dp (scale 4/8/12/16/24/32).

Composants essentiels
- `TopBar` (titre + import action)
- `PrimaryButton` / `SecondaryButton`
- `Card` (header + content + trailing)
- `ListItem` (leading icon, title, subtitle)
- `FAB` (Add / Start)

Écrans
- Live: large tempo center, Play/Pause large, prev/next, mini-carousel library, import action
- Editor: liste de mesures en `Card`, edit sheet, Add measure FAB, Preview
- Library: search, list sets, Import (local file) with preview collisions

Import JSON
- Source: stockage local (ACTION_OPEN_DOCUMENT)
- Unicité: `title` du morceau seulement
- Merge par défaut: append mesures/presets non présents
- Transaction: utiliser Room transaction; rollback en cas d'erreur

PR plan
- PR1: Theme global (`Theme.kt`)
- PR2: Composants réutilisables (`TopBar`, `Buttons`, `Cards`)
- PR3: Migration écran Live
- PR4: Import JSON + tests

Checklist rapide
- Build debug via Docker OK
- Parser JSON testé
- Import UI: preview collisions + merge
