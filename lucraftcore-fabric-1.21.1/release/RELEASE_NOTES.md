# Lucraft: Core & Infinityraft — Fabric 1.21.1 (3.0.1)

**One jar.** Infinityraft is bundled inside Lucraft: Core. The six stones,
every gauntlet ability, the snap, the stone temples and `/generatedStones` all
ship in `LucraftCore-Fabric-1.21.1-3.0.1.jar`. Delete the separate
`Infinity-Fabric` jar from `mods/` when upgrading. Fabric now refuses to start
with both jars instead of letting their duplicate stone classes collide.

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16.14+
- Fabric API 0.116.18+1.21.1
- Java 21

## Fixes in 3.0.1

### The gauntlet no longer switches abilities on by itself

Equipping the gauntlet used to call `setEnabled(true)` on **every** stone
ability. That turned on flight, invisibility, size changing, stop time and fast
forward immediately, without a key press. The old code also enabled those
abilities while a singleplayer world was being joined or the gauntlet GUI was
opening. 3.0.1 now only enables constant passives (`strength`, `resistance`,
`punch`, `immortality`, `healing`, `saturation`); toggles, held and action
abilities start off and wait for the player, exactly as they did in 1.12.

### Remaining server callbacks are guarded

3.0.0 guarded most client drawing and gameplay paths. The join/disconnect
ability sync, immortality damage check, snap effect tick, karma death handler,
extended-inventory open, infinity-generator tick, chunk worldgen queue and
infinity worldgen queue cleanup are now guarded as well. A failure is logged as
`Error in <site> (seen N time(s); the game keeps running)` instead of escaping
into the integrated server thread and returning the player to the main menu.

Static ability and effect caches are also cleared when the integrated or
dedicated server stops, so leaving one world cannot leave a stale player or
ability behind for the next one.

### Saved ability data is loaded again

The `lucraftcore:abilities` attachment was created lazily, after Fabric had
already deserialized the player data. The loader therefore logged
`Unknown attachment type lucraftcore:abilities` and skipped every saved grant.
The attachment (and karma) is now registered during mod initialization, before
any player data is read.

### The 1.12 ability behaviour is completed

| Stone | 3.0.1 behaviour |
| --- | --- |
| Power | `infinity_blast` at max damage, `infinity_strength` at max attack damage, `infinity_punch` as a constant unarmed max-damage bonus, `infinity_resistance` at max armor, `saturation`. Holding the Power Stone by itself grants its default abilities, including the StarTech ones when installed. |
| Mind | `infinity_flight` at the original speed (1.0 flying, 0.2 sprinting) and `changesnap`. |
| Reality | `infinity_size` and `infinity_invisibility`; both start disabled. |
| Soul | `immortality`, `infinity_healing` at the original 0.5 health per tick, `kill` (Power + Space + Reality + Soul) and `snap`. |
| Space | `locate` for `/generatedStones`, plus the HeroesExpansion abilities when that addon is present. |
| Time | `fast_forward` is now a **held** ability (world time advances while the use key is down), and `stop_time` remains a toggle. |

The ability bar now hides constant passives as the 1.12 bar did, and sends a
held flag for abilities such as fast forward. The gauntlet is also a main-hand
weapon again (+5 attack damage, -2.4 attack speed), and right-clicking it in the
off hand no longer opens a screen backed by the main-hand item.

## Contents

Lucraft: Core — materials (22 materials, 79 items, 31 blocks, ore generation,
loot tables, tags, 116 recipes), utilities (hammer, wire cutter, injection,
instruction, capacitors, servo motor, circuits, tri-polymer, extractor, boiler,
construction table, furnace generator, suit stand), karma, size changing, suit
sets, addon packs, engine layer, and 22 superpower abilities with `/ability`.

Infinityraft (bundled) — six infinity stones (fireproof, indestructible as dropped
items, 3D cube renderers), the gauntlet with its 3D model and six-slot GUI
(sneak + use inserts a stone), the snap with its results/effects/sounds, stone
temples and `/generatedStones`.

## Verified

- Dedicated Fabric 1.21.1 server with only this jar + Fabric API: clean start,
  38 abilities registered (22 core + 16 Infinityraft), no errors.
- `/place template infinity:stonetemple` loads the bundled structure without an
  exception.
- A real 1.21.1 client joins a singleplayer world with the full gauntlet, opens
  the gauntlet screen, cycles the ability bar and uses abilities without a
  disconnect or crash report.
- The client HUD and ability bar were exercised; if a render callback does fail,
  it is logged and the game keeps running.

If a disconnect still happens, attach `logs/latest.log` and the matching
`crash-reports/crash-*.txt`. The guarded `Error in ...` line names the failing
part; the stack trace is what a targeted follow-up needs.

Original mod by Lucraft/Nictogen (Lucraft: Core, Apache-2.0) and tilera
(Infinityraft, Apache-2.0).
