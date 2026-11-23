"""Converts the original 1.12.2 `.lang` files to 1.21.1 JSON language files.

Only item keys are renamed: 1.12 used "item.<name>.name" with a bare
unlocalized name, 1.21 derives "item.infinity.<name>" from the registry id.
Every other key (snap messages, advancement titles) is kept verbatim.

Usage: python3 tools/convert_lang.py <in.lang> <out.json>
"""
import json
import sys

ITEM_KEYS = {
    "item.power_stone.name": "item.infinity.power_stone",
    "item.reality_stone.name": "item.infinity.reality_stone",
    "item.soul_stone.name": "item.infinity.soul_stone",
    "item.time_stone.name": "item.infinity.time_stone",
    "item.mind_stone.name": "item.infinity.mind_stone",
    "item.space_stone.name": "item.infinity.space_stone",
}


def read_text(path):
    raw = open(path, "rb").read()
    for encoding in ("utf-8", "cp1254", "latin-1"):
        try:
            return raw.decode(encoding)
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", "replace")


def convert(src, dst):
    entries = {}
    for line in read_text(src).splitlines():
        line = line.replace("\r", "").strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        key = ITEM_KEYS.get(key.strip(), key.strip())
        entries[key] = value.strip()
    with open(dst, "w", encoding="utf-8") as out:
        json.dump(entries, out, ensure_ascii=False, indent=2, sort_keys=True)
        out.write("\n")
    print("wrote %d keys to %s" % (len(entries), dst))


if __name__ == "__main__":
    convert(sys.argv[1], sys.argv[2])
