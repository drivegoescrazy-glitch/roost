Chickens
========

Chickens... lots of chickens.

This is the Minecraft 1.21.1 **Fabric** port of [Chickens 6.1.0](https://www.curseforge.com/minecraft/mc-mods/chickens),
built from the original `chickens-6.1.0.jar` kept in this branch. All of the original models, textures,
GUIs, chicken kinds and mechanics are preserved; only the mod loader and Minecraft APIs are new.

The 1.12.2 Forge sources and assets that used to live at the repository root are kept in
`legacy-1.12.2/` for reference. `chickens-6.1.0.jar` is the jar this port was made from.

## What's included

| Feature | Notes |
| --- | --- |
| **46 chicken kinds** | Every chicken from 6.1.0: 16 dye chickens plus flint, quartz, log, sand, string, glowstone, gunpowder, redstone, glass, iron, coal, gold, snowball, water, lava, clay, leather, nether wart, diamond, blaze, slime, ender, ghast, emerald, magma, prismarine shard/crystal, obsidian, soul sand and the smart chicken |
| **Genetics** | Growth, gain and strength stats (1-10) that are inherited and mutated when breeding |
| **Breeding** | Tiered breeding trees with the original weighted dice roll, plus tier-based lay times |
| **Henhouse** | Six wood variants with the original 166x166 GUI, 9 output slots, hay-bale energy and dirt byproduct. Chickens within five blocks fill nearby henhouses |
| **Chicken Analyzer** | Shows a chicken's tier, stats and next-egg timer; 238 uses |
| **Colored eggs** | 16 dye-colored eggs; each has the original 1-in-8 chance to hatch chicks when thrown |
| **Spawn eggs** | One spawn egg item covering all 46 kinds, tinted with each chicken's colors |
| **Liquid eggs** | Water and lava eggs, one-shot buckets as in the original |
| **Smart chickens** | Right-click a vanilla chicken with an egg to convert it |
| **Natural spawning** | Overworld (normal and snowy) and nether chickens, with configurable rates |
| **Config** | `config/chickens.json` for spawn rate, brood size, nether multiplier, always-show-stats and disabled chickens |
| **Recipes** | Analyzer, six henhouse recipes and all 16 colored egg recipes |

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16 or newer
- Fabric API
- Java 21

## Building

```sh
./gradlew build
```

The remapped mod jar is written to `build/libs/chickens-7.0.0.jar`.

## Configuration

`config/chickens.json` is created on first launch:

```json
{
  "spawn_probability": 10,
  "min_brood_size": 3,
  "max_brood_size": 5,
  "nether_spawn_chance_multiplier": 1.0,
  "always_show_stats": false,
  "disabled_chickens": []
}
```

Disable a kind by adding its id, for example `"chickens:quartzchicken"`.

## Credits

Original mod by SetyCz, Wyvi, GenDeathrow and LordCazsius. MIT licensed. The 1.21.1 Fabric port
reuses the original jar's textures, models, GUI sheets and language files.
