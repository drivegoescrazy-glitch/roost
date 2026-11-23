"""Minimal NBT reader/writer used to convert the original 1.12.2 structure NBT
to the modern structure-template layout. Standard library only."""
import gzip
import json
import struct
import sys


class IntList(list):
    """A list tag of ints (structure templates use lists, not int arrays)."""
    pass


def read(f):
    t = f.read(1)[0]
    name = read_str(f)
    return name, read_payload(f, t)


def read_str(f):
    (n,) = struct.unpack('>H', f.read(2))
    return f.read(n).decode('utf-8', 'replace')


def read_payload(f, t):
    if t == 1:
        return struct.unpack('>b', f.read(1))[0]
    if t == 2:
        return struct.unpack('>h', f.read(2))[0]
    if t == 3:
        return struct.unpack('>i', f.read(4))[0]
    if t == 4:
        return struct.unpack('>q', f.read(8))[0]
    if t == 5:
        return struct.unpack('>f', f.read(4))[0]
    if t == 6:
        return struct.unpack('>d', f.read(8))[0]
    if t == 7:
        (n,) = struct.unpack('>i', f.read(4))
        return list(f.read(n))
    if t == 8:
        return read_str(f)
    if t == 9:
        it = f.read(1)[0]
        (n,) = struct.unpack('>i', f.read(4))
        return [read_payload(f, it) for _ in range(n)]
    if t == 10:
        out = {}
        while True:
            ct = f.read(1)[0]
            if ct == 0:
                return out
            k = read_str(f)
            out[k] = read_payload(f, ct)
    if t == 11:
        (n,) = struct.unpack('>i', f.read(4))
        return list(struct.unpack('>%di' % n, f.read(4 * n)))
    if t == 12:
        (n,) = struct.unpack('>i', f.read(4))
        return list(struct.unpack('>%dq' % n, f.read(8 * n)))
    raise ValueError(t)


def load(path):
    opener = gzip.open if path.endswith('.nbt') else open
    with opener(path, 'rb') as f:
        return read(f)


def write_val(f, t, v):
    if t == 1:
        f.write(struct.pack('>b', v))
    elif t == 2:
        f.write(struct.pack('>h', v))
    elif t == 3:
        f.write(struct.pack('>i', v))
    elif t == 4:
        f.write(struct.pack('>q', v))
    elif t == 5:
        f.write(struct.pack('>f', v))
    elif t == 6:
        f.write(struct.pack('>d', v))
    elif t == 7:
        f.write(struct.pack('>i', len(v)))
        f.write(bytes(v))
    elif t == 8:
        b = v.encode('utf-8')
        f.write(struct.pack('>H', len(b)))
        f.write(b)
    elif t == 9:
        if not v:
            f.write(b'\x08')
            f.write(struct.pack('>i', 0))
        else:
            it = 3 if isinstance(v, IntList) else type_of(v[0])
            f.write(bytes([it]))
            f.write(struct.pack('>i', len(v)))
            for e in v:
                write_val(f, it, e)
    elif t == 10:
        for k, vv in v.items():
            tt = type_of(vv)
            f.write(bytes([tt]))
            write_val(f, 8, k)
            write_val(f, tt, vv)
        f.write(b'\x00')
    elif t == 11:
        f.write(struct.pack('>i', len(v)))
        f.write(struct.pack('>%di' % len(v), *v))
    elif t == 12:
        f.write(struct.pack('>i', len(v)))
        f.write(struct.pack('>%dq' % len(v), *v))
    else:
        raise ValueError(t)


def type_of(v):
    if isinstance(v, bool):
        return 1
    if isinstance(v, int):
        return 3
    if isinstance(v, float):
        return 6
    if isinstance(v, str):
        return 8
    if isinstance(v, dict):
        return 10
    if isinstance(v, IntList):
        return 9
    if isinstance(v, list):
        if v and all(isinstance(e, int) for e in v):
            return 11
        return 9
    raise TypeError(type(v))


def save(path, name, root):
    with gzip.open(path, 'wb') as f:
        f.write(b'\x0a')
        write_val(f, 8, name)
        write_val(f, 10, root)


if __name__ == '__main__':
    n, root = load(sys.argv[1])
    print(json.dumps(root, indent=1, default=str)[:20000])
