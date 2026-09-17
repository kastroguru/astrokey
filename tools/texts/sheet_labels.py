"""Human-readable, unique labels for every text — the only address the editor sees."""
import re

PLANET_BG = {
    "sun": "Слънце", "moon": "Луна", "mercury": "Меркурий", "venus": "Венера", "mars": "Марс",
    "jupiter": "Юпитер", "saturn": "Сатурн", "uranus": "Уран", "neptune": "Нептун",
    "pluto": "Плутон", "chiron": "Хирон", "lilith": "Лилит",
    "rahu": "Раху", "ketu": "Кету", "north_node": "Северен възел", "south_node": "Южен възел",
    "earth": "Земя", "asc": "Асцендент", "mc": "MC",
}
SIGN_BG = {
    "aries": "Овен", "taurus": "Телец", "gemini": "Близнаци", "cancer": "Рак", "leo": "Лъв",
    "virgo": "Дева", "libra": "Везни", "scorpio": "Скорпион", "sagittarius": "Стрелец",
    "capricorn": "Козирог", "aquarius": "Водолей", "pisces": "Риби",
}
# Kotlin file → the area a human recognises
AREA = {
    "HdHangingGateTexts": "HD висящ гейт",
    "HdGateTransitTexts": "HD гейт днес",
    "HdOpenCenterTexts": "HD отворен център",
    "HdParentingTexts": "HD дете",
    "HdVariableTexts": "HD стрелки",
    "HdConnectionTexts": "HD свързване",
    "HdDescriptions": "HD",
    "TransitInterpretations": "Транзит",
    "PlanetInHouseLines": "Планета в дом",
    "SunInSign": "Слънце в знак",
    "MoonInSign": "Луна в знак",
    "AscendantInSign": "Асцендент в знак",
    "MidheavenInSign": "MC в знак",
    "RulerHouseKey": "Владетел в дом",
    "PlanetMeanings": "Планета",
    "SignMeanings": "Знак",
    "HouseMeanings": "Дом",
    "AngleMeanings": "Ъгъл",
    "ChartConcepts": "Понятие",
    "NatalInterpretations": "Натал",
}


def _pl(key: str) -> str:
    return PLANET_BG.get(key, key)


def _sg(key: str) -> str:
    return SIGN_BG.get(key, key)


def csv_label(kind: str, key: str) -> str:
    parts = key.split("|")
    if kind == "chain":
        planet, house, ruler, ruler_house = parts
        return f"Астро · {_pl(planet)} в {house} дом · владетел {_pl(ruler)} в {ruler_house} дом"
    planet, house, sign = parts
    return f"Астро · {_pl(planet)} в {house} дом · {_sg(sign)}"


# Kotlin context strings are code; strip the syntax and keep what identifies the row.
SIGN_CONST = re.compile(r"\bZodiacSign\.([A-Z]+)\b")

_STRIP = [
    (re.compile(r"^\s*val\s+([A-Za-z0-9_]+)\s*(?::[^=]*)?=\s*t?\s*\(?"), r"\1"),
    (re.compile(r"^\s*val\s+([A-Za-z0-9_]+)\s*(?::.*)?$"), r"\1"),
    (re.compile(r"\s*->\s*t?\s*\(?\s*$"), ""),
    (re.compile(r"\bPair<[^>]*>"), ""),
    (re.compile(r"\s+to\s+[A-Za-z_]+\s*$"), ""),      # "GENERATOR to TypeInfo" -> "GENERATOR"
    (re.compile(r"\s+to\s*$"), ""),                    # a bare "to" left by a split key
    (re.compile(r"\bto\s+t\s*\($"), ""),
    (re.compile(r"\bto\s*\($"), ""),
    (re.compile(r"^map\s*\[\s*"), ""),
    (re.compile(r"^p\s*\(\s*"), ""),
    (re.compile(r"\bkeyFor\s*\(\s*"), ""),
    (re.compile(r"\bHdType\.|\bHdAuthority\.|\bHdCenter\.|\bHdDetermination\.|"
                r"\bHdEnvironment\.|\bHdMotivation\.|\bHdPerspective\.|"
                r"\bHdConnectionKind\.|\bHdTransitEffect\.|\bHdDefinition\."), ""),
    (re.compile(r"^key\s*\(\s*"), ""),
    (re.compile(r"\]\s*=\s*$"), ""),
    (re.compile(r"^\s*[\"']|[\"']\s*$"), ""),
    (re.compile(r"\s*=\s*$"), ""),
    (re.compile(r"[(),\[\]]+"), " "),
    (re.compile(r"\s{2,}"), " "),
]


def _clean(part: str) -> str:
    part = SIGN_CONST.sub(lambda m: _sg(m.group(1).lower()), part.strip())
    for pat, rep in _STRIP:
        part = pat.sub(rep, part).strip()
    return part.strip(' ·-="\'').replace("_", " ")


def kotlin_label(source: str, context: str, key: str = "") -> str:
    """area · enclosing map key · field — the second part is what tells sibling fields apart."""
    stem = source.rsplit("/", 1)[-1][:-3]
    parts = [AREA.get(stem, stem)]
    k, c = _clean(key), _clean(context)
    # When the key and the field name are the same thing (a one-line entry), print it once.
    if k and k != c and k not in c and c not in k:
        parts.append(k)
    if c:
        parts.append(c)
    return " · ".join(p for p in parts if p)


def uniquify(labels):
    """Labels are the address on the way back, so collisions must be made distinct."""
    seen, out = {}, []
    for lab in labels:
        n = seen.get(lab, 0) + 1
        seen[lab] = n
        out.append(lab if n == 1 else f"{lab} #{n}")
    return out


# ── the app's own key ────────────────────────────────────────────────────────────
# What the code itself uses to look a text up, prefixed by the object it lives in, so the
# editor sees the same identifier the app does: PlanetInHouseLines.sun_1,
# TransitInterpretations.sun_sun, HdDescriptions.HEAD.undefined, HdHangingGateTexts.1.

_QUOTED = re.compile(r'"([^"]+)"')
_ENUM = re.compile(r"\b(?:HdType|HdAuthority|HdCenter|HdDefinition|HdDetermination|HdEnvironment"
                   r"|HdMotivation|HdPerspective|HdConnectionKind|HdTransitEffect|ZodiacSign)"
                   r"\.([A-Z_0-9]+)")
_KEYFOR = re.compile(r"keyFor\(\s*HdType\.([A-Z_]+)\s*,\s*HdAuthority\.([A-Z_]+)\s*\)")
_MAPKEY = re.compile(r"map\[\s*key\(\s*(\d+)\s*,\s*(\d+)\s*\)\s*\]")
_NUMKEY = re.compile(r"^\s*(\d+)\s+to\b")
_VALNAME = re.compile(r"^\s*val\s+([A-Za-z0-9_]+)")
_FIELD = re.compile(r"^\s*([A-Za-z0-9_]+)\s*=")


def _one_key(part: str) -> str:
    """The lookup key a single line of Kotlin declares, or '' if it declares none."""
    if not part:
        return ""
    m = _KEYFOR.search(part)
    if m:
        return f"{m.group(1)}|{m.group(2)}"
    m = _MAPKEY.search(part)
    if m:
        return f"{m.group(1)}-{m.group(2)}"
    m = _QUOTED.search(part)
    if m:
        return m.group(1)
    m = _ENUM.search(part)
    if m:
        return m.group(1)
    m = _NUMKEY.match(part)
    if m:
        return m.group(1)
    m = _VALNAME.match(part)
    if m:
        return m.group(1)
    return ""


def app_key(source: str, context: str, key: str = "") -> str:
    """`<object>.<key>` — and `<object>.<key>.<field>` where a record has several texts."""
    stem = source.rsplit("/", 1)[-1][:-3]
    outer = _one_key(key)
    inner = _one_key(context)
    field = ""
    m = _FIELD.match(context.strip())
    if m and not _QUOTED.search(context) and not _ENUM.search(context):
        field = m.group(1)

    parts = [p for p in (outer, inner if inner != outer else "", field) if p]
    if not parts:                      # a branch rather than a map entry (NatalInterpretations)
        parts = [re.sub(r"[^A-Za-z0-9_.]+", "", context.split("->")[0]) or "text"]
    # a field name already captured as inner must not repeat
    seen, uniq = set(), []
    for p in parts:
        if p not in seen:
            seen.add(p)
            uniq.append(p)
    return stem + "." + ".".join(uniq)


def csv_key(kind: str, key: str) -> str:
    return f"{kind}.{key}"
