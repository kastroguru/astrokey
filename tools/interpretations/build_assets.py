#!/usr/bin/env python3
"""Turns the content CSVs into the JSON the app ships in assets, and back again.

Why JSON per planet rather than one big file or a database: a lookup only ever needs one planet, so
the app parses ~150 KB and caches it; the files stay small enough to diff per planet in git; and the
CSV round-trip that the texts are actually written in stays trivial.

  build   content/*.csv        → app/src/main/assets/interpretations/*.json
  export  assets/*.json        → one CSV per dimension, for editing outside the code

Content CSV columns:
  chain      planet,house,ruler,ruler_house,bg,en
  placement  planet,house,sign,bg,en
"""
import csv, json, os, sys
from collections import defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
CONTENT = HERE + "/content"
ASSETS = os.path.abspath(HERE + "/../../app/src/main/assets/interpretations")

CHAIN_COLS = ["planet", "house", "ruler", "ruler_house", "bg", "en"]
PLACEMENT_COLS = ["planet", "house", "sign", "bg", "en"]

# Every combination the app can ask for, so coverage is a fact rather than a feeling.
PLANETS = ["sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn", "uranus", "neptune", "pluto"]
RULERS = ["sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn"]   # the ancient seven
SIGNS = ["aries", "taurus", "gemini", "cancer", "leo", "virgo",
         "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"]
TOTAL_CHAIN = len(PLANETS) * 12 * len(RULERS) * 12          # 10 080
TOTAL_PLACEMENT = len(PLANETS) * 12 * len(SIGNS)            # 1 440

# Which chain combinations can physically exist.
#
# A body that rules the house it stands in cannot rule it from anywhere else, so `ruler_house`
# can only be the planet's own house. And the Sun, Mercury and Venus travel together: Mercury is
# never more than ~28° from the Sun, Venus never more than ~48°, so Mercury and Venus are never
# more than ~76° apart. Houses are NOT 30° wide in Placidus — at higher latitudes some run 10–15° —
# so the elongation is converted at a deliberately conservative 15° per house rather than 30°.
# That is why Mercury may sit two houses from the Sun, Venus three, and Mercury and Venus five.
MIN_HOUSE_DEG = 15.0            # a narrow Placidus house at higher latitudes, not the naive 30°
MAX_ELONGATION = {("mercury", "sun"): 28.0, ("sun", "venus"): 48.0, ("mercury", "venus"): 76.0}
# Keys are sorted pairs so the lookup below matches either order.
MAX_HOUSES_APART = {k: int(round(v / MIN_HOUSE_DEG)) for k, v in MAX_ELONGATION.items()}   # 2, 3, 5

def houses_apart(a, b):
    d = abs(a - b) % 12
    return min(d, 12 - d)

def allowed_ruler_houses(planet, house, ruler):
    """The ruler_house values this (planet, house, ruler) can actually take."""
    if ruler == planet:
        return {house}                                   # it rules from where it already stands
    limit = MAX_HOUSES_APART.get(tuple(sorted((planet, ruler))))
    if limit is None:
        return set(range(1, 13))
    return {h for h in range(1, 13) if houses_apart(h, house) <= limit}

def possible_chain_rows():
    return sum(len(allowed_ruler_houses(p, h, r))
               for p in PLANETS for h in range(1, 13) for r in RULERS)

def read_content(kind, cols):
    rows = []
    for name in sorted(os.listdir(CONTENT)):
        if not name.startswith(kind) or not name.endswith(".csv"):
            continue
        with open(CONTENT + "/" + name, encoding="utf-8-sig") as f:
            for i, row in enumerate(csv.DictReader(f), start=2):
                missing = [c for c in cols if not (row.get(c) or "").strip()]
                if missing:
                    print("!! %s:%d липсва %s" % (name, i, ",".join(missing)))
                    continue
                rows.append({c: row[c].strip() for c in cols})
    return rows

# The texts are written by hand in large batches, so the things that went wrong once are checked
# here rather than trusted: a bad row must stop the build instead of shipping into the app.
RUSSIANISMS = ["скучн", "быт", "нужно е да", "являет"]          # sound wrong to a Bulgarian ear
CHART_WORDS_BG = ["Слънцето", "Луната", "владетел", "хороскоп", "натална", "асцендент"]
CHART_WORDS_EN = ["ruler of", "natal chart", "the chart shows"]
# The corpus addresses the reader in the generic masculine throughout, so a feminine form is an
# inconsistency rather than a style choice. Only direct-address forms are listed: "самата работа",
# "спокойна работа" and "точността идва сама" agree with a noun and are fine.
GENDERED_BG = ["сте сама", "останете сама", "броите сама", "но не сама",
               "сте тази, ", "обичана", "уморена", "изтощена", "готова сте"]

def validate(chain, placement):
    problems = []
    seen_key, seen_text = {}, {}
    for r in chain + placement:
        key = (r["planet"], r["house"], r.get("ruler", ""), r.get("ruler_house", ""), r.get("sign", ""))
        if key in seen_key:
            problems.append("дублиран ключ %s" % (key,))
        seen_key[key] = True
        for lang in ("bg", "en"):
            t = r[lang]
            if t in seen_text:
                problems.append("повтарящ се текст (%s): %s…" % (lang, t[:60]))
            seen_text[t] = True
            if "  " in t or t != t.strip():
                problems.append("двойни/висящи интервали: %s…" % t[:60])
            if t.endswith(","):
                problems.append("текстът свършва със запетая: %s…" % t[:60])
            if t.count(".") + t.count("!") + t.count("?") < 2:
                problems.append("под две изречения: %s…" % t[:60])
        for w in RUSSIANISMS + CHART_WORDS_BG + GENDERED_BG:
            if w in r["bg"]:
                problems.append("„%s“ в български текст: %s…" % (w, r["bg"][:60]))
        for w in CHART_WORDS_EN:
            if w in r["en"].lower():
                problems.append("'%s' in English text: %s…" % (w, r["en"][:60]))
    for r in chain:
        allowed = allowed_ruler_houses(r["planet"], int(r["house"]), r["ruler"])
        if int(r["ruler_house"]) not in allowed:
            problems.append("невъзможна комбинация %s|%s|%s|%s" %
                            (r["planet"], r["house"], r["ruler"], r["ruler_house"]))
    if problems:
        print("\n".join("!! " + p for p in problems[:40]))
        print("!! общо %d проблема — нищо не е записано" % len(problems))
        sys.exit(1)

def build():
    chain = read_content("chain", CHAIN_COLS)
    placement = read_content("placement", PLACEMENT_COLS)
    validate(chain, placement)

    per_planet = defaultdict(dict)
    for r in chain:
        key = "%s|%s|%s" % (r["house"], r["ruler"], r["ruler_house"])
        per_planet[("chain", r["planet"])][key] = {"bg": r["bg"], "en": r["en"]}
    for r in placement:
        key = "%s|%s" % (r["house"], r["sign"])
        per_planet[("placement", r["planet"])][key] = {"bg": r["bg"], "en": r["en"]}

    os.makedirs(ASSETS, exist_ok=True)
    written = 0
    for (kind, planet), entries in sorted(per_planet.items()):
        path = "%s/%s_%s.json" % (ASSETS, kind, planet)
        with open(path, "w", encoding="utf-8") as f:
            json.dump(entries, f, ensure_ascii=False, separators=(",", ":"), sort_keys=True)
        written += len(entries)
        print("%-28s %5d записа" % (os.path.basename(path), len(entries)))

    dup = len(chain) + len(placement) - written
    possible = possible_chain_rows()
    have = {(r["planet"], int(r["house"]), r["ruler"], int(r["ruler_house"])) for r in chain}
    gaps = defaultdict(int)
    for p in PLANETS:
        for h in range(1, 13):
            for r in RULERS:
                for rh in allowed_ruler_houses(p, h, r):
                    if (p, h, r, rh) not in have:
                        gaps[(p, h)] += 1
    print("\nверига      %5d / %5d възможни  (%.1f%%)" % (len(chain), possible, 100.0 * len(chain) / possible))
    started = [(c, n) for c, n in sorted(gaps.items()) if any(k[:2] == c for k in have)]
    if started:
        print("непълни започнати клетки: " + ", ".join("%s h%d липсват %d" % (c[0], c[1], n) for c, n in started))
    print("дом и знак  %5d / %5d  (%.1f%%)" % (len(placement), TOTAL_PLACEMENT, 100.0 * len(placement) / TOTAL_PLACEMENT))
    if dup:
        print("!! %d дублирани ключа са презаписани — проверй съдържанието" % dup)

def export():
    out = {}
    for name in sorted(os.listdir(ASSETS)):
        if not name.endswith(".json"):
            continue
        kind, planet = name[:-5].split("_", 1)
        data = json.load(open(ASSETS + "/" + name, encoding="utf-8"))
        for key, v in data.items():
            parts = key.split("|")
            if kind == "chain":
                row = dict(zip(CHAIN_COLS, [planet, parts[0], parts[1], parts[2], v["bg"], v["en"]]))
            else:
                row = dict(zip(PLACEMENT_COLS, [planet, parts[0], parts[1], v["bg"], v["en"]]))
            out.setdefault(kind, []).append(row)
    for kind, rows in out.items():
        cols = CHAIN_COLS if kind == "chain" else PLACEMENT_COLS
        path = os.path.expanduser("~/Desktop/astro-key-%s.csv" % kind)
        with open(path, "w", encoding="utf-8-sig", newline="") as f:
            w = csv.DictWriter(f, fieldnames=cols, quoting=csv.QUOTE_ALL)
            w.writeheader()
            w.writerows(sorted(rows, key=lambda r: (r["planet"], int(r["house"]))))
        print("%s → %d реда" % (path, len(rows)))

if __name__ == "__main__":
    export() if "--export" in sys.argv else build()
