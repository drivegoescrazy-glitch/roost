"""Converts the original 1.12.2 `infinity:stonetemple` structure NBT into the
1.21.1 structure-template format used by data/infinity/structure/stonetemple.nbt.

Run from the project root:
    python3 tools/convert_structure.py <input.nbt> <output.nbt>

The original file is a 1.12 `Template` saved from a structure block:
  * palette entries are old block ids that were renamed in 1.13+
  * block states are numeric palette indices (same idea as modern templates)
  * the chest marker was a `minecraft:structure_block` in DATA mode whose
    metadata was "chest"; it is removed and the chest itself (directly below
    the marker) is kept, so the generator only has to look for chests.
"""
import sys

from nbt import IntList
import nbt


def rewrite_state(entry):
    """Maps one old palette entry to (modern entry, keep?) ."""
    name = entry["Name"]
    props = dict(entry.get("Properties", {}))
    modern_name = name
    keep = True

    if name == "minecraft:monster_egg":
        variant = props.pop("variant", "stone")
        modern_name = "minecraft:infested_stone_bricks" if variant == "stone_brick" \
            else "minecraft:infested_chiseled_stone_bricks"
    elif name == "minecraft:stained_glass":
        colour = props.pop("color", "white")
        modern_name = "minecraft:%s_stained_glass" % ("light_gray" if colour == "silver" else colour)
    elif name.endswith("_wall"):
        # 1.12 had a `variant` and four boolean connections; modern uses
        # separate block ids and per-side WallShape values.
        if props.pop("variant", "cobblestone") == "mossy":
            modern_name = "minecraft:mossy_cobblestone_wall"
        elif name == "minecraft:cobblestone_wall":
            modern_name = "minecraft:cobblestone_wall"
        wall = {}
        for side in ("north", "east", "south", "west"):
            connected = str(props.pop(side, "false")).lower() == "true"
            wall[side + "_shape"] = "low" if connected else "none"
        wall["up"] = str(props.pop("up", "true")).lower()
        wall["waterlogged"] = "false"
        props = wall
    elif name == "minecraft:structure_block":
        # DATA marker ("chest"); the real chest sits below it.
        keep = False
    elif name == "minecraft:air":
        keep = False

    if not keep:
        return None
    out = {"Name": modern_name}
    if props:
        out["Properties"] = {k: str(v).lower() if isinstance(v, bool) else str(v)
                             for k, v in sorted(props.items())}
    return out


def convert(src, dst):
    _, root = nbt.load(src)
    assert "palette" in root and "blocks" in root

    palette = []
    index_map = {}
    for old_index, entry in enumerate(root["palette"]):
        new_entry = rewrite_state(entry)
        if new_entry is None:
            index_map[old_index] = None
            continue
        key = (new_entry["Name"], tuple(sorted(new_entry.get("Properties", {}).items())))
        if key not in {k: i for i, k in enumerate([(p["Name"], tuple(sorted(p.get("Properties", {}).items()))) for p in palette])}:
            palette.append(new_entry)
        index_map[old_index] = next(i for i, p in enumerate(palette)
                                    if p["Name"] == key[0]
                                    and tuple(sorted(p.get("Properties", {}).items())) == key[1])

    blocks = []
    for block in root["blocks"]:
        new_index = index_map[block["state"]]
        if new_index is None:
            continue
        out = {"pos": IntList(block["pos"]), "state": new_index}
        if "nbt" in block:
            tag = dict(block["nbt"])
            tag.pop("Lock", None)  # 1.12 container lock, empty in the original
            out["nbt"] = tag
        blocks.append(out)

    size = root["size"]
    converted = {
        "DataVersion": 3955,  # 1.21.1
        "size": IntList(size),
        "palette": palette,
        "blocks": blocks,
        "entities": [],
    }
    nbt.save(dst, "", converted)
    print("converted %d palette entries, %d blocks -> %s" % (len(palette), len(blocks), dst))


if __name__ == "__main__":
    convert(sys.argv[1], sys.argv[2])
