# Lucraft: Core & Infinityraft — Fabric 1.21.1 (3.0.0)

**One jar.** Infinityraft is now bundled inside Lucraft: Core. The six stones,
every gauntlet ability, the snap, the stone temples and `/generatedStones` all
ship in `LucraftCore-Fabric-1.21.1-3.0.0.jar`. Delete the separate
`Infinity-Fabric` jar from `mods/` when upgrading; keeping it would register the
stones twice.

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16.14+
- Fabric API 0.116.18+1.21.1
- Java 21

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

### Every gauntlet ability of the 1.12 mod, with the original values

| Stone | Abilities |
| --- | --- |
| Power | `infinity_blast` (max-damage energy blast), `infinity_strength`, `infinity_punch`, `infinity_resistance`, `saturation` |
| Mind | `infinity_flight`, `changesnap` (cycles the snap result) |
| Reality | `infinity_size` (configurable), `infinity_invisibility` |
| Soul | `immortality` (health boost + lethal-damage immunity), `infinity_healing`, `kill` (needs Power + Space + Reality + Soul), `snap` |
| Space | `locate` (stones found by `/generatedStones`) |
| Time | `fast_forward`, `stop_time` |

The 1.12 compatibility hooks for HeroesExpansion, SpeedsterHeroes and StarTech are
kept and only activate when those addons are installed.

## Crash fix

The intermittent "Server closed"/"Connection lost" drop to a black main menu with
glowing buttons is the client render thread dying on an exception thrown inside a
mod render or tick callback. Every custom drawing and gameplay step now runs
through `LucraftCore.guard`, which catches **Throwable** (not just Exception, so
mapping/loader `Error`s such as `NoSuchMethodError` are covered), logs it once as
`Error in <site> (seen N time(s); the game keeps running)` and returns with the
matrix stack balanced.

Guarded: gauntlet model and icon, stone cubes, energy bolt, ability bar HUD plus
its input and sync, gauntlet screen, opening and saving the gauntlet, the
crafting-grid mixin, ability/effect/suit ticks, extended inventory, worldgen, and
the beam/particle/swing packet handlers.

If `Error in ...` ever shows up in `logs/latest.log`, that line names the failing
part, the game keeps running, and the log line plus stack trace is what a targeted
fix needs.

## Verified

- A dedicated Fabric 1.21.1 server running only this jar + Fabric API: clean start,
  `engine`, `machines`, `infinity` and `extended` self tests pass, 38 abilities
  registered (22 core + 16 Infinityraft), zero errors.
- A real 1.21.1 client, in single player and connected to a dedicated server:
  joined, took the gauntlet, inserted stones, opened the gauntlet GUI, used every
  stone ability (blast, snap, kill, change snap, stop time, fast forward, flight,
  size, invisibility, locate), flew and switched camera modes, with no exception on
  either side.

Original mod by Lucraft/Nictogen (Lucraft: Core, Apache-2.0) and tilera
(Infinityraft, Apache-2.0).
