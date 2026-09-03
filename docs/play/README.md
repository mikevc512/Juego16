# Google Play store assets

Graphics for the Juego 16 Play Console listing, in English and Spanish. Every
image is rendered from `screens.html`, which reproduces the app's real layout,
spacing and colour palette (`shared/src/commonMain/kotlin/org/juego/game/ui/Theme.kt`)
at Android's `dp` values, so the mockups stay in sync with the actual UI.

## Files

Each locale folder holds the same six assets, sized for their Play Console slot.

| File | Size | Play Console slot |
| --- | --- | --- |
| `feature-graphic.png` | 1024 × 500 | Feature graphic |
| `screenshot-1-home.png` | 1080 × 1920 | Phone screenshot — stats and Continue |
| `screenshot-2-difficulty.png` | 1080 × 1920 | Phone screenshot — difficulty picker |
| `screenshot-3-board.png` | 1080 × 1920 | Phone screenshot — board and number pad |
| `screenshot-4-controls.png` | 1080 × 1920 | Phone screenshot — action row |
| `screenshot-5-solved.png` | 1080 × 1920 | Phone screenshot — solved puzzle |

- `en-US/` — English listing
- `es-ES/` — Spanish listing

Play requires between 2 and 8 phone screenshots, so all five can be uploaded per
locale. Recommended order is the numbering above; the board screens (3 and 4)
explain the game fastest, so consider moving `screenshot-3-board.png` first if
you want the mechanic visible in the search results carousel.

## Translations

Spanish copy is taken verbatim from
`shared/src/commonMain/composeResources/values-es/strings.xml`, so the
screenshots read exactly like the app does in Spanish. Only the feature graphic
has marketing copy with no in-app equivalent:

| English | Spanish |
| --- | --- |
| Place 1–16 so every row and column adds up. | Coloca del 1 al 16 para que cada fila y columna sume. |
| A quiet logic puzzle with instant feedback. | Un puzzle de lógica tranquilo con respuesta al instante. |

## Board shown in the artwork

All screens use one real, solvable puzzle so the sums are consistent:

```
11   6   1   2   → 20
10   5   7   3   → 25
 4  16  12   8   → 40
13   9  14  15   → 51
 ↓   ↓   ↓   ↓
38  36  34  28
```

Green circles mark a completed row or column, coral marks one still in
progress — the same `LineState` colour rules the app uses.

## Regenerating

The page cannot be opened over `file://` (Chrome blocks the local image), so
serve the repository root first:

```bash
python3 -m http.server 8731 --bind 127.0.0.1
```

Then open `http://127.0.0.1:8731/docs/play/screens.html`. Without a URL hash the
page shows every asset stacked for review. Adding a hash (`#s1` … `#s5`, `#fg`)
isolates a single asset at the top-left corner so it can be captured at exactly
the right pixel size, and `?lang=es` switches the copy to Spanish:

```
screens.html#s3            English board screen
screens.html?lang=es#s3    Spanish board screen
screens.html?lang=es#fg    Spanish feature graphic
```

Set the browser viewport to 1080 × 1920 for screenshots or 1024 × 500 for the
feature graphic, with a device pixel ratio of 1, then capture the viewport.

Spanish strings live in the `ES` dictionary at the bottom of `screens.html`,
keyed by the English text. Adding a locale means adding one more dictionary.

## Note on fidelity

These are rendered mockups, not device captures. They match the shipped layout,
but two small liberties were taken on `screenshot-5-solved.png`: the dialog
scrim is much lighter than Material's default, and the confetti is drawn above
it, so the solved board stays readable. Replace these folders with real device
captures if you would rather the listing use them.
