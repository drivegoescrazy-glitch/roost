# Lucraft: Core 3.1.0 + Infinityraft 1.13.0 — Fabric 1.21.1

This release contains **two separate mod jars**. Infinityraft is no longer
bundled inside Lucraft: Core.

| File | Mod |
| --- | --- |
| `LucraftCore-Fabric-1.21.1-3.1.0.jar` | Lucraft: Core |
| `Infinity-Fabric-1.21.1-1.13.0.jar` | Infinityraft |

## Installation

1. Remove the old bundled `LucraftCore-Fabric-1.21.1-3.0.x.jar` from `mods/`.
2. Put both jars from this release into `mods/`.
3. Start the game with Fabric Loader 0.16.14+ and Fabric API 0.116.18+1.21.1.

Infinityraft requires **Lucraft: Core 3.1.0 or newer**. Core works on its own
without Infinityraft.

## What changed

- **The mods are separate again.** All `anvil.infinity` classes, assets and
  data moved out of Core into `Infinity-Fabric-1.21.1-1.13.0.jar`. Core has no
  bundled Infinity code, and the duplicate-class conflict from installing both
  jars is gone.
- **The gauntlet's stone abilities are granted again.** Power, Mind, Reality,
  Soul, Space and Time abilities are rebuilt from the stones held in the
  gauntlet, including the blast, strength, punch, resistance, flight,
  invisibility, size, immortality, healing, kill, snap, change-snap, locate,
  fast-forward and stop-time actions.
- **Ability icons match the 1.12 originals.** Snap uses the gauntlet item,
  immortality uses the health icon, and the Power Stone blast draws the magenta
  glow under the white core instead of a single red square. The ability bar also
  shows the original names (Energy Blast, Punch Damage, Change Snap, ...).
- **The energy beam looks like the original bolt.** It is drawn as a
  double-sided square glow tube with the original layer count, width growth and
  white core. It uses the regular translucent entity layer, so the missing
  `Sampler2` emissive shader warning is gone.
- **Saved player data loads before abilities are used.** The abilities,
  extended inventory and Infinity player-data attachments are registered during
  mod initialization, before Fabric reads player data. Saved gauntlets, snap
  selections and abilities no longer log `Unknown attachment type` or disappear
  on reconnect.
- **The Infinity advancements are valid 1.21 data again.** The root advancement
  uses the item-predicate array form, and all six stone advancements load.

## Contents

**Lucraft: Core** — materials (22 materials, 79 items, 31 blocks, ore
generation, loot tables, tags, 116 recipes), utilities (hammer, wire cutter,
injection, instruction, capacitors, servo motor, circuits, tri-polymer,
extractor, boiler, construction table, furnace generator, suit stand), karma,
size changing, suit sets, addon packs, engine layer, the Infinity Gauntlet item
and six-slot GUI, and 22 superpower abilities with `/ability`.

**Infinityraft** — the six infinity stones (fireproof, indestructible as
dropped items, 3D cube renderers), the 16 stone abilities, the snap with its
results/effects/sounds, stone temples, `/generatedStones`, and the Infinity
creative tab.

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16.14+
- Fabric API 0.116.18+1.21.1
- Java 21

## Verified

- Both projects build successfully on Gradle 8.12.1 / Java 21.
- The Core jar contains no `anvil/infinity` entries; the Infinityraft jar
  contains all 62.
- A dedicated Fabric 1.21.1 server with both jars registered 38 abilities
  (22 Core + 16 Infinityraft), loaded 1405 advancements without a parsing error,
  accepted a player wearing a full gauntlet, executed the blast and snap
  abilities, and stayed up without a crash report.
- Core also starts on its own. An old test player carrying Infinity stones
  logs the expected missing-item message but does not crash the server.

The client HUD icons and beam rendering were compiled but could not be visually
checked in this headless environment. If anything still looks wrong, attach
`logs/latest.log` and a screenshot.

Original mod by Lucraft/Nictogen (Lucraft: Core, Apache-2.0) and tilera
(Infinityraft, Apache-2.0).
