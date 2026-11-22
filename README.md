Roost
====

A Minecraft mod that gives chickens a place to rest.

This is the Fabric port of Roost 3.0.0 for Minecraft 1.21.1. The original 1.12.2 Forge sources
and assets are kept in `legacy-1.12.2/` for reference; the Fabric mod is the project at the
repository root.

## Blocks

| Block | What it does |
| --- | --- |
| Roost | Holds up to 16 chickens of one kind and collects their drops. Sneak-use with an empty hand to take the chicken back out. |
| Breeder | Holds two chickens, consumes two seeds per child, and produces a chicken item. |
| Collector | Pulls items out of roosts in a 9x3x9 area around and above it into a 27-slot inventory. |

## Items

- **Chicken** - a captured chicken. Use it on a roost to store it, or on any other block to release it.
- **Catcher** - captures adult chickens and turns them into chicken items. Has 64 uses.

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16 or newer
- Fabric API
- Java 21

## Building

```sh
./gradlew build
```

The remapped mod jar is written to `build/libs/roost-3.0.0.jar`.

## Configuration

`config/roost.json` is created on first launch:

```json
{
  "roostSpeed": 1.0,
  "breederSpeed": 1.0,
  "disableEggLaying": false
}
```

`disableEggLaying` stops vanilla chickens from laying eggs and swaps the roost's egg drops for
feathers, matching the 1.12.2 config.

## Adding chicken kinds

Roost ships the vanilla chicken and all 152 chicken textures from the original mod. Other mods
can add their own kinds with `RoostChickens.register(ChickenType)`; the type's `texture()` picks
one of the shipped textures for the roost model and the chicken item.

## License

MIT. Original mod by Tim Wood and David Vierra.
