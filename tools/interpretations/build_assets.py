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
  resonance  point,colour,outcome,title_bg,title_en,bg,en
  cusp       kind,sign,outcome,title_bg,title_en,bg,en
  band       key,bg,en

The last two belong to the synastry module and are held to a stricter rule than the rest of the
corpus: they must not read as astrology at all. See `ban_astrology`.
"""
import csv, json, os, re, sys
from collections import defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
CONTENT = HERE + "/content"
ASSETS = os.path.abspath(HERE + "/../../app/src/main/assets/interpretations")

CHAIN_COLS = ["planet", "house", "ruler", "ruler_house", "bg", "en"]
PLACEMENT_COLS = ["planet", "house", "sign", "bg", "en"]
RESONANCE_COLS = ["point", "colour", "outcome", "title_bg", "title_en", "bg", "en"]
CUSP_COLS = ["kind", "sign", "outcome", "title_bg", "title_en", "bg", "en"]
BAND_COLS = ["key", "bg", "en"]


# Every combination the app can ask for, so coverage is a fact rather than a feeling.
PLANETS = ["sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn", "uranus", "neptune", "pluto"]
RULERS = ["sun", "moon", "mercury", "venus", "mars", "jupiter", "saturn"]   # the ancient seven
SIGNS = ["aries", "taurus", "gemini", "cancer", "leo", "virgo",
         "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"]
# The house-and-sign dimension covers everything the chart screen plots, not only the ten planets:
# both lunar nodes, Chiron and Lilith are read there too. Ketu is never stored — it is exactly
# opposite Rahu — but it is read, so it is written.
PLACEMENT_BODIES = PLANETS + ["rahu", "ketu", "chiron", "lilith"]
TOTAL_CHAIN = len(PLANETS) * 12 * len(RULERS) * 12          # 10 080
TOTAL_PLACEMENT = len(PLACEMENT_BODIES) * 12 * len(SIGNS)   # 2 016

# The synastry grids. A point is one of the seven classical bodies; a colour is any of the ten.
POINTS = ["sun", "moon", "mercury", "venus", "mars"]   # only these five ask
COLOURS = PLANETS
CUSP_KINDS = ["descendant", "imum_coeli"]
# Every cell is written twice: once for when the partner carries the colour and once for when
# they do not. The reading shows one or the other, never both.
OUTCOMES = ["met", "miss"]
TOTAL_RESONANCE = (len(POINTS) * len(COLOURS) - len(POINTS)) * len(OUTCOMES)   # 126
TOTAL_CUSP = len(CUSP_KINDS) * 12 * len(OUTCOMES)                             # 48

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
RUSSIANISMS = ["быт", "нужно е да", "являет"]          # sound wrong to a Bulgarian ear
CHART_WORDS_BG = ["Слънцето", "Луната", "владетел", "хороскоп", "натална", "асцендент"]
CHART_WORDS_EN = ["ruler of", "natal chart", "the chart shows"]
# The corpus addresses the reader in the generic masculine throughout, so a feminine form is an
# inconsistency rather than a style choice. Only direct-address forms are listed: "самата работа",
# "спокойна работа" and "точността идва сама" agree with a noun and are fine.
GENDERED_BG = ["сте сама", "останете сама", "броите сама", "но не сама",
               "сте тази, ", "обичана", "уморена", "изтощена", "готова сте"]

# ── The synastry corpus must not read as astrology ────────────────────────────
#
# The rest of the corpus only avoids naming the planet, the house and the ruler. These two grids
# go further: the reader should not be able to work out which position is being described at all.
# Naming the mechanism pulls them back into the chart and out of their own life, which is the one
# thing this module exists to avoid. So the whole vocabulary is refused, not just the obvious part.
#
# Word boundaries matter here. Bare "дом" is ordinary Bulgarian for home and stays allowed; it is
# the numbered house that is banned. "Слънце" and "Луна" are banned outright — a practical text has
# no reason to reach for either.
ASTRO_BAN_BG = [
    r"Овен", r"Телец", r"Близнац", r"\bРак\b", r"\bЛъв\b", r"\bДева\b", r"\bВезни\b",
    r"Скорпион", r"Стрелец", r"Козирог", r"Водолей", r"\bРиби\b",
    r"[Сс]лънц", r"[Лл]ун[аи]", r"Меркурий", r"Венера", r"\bМарс", r"Юпитер", r"Сатурн",
    r"Уран\b", r"Нептун", r"Плутон",
    r"аспект", r"квадрат", r"тригон", r"секстил", r"опозиц", r"съвпад", r"квинконс", r"орбис",
    r"хороскоп", r"асцендент", r"десцендент", r"зодиак", r"натал", r"транзит", r"ретрограден",
    r"владетел", r"куспид", r"[Кк]арта(та)?\b", r"планет",
    r"(първи|втори|трети|четвърти|пети|шести|седми|осми|девети|десети|"
    r"единадесети|дванадесети|единайсети|дванайсети)\s+дом",
]
ASTRO_BAN_EN = [
    r"\baries\b", r"\btaurus\b", r"\bgemini\b", r"\bcancer\b", r"\bleo\b", r"\bvirgo\b",
    r"\blibra\b", r"\bscorpio\b", r"\bsagittarius\b", r"\bcapricorn\b", r"\baquarius\b",
    r"\bpisces\b",
    r"\bsun\b", r"\bmoon\b", r"\bmercury\b", r"\bvenus\b", r"\bmars\b", r"\bjupiter\b",
    r"\bsaturn\b", r"\buranus\b", r"\bneptune\b", r"\bpluto\b",
    r"\baspect", r"\bsquare\b", r"\btrine\b", r"\bsextile\b", r"\bopposition\b",
    r"\bconjunct", r"\bquincunx\b", r"\borb\b",
    r"horoscope", r"ascendant", r"descendant", r"zodiac", r"\bnatal\b", r"\btransit",
    r"retrograde", r"\bruler\b", r"\bcusp\b", r"\bchart\b", r"\bplanet",
    r"(first|second|third|fourth|fifth|sixth|seventh|eighth|ninth|tenth|eleventh|twelfth)\s+house",
]

def ban_astrology(text, lang):
    """Every banned pattern this text trips, so the writer sees all of them at once."""
    patterns = ASTRO_BAN_BG if lang == "bg" else ASTRO_BAN_EN
    flags = 0 if lang == "bg" else re.IGNORECASE
    return [p for p in patterns if re.search(p, text, flags)]

def validate_synastry(resonance, cusp):
    """The two synastry grids: the usual hygiene, plus the no-astrology rule, plus the titles.

    Titles are held to a different shape than bodies — they are card headings, so they are short
    and need not be two sentences — but to the same vocabulary rule, because the heading is the
    first thing the reader meets and gives the position away fastest of all.
    """
    problems = []
    seen_key, seen_text, seen_title = {}, {}, {}
    for r in resonance:
        key = (r["point"], r["colour"], r["outcome"])
        if key in seen_key:
            problems.append("дублиран ключ %s" % (key,))
        seen_key[key] = True
        if r["point"] not in POINTS:
            problems.append("непозната точка %s" % r["point"])
        if r["colour"] not in COLOURS:
            problems.append("непознат цвят %s" % r["colour"])
        if r["point"] == r["colour"]:
            problems.append("точка и цвят съвпадат: %s" % r["point"])
        if r["outcome"] not in OUTCOMES:
            problems.append("непознат изход %s" % r["outcome"])
    for r in cusp:
        key = (r["kind"], r["sign"], r["outcome"])
        if key in seen_key:
            problems.append("дублиран ключ %s" % (key,))
        seen_key[key] = True
        if r["kind"] not in CUSP_KINDS:
            problems.append("непознат вид куспида %s" % r["kind"])
        if r["sign"] not in SIGNS:
            problems.append("непознат знак %s" % r["sign"])
        if r["outcome"] not in OUTCOMES:
            problems.append("непознат изход %s" % r["outcome"])

    for r in resonance + cusp:
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
            for hit in ban_astrology(t, lang):
                problems.append("астрологичен речник „%s“ (%s): %s…" % (hit, lang, t[:60]))

            title = r["title_" + lang]
            if title in seen_title:
                problems.append("повтарящо се заглавие (%s): %s" % (lang, title))
            seen_title[title] = True
            if not title.strip():
                problems.append("празно заглавие (%s) за %s" % (lang, r))
            if len(title) > 60:
                problems.append("заглавие над 60 знака (%s): %s" % (lang, title))
            if title.endswith("."):
                problems.append("заглавието свършва с точка: %s" % title)
            for hit in ban_astrology(title, lang):
                problems.append("астрологичен речник в заглавие „%s“ (%s): %s" % (hit, lang, title))
        for w in RUSSIANISMS + GENDERED_BG:
            if w in r["bg"]:
                problems.append("„%s“ в български текст: %s…" % (w, r["bg"][:60]))
    return problems


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
    for r in placement:
        if r["planet"] not in PLACEMENT_BODIES:
            problems.append("непознато тяло %s" % r["planet"])
        if r["sign"] not in SIGNS:
            problems.append("непознат знак %s" % r["sign"])
        if not 1 <= int(r["house"]) <= 12:
            problems.append("дом извън 1..12: %s" % r["house"])
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
    resonance = read_content("resonance", RESONANCE_COLS)
    cusp = read_content("cusp", CUSP_COLS)
    band = read_content("band", BAND_COLS)
    validate(chain, placement)
    synastry_problems = validate_synastry(resonance, cusp)
    if synastry_problems:
        print("\n".join("!! " + p for p in synastry_problems[:40]))
        print("!! общо %d проблема в синастрията — нищо не е записано" % len(synastry_problems))
        sys.exit(1)

    per_planet = defaultdict(dict)
    for r in chain:
        key = "%s|%s|%s" % (r["house"], r["ruler"], r["ruler_house"])
        per_planet[("chain", r["planet"])][key] = {"bg": r["bg"], "en": r["en"]}
    for r in placement:
        key = "%s|%s" % (r["house"], r["sign"])
        per_planet[("placement", r["planet"])][key] = {"bg": r["bg"], "en": r["en"]}

    # The synastry grids are small enough to ship as one file each: 63 and 24 entries, opened
    # together whenever the synastry screen is drawn, so splitting them per body would only cost
    # extra parses.
    synastry = {"resonance": {}, "cusp": {}, "band": {}}
    for r in resonance:
        synastry["resonance"]["%s|%s|%s" % (r["point"], r["colour"], r["outcome"])] = {
            "bg": r["bg"], "en": r["en"],
            "title_bg": r["title_bg"], "title_en": r["title_en"],
        }
    for r in band:
        synastry["band"][r["key"]] = {"bg": r["bg"], "en": r["en"]}
    for r in cusp:
        synastry["cusp"]["%s|%s|%s" % (r["kind"], r["sign"], r["outcome"])] = {
            "bg": r["bg"], "en": r["en"],
            "title_bg": r["title_bg"], "title_en": r["title_en"],
        }

    os.makedirs(ASSETS, exist_ok=True)
    written = 0
    for (kind, planet), entries in sorted(per_planet.items()):
        path = "%s/%s_%s.json" % (ASSETS, kind, planet)
        with open(path, "w", encoding="utf-8") as f:
            json.dump(entries, f, ensure_ascii=False, separators=(",", ":"), sort_keys=True)
        written += len(entries)
        print("%-28s %5d записа" % (os.path.basename(path), len(entries)))

    for name, entries in sorted(synastry.items()):
        path = "%s/%s.json" % (ASSETS, name)
        with open(path, "w", encoding="utf-8") as f:
            json.dump(entries, f, ensure_ascii=False, separators=(",", ":"), sort_keys=True)
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
    have_pl = {(r["planet"], int(r["house"]), r["sign"]) for r in placement}
    short = []
    for b in PLACEMENT_BODIES:
        missing = sum(1 for h in range(1, 13) for sg in SIGNS if (b, h, sg) not in have_pl)
        if 0 < missing < 144:
            short.append("%s липсват %d" % (b, missing))
    if short:
        print("непълни тела (дом и знак): " + ", ".join(short))
    print("резонанс    %5d / %5d  (%.1f%%)" % (len(resonance), TOTAL_RESONANCE,
                                                100.0 * len(resonance) / TOTAL_RESONANCE))
    have_res = {(r["point"], r["colour"], r["outcome"]) for r in resonance}
    missing_res = [(p, c, o) for p in POINTS for c in COLOURS for o in OUTCOMES
                   if p != c and (p, c, o) not in have_res]
    if missing_res:
        print("липсват двойки: " + ", ".join("%s←%s/%s" % pc for pc in missing_res[:20]) +
              (" …" if len(missing_res) > 20 else ""))
    print("куспиди     %5d / %5d  (%.1f%%)" % (len(cusp), TOTAL_CUSP,
                                               100.0 * len(cusp) / TOTAL_CUSP))
    have_cusp = {(r["kind"], r["sign"], r["outcome"]) for r in cusp}
    missing_cusp = [(k, sg, o) for k in CUSP_KINDS for sg in SIGNS for o in OUTCOMES
                    if (k, sg, o) not in have_cusp]
    if missing_cusp:
        print("липсват куспиди: " + ", ".join("%s|%s/%s" % kc for kc in missing_cusp[:24]))
    print("ленти       %5d / %5d" % (len(band), 7))
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
