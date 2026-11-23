# Infinityraft — Fabric 1.21.1 port (1.12.1)

## Fixed in 1.12.1
- **The stones' enchant-glint no longer flies off the item.** The 1.12 renderer
  applied the glint drift and the 1/3 scale to the *texture* matrix, so only the
  shine scrolled across the cube. The port had applied them to the model matrix,
  which moved the cube itself. The glint passes now use the item glint layer,
  which owns that animation, so the stone stays put and only the shine moves.
- Requires Lucraft: Core 2.4.21, which restores the gauntlet's 3D model, fixes
  the gauntlet GUI and fixes the disconnect when carrying a gauntlet.

The six stones, their 16 abilities, the snap, the temples, the config and the
stone API are unchanged from 1.12.
