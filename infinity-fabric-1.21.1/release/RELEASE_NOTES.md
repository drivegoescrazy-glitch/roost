# Infinityraft 1.13.0 — Fabric 1.21.1

Infinityraft is a separate mod again. Use
`Infinity-Fabric-1.21.1-1.13.0.jar` with
`LucraftCore-Fabric-1.21.1-3.1.0.jar`; do not use the old bundled 3.0.x Core
jar.

## Requires

- Minecraft 1.21.1
- Fabric Loader 0.16.14+
- Fabric API 0.116.18+1.21.1
- Lucraft: Core **3.1.0 or newer**
- Java 21

## Changes in 1.13.0

- Split the addon back out of Lucraft: Core. The six stones, their abilities,
  the snap, the stone temples, `/generatedStones`, the Infinity creative tab
  and the stone renderers now live in this jar. The gauntlet, cast, generator
  and gauntlet GUI remain in Core, as in the 1.12 original.
- The gauntlet's abilities are granted from the stone set again, and the
  ability icons/names match the 1.12 originals.
- The energy beam is the original layered glow tube rather than a flat
  billboard, and no longer requests the missing emissive shader sampler.
- The Infinity player-data attachment is registered before saved player data is
  read, so snap selections and gauntlet state survive a reconnect.
- The six stone advancements load with the 1.21 item-predicate format.

## Contents

Six infinity stones, 16 stone abilities, the snap and its results, the stone
temples, `/generatedStones`, and the Infinity creative tab. The gauntlet model,
gauntlet GUI and Infinity Generator are supplied by Lucraft: Core.

Original mod by tilera (Apache-2.0), ported to Fabric 1.21.1.
