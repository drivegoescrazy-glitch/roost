# Lucraft: Core — Fabric 1.21.1 port (2.4.22)

## Fixed in 2.4.22
- **The gauntlet is the fingerless model again.** The renderer now always uses
  the original's fingerless variant (the 1.12 `LCConfig.fingersOnGauntlet = false`
  build): the five separate finger parts and their tips are hidden, leaving the
  glove, wrist cuff, knuckle plate and the six stone positions.
- **The gauntlet is worn around the hand.** Placement no longer comes from the
  old 1.12 `display` block, which stacked on top of the removed
  `TileEntityItemStackRenderer` transform and left the gauntlet tipped end-on and
  off the hand. The renderer now places it itself, per camera mode: while held,
  the wrist cuff straddles the item origin so the hand goes into the gauntlet,
  with the glove and knuckles projecting out of it, and the
  `(-0.5, -0.5, -0.5)` translate that 1.21 applies to built-in item renderers is
  cancelled. GUI, item frame, ground and head keep the whole gauntlet centred.
  First person 0.62 scale at -15 degrees pitch, third person 0.60 at -28 degrees.
- **The ability bar is the original HUD again.** It is a vertical column of
  22x22 frames from `textures/gui/ability_bar.png` (the original white frame,
  UV 0,36) hanging from the top left at y = 12 with 21 px steps, showing up to
  five entries starting at the selected one — the 1.12 geometry. Each frame
  holds the ability glyph, the green enabled bar and the cooldown shading in the
  original 14 px inner strip, the selected ability's name is shown beside its
  frame ("Fire Resistance" for `fire_resistance`), and the scroll/use key names
  sit at the bottom of the column. The previous letter boxes drawn along the
  bottom of the screen, on top of the hotbar area, are gone.

# Lucraft: Core — Fabric 1.21.1 port (2.4.21)

## Fixed in 2.4.21
- **The infinity gauntlet has its original 3D model again.** The 19-part
  `ModelInfinityGauntlet` (hand, four two-part fingers, thumb and six stone
  blocks) is rebuilt for 1.21 and drawn by a built-in item renderer with the
  1.12 `models/infinity_gauntlet.png` texture, in the original position and
  scale. It closes into the original fist pose while held and opens when it
  lies on the ground, and inserted stones are drawn as the same 3D cubes at
  the original knuckle offsets.
- **The gauntlet screen is the original GUI.** It replaces the chest-style
  screen with a 176x211 panel drawn from `textures/gui/infinity_gauntlet.png`
  and the six stone slots at their 1.12 coordinates (soul 38/28, reality 59/28,
  space 80/28, power 101/28, mind 69/58, time 129/38), the player inventory at
  its original offsets, the held gauntlet's hotbar slot locked, the original
  quick-move rules and the stone equip sound played from the slot.
- **Fixed the disconnect when carrying a gauntlet.** The `infinity_stones`
  data component had no packet codec, and 1.21's `ComponentChanges.encode`
  dereferences the codec without a null check, so syncing a gauntlet threw on
  the server thread and dropped the connection ("Server closed") on every
  inventory update. The component now has a packet codec, and a full gauntlet
  with all six stones encodes and decodes correctly.
- The gauntlet inventory is six slots again instead of a nine-slot chest
  inventory with three locked slots, and the stone slots only accept their own
  stone type.

# Lucraft: Core — Fabric 1.21.1 port (2.4.20)

Community port of Lucraft: Core 2.4.17 to Minecraft 1.21.1 / Fabric.
Original mod by Lucraft/Nictogen (Apache-2.0).

## Requirements
- Minecraft 1.21.1
- Fabric Loader 0.16.14+
- Fabric API 0.116.18+1.21.1
- Java 21

## Included modules
- **Materials** — 22 materials, 79 items, 31 blocks (ores, storage blocks, ingots,
  dusts, plates, nuggets, wirings), ore world generation in all overworld biomes,
  loot tables, harvest-tier tags, creative tab, radiation effect (config, default off),
  116 crafting recipes converted to 1.21 data-driven recipes with `c:` item tags.
- **Utilities items** — hammer, wire cutter, injection, instruction, LV/MV/HV
  capacitors, servo motor, basic/advanced circuit, 16 reinforced tri-polymer colours,
  own creative tab, crafting-grid return behaviour.
- **Karma** — nine karma stats and nine karma classes, persistent per-player data,
  kill-based karma, knockout effect, `/karma` commands.
- **Size changing** — `/sizechange`, vanilla scale plus movement/combat/fall modifiers.
- **Superpowers** — ability framework with 22 abilities (flight, speed, strength,
  health, jump boost, step assist, knockback resistance, fall resistance,
  water breathing, fire resistance, slow fall, invisibility, healing, teleport,
  size change), persistent per-player grants, `/ability` commands.
- **Addon packs** — loads packs from `config/lucraftcore/addonpacks` (folders or
  zip/jar with `addonpack.mcmeta`) and reads instruction recipes from
  `instructionrecipes/*.json`; instruction items store their recipes in a data
  component and list outputs/requirements in their tooltip.
- **Suit sets** — addon packs can define suits in `suitsets/*.json` (original
  `armor_material`, `armor_parts`, `name`, `abilities` keys); armor materials and
  the four armor pieces are registered, shown in the creative tab, and the suit's
  abilities are applied while the full set is worn. Custom suit models/glow layers
  are not ported yet (armor uses vanilla layer rendering).

- **Engine layer** — energy buffers for blocks and items (`lucraftcore:energy`
  component, charge/discharge/transfer helpers), fluid tanks with bucket
  interactions, inventory insertion/extraction helpers, S2C payloads for
  particle spawning and arm swings with client receivers, the original sound
  events (energy blast, infinity stone equip, gauntlet use), a coloured cloud
  particle type, the `lucraftcore:ability_used` advancement criterion, GUI
  screen/button/drawing bases, and `/lucraftcore engine|addonpacks` operator
  commands. `/lucraftcore engine` self-tests the energy, item energy, fluid and
  inventory APIs at runtime.

- **Data-driven superpowers** — addon packs can define `superpowers/*.json`
  (or `powers/*.json`): name, description, colour, XP level thresholds and an
  ability list where each entry can set its unlock level, energy cost, cooldown
  and conditions. `/superpower list|info|set|remove|xp` manages powers; the
  granted abilities are rebuilt when the power or level changes.
- **Ability upgrades** — energy costs paid from carried energy items, cooldowns,
  and conditions (held item, superpower, level, enabled ability, scoreboard tag,
  potion effect, and/or/not). New ability behaviours: energy blast (hitscan beam
  with particle trail and sound), punch (damage, knockback, fire, potion effect),
  command runner (use/grant/revoke/loop), sprint boost, generator and damage
  resistance.

- **Effects and ability bar** — suits and superpowers can declare `effects`
  (glow, particle trail, aura, name change) with the shared condition system;
  suits now also parse `parent`, `description`, `data`, `creative_tab` and
  per-piece `glow` (enchanted glint). The ability bar is synced to the client
  with entries, active state and cooldowns, and can be used with the
  next/previous/use keybinds (defaults `]`, `[`, `V`). Size changing already
  drives vanilla rendering through 1.21's `generic.scale` attribute.

- **Utilities machines** — the five original machine blocks are back with
  horizontal facing, block-entity inventories saved to NBT and crafting recipes:
  the furnace generator burns vanilla fuels into FE (10 FE/t, 100,000 FE buffer)
  and charges energy items, the extractor consumes FE over time using
  data-driven `extractor/*.json` recipes (item in, item out), the boiler converts
  fluids with `boiler/*.json` recipes, the construction table crafts from
  instruction items (right-click to craft, sneak-right-click to list recipes),
  and the suit stand stores suit pieces. Addon packs can add machine recipes via
  `extractor/` and `boiler/` folders; `/lucraftcore machines` self-tests fuels,
  block-entity registration, recipe parsing and the machine energy buffer.
  Machine GUIs open a vanilla chest-style view of each machine's inventory.

- **Infinity module** — the infinity gauntlet (six typed stone slots stored in a
  data component, right-click opens the stone view, sneak-right-click with a
  stone inserts it), the gauntlet cast, and the infinity generator block
  (2,000,000 FE buffer, 100,000 FE per tick from a contained stone, pushing FE
  into adjacent machines). Stone items themselves are addon-pack content: packs
  subclass `ItemInfinityStone`, return a stone type, and can expose gauntlet
  abilities and per-tick hooks. `/lucraftcore infinity` self-tests the gauntlet
  component, slot validation, generator energy and registrations.
- **Extended inventory** — five persistent extra slots (necklace, mantle, wrist,
  two weapon slots) saved per player, opened with the `H` key in a chest-style
  screen; items implementing `IItemExtendedInventory` receive worn/equip/unequip
  hooks. `/lucraftcore extended` self-tests storage and screen slot validation.
- **Automation glue** — machine block entities now implement `Inventory` so
  hoppers can insert/extract, blocks drop their contents when broken, and the
  infinity generator feeds neighbouring energy machines.

- **Asset fixes** — every material ore and storage block now has an item model
  (previously all 31 block items rendered as missing models in inventories), the
  vibranium ore and block use the glow-layer model again with their emissive
  overlay, invalid Forge CTM metadata was removed, orphan wiring models and
  duplicated nested texture folders were cleaned up.

## Not included in this build
- Custom suit armor models, glow overlays and openable armor visuals.
- Custom suit armour models/overlays, skin/flicker/vibrating effects, stone readers and
  dedicated machine/power GUIs (machines use a chest-style inventory view).
- JEI integration for the construction table and machines.
- Dedicated gauntlet, extended inventory and power GUIs (chest-style screens are used).
- Infinity stone items, models and abilities (addon-pack content).
- Gauntlet cast injection into village blacksmith loot.
- Suit stand armour rendering (it stores pieces but does not display them).

Server-side behaviour for all included modules is verified on a dedicated 1.21.1
server; client rendering of the ported items is untested in this environment.
