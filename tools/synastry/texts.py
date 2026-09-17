#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Изнася всички текстове на синастрията в един CSV за редактиране и ги връща обратно.

Един ред = един текст. Редактира се само колоната „български“ (и „english“, ако трябва);
всичко останало е адрес и не бива да се пипа. Празен текст значи „остави както е“.

    python3 tools/synastry/texts.py export              # → ~/Desktop/astro-key-синастрия.csv
    python3 tools/synastry/texts.py export --out ФАЙЛ
    python3 tools/synastry/texts.py import ФАЙЛ         # само отчет, нищо не се пише
    python3 tools/synastry/texts.py import ФАЙЛ --apply # записва

След внасяне задължително:
    python3 tools/interpretations/build_assets.py
Валидаторът там е този, който отказва астрологичен речник, повторени текстове и прочее —
този скрипт нарочно не проверява съдържание, за да има само едно място с правила.
"""

import csv
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.abspath(HERE + "/../..")
CONTENT = ROOT + "/kastro_android/tools/interpretations/content"
if not os.path.isdir(CONTENT):                      # когато се вика от kastro_android/
    CONTENT = os.path.abspath(HERE + "/../interpretations/content")
RES = os.path.abspath(HERE + "/../..")
STRINGS_EN = RES + "/app/src/main/res/values/strings.xml"
STRINGS_BG = RES + "/app/src/main/res/values-bg/strings.xml"

COLS = ["ключ", "вид", "какво е", "български", "english"]

RESONANCE_FILES = ["resonance_a.csv", "resonance_b.csv",
                   "resonance_miss_a.csv", "resonance_miss_b.csv"]
CUSP_FILES = ["cusp.csv", "cusp_miss.csv"]

BODY_BG = {"sun": "Слънцето", "moon": "Луната", "mercury": "Меркурий", "venus": "Венера",
           "mars": "Марс", "jupiter": "Юпитер", "saturn": "Сатурн",
           "uranus": "Уран", "neptune": "Нептун", "pluto": "Плутон"}
# Цветът се именува с прилагателно — „лунен цвят“, не „Луна цвят“.
COLOUR_BG = {"sun": "слънчев", "moon": "лунен", "mercury": "меркуриев", "venus": "венерин",
             "mars": "марсов", "jupiter": "юпитеров", "saturn": "сатурнов",
             "uranus": "уранов", "neptune": "нептунов", "pluto": "плутонов"}
SIGN_BG = {"aries": "Овен", "taurus": "Телец", "gemini": "Близнаци", "cancer": "Рак",
           "leo": "Лъв", "virgo": "Дева", "libra": "Везни", "scorpio": "Скорпион",
           "sagittarius": "Стрелец", "capricorn": "Козирог", "aquarius": "Водолей",
           "pisces": "Риби"}
CUSP_BG = {"descendant": "Десцендент", "imum_coeli": "Имум цели"}
OUTCOME_BG = {"met": "покрито", "miss": "непокрито"}

# Низовете на интерфейса, които се появиха със синастрията.
UI_KEYS = [
    "synastry_title", "synastry_open", "synastry_pick_partner", "synastry_pick_prompt",
    "synastry_needs_two", "synastry_group_cusp", "synastry_group_point",
    "synastry_group_cusp_miss", "synastry_group_point_miss", "help_synastry",
    "synastry_via_aspect", "synastry_via_sign", "synastry_via_house", "synastry_via_same_sign",
    "synastry_cusp_descendant", "synastry_cusp_imum_coeli", "synastry_by_ascendant",
    "synastry_by_stellium", "synastry_by_sun_sign", "synastry_by_ruler_aspect",
    "synastry_by_ruler_angle",
]


def read_csv(name):
    path = CONTENT + "/" + name
    if not os.path.exists(path):
        return []
    with open(path, encoding="utf-8") as fh:
        return list(csv.DictReader(fh))


def write_csv(name, rows, cols):
    with open(CONTENT + "/" + name, "w", encoding="utf-8", newline="") as fh:
        w = csv.DictWriter(fh, fieldnames=cols, quoting=csv.QUOTE_ALL, lineterminator="\n")
        w.writeheader()
        w.writerows(rows)


def read_strings(path, name):
    src = open(path, encoding="utf-8").read()
    m = re.search(r'<string name="%s">(.*?)</string>' % re.escape(name), src, re.S)
    return m.group(1) if m else None


def write_string(path, name, value):
    src = open(path, encoding="utf-8").read()
    pat = re.compile(r'(<string name="%s">)(.*?)(</string>)' % re.escape(name), re.S)
    if not pat.search(src):
        return False
    open(path, "w", encoding="utf-8").write(pat.sub(lambda m: m.group(1) + value + m.group(3), src, count=1))
    return True


def collect():
    """Всеки текст с адреса му, в реда, в който се чете на екрана."""
    out = []
    for name in RESONANCE_FILES:
        for r in read_csv(name):
            what = "%s иска %s цвят — %s" % (
                BODY_BG[r["point"]], COLOUR_BG[r["colour"]], OUTCOME_BG[r["outcome"]])
            base = "resonance|%s|%s|%s" % (r["point"], r["colour"], r["outcome"])
            out.append((base + "|title", "заглавие", what, r["title_bg"], r["title_en"]))
            out.append((base + "|body", "текст", what, r["bg"], r["en"]))
    for name in CUSP_FILES:
        for r in read_csv(name):
            what = "%s в %s — %s" % (CUSP_BG[r["kind"]], SIGN_BG[r["sign"]], OUTCOME_BG[r["outcome"]])
            base = "cusp|%s|%s|%s" % (r["kind"], r["sign"], r["outcome"])
            out.append((base + "|title", "заглавие", what, r["title_bg"], r["title_en"]))
            out.append((base + "|body", "текст", what, r["bg"], r["en"]))
    for r in read_csv("band.csv"):
        what = "въведение под числото" if r["key"] == "intro" else "лента %s" % r["key"]
        out.append(("band|" + r["key"], "текст", what, r["bg"], r["en"]))
    for name in UI_KEYS:
        bg = read_strings(STRINGS_BG, name)
        en = read_strings(STRINGS_EN, name)
        if bg is None:
            continue
        out.append(("ui|" + name, "интерфейс", name, bg, en or ""))
    return out


def do_export(out_path):
    rows = collect()
    with open(out_path, "w", encoding="utf-8-sig", newline="") as fh:
        w = csv.writer(fh, quoting=csv.QUOTE_ALL, lineterminator="\n")
        w.writerow(COLS)
        w.writerows(rows)
    print("%d текста → %s" % (len(rows), out_path))
    kinds = {}
    for _, kind, _, _, _ in rows:
        kinds[kind] = kinds.get(kind, 0) + 1
    for k, n in sorted(kinds.items()):
        print("   %-12s %d" % (k, n))


def do_import(in_path, apply):
    with open(in_path, encoding="utf-8-sig") as fh:
        incoming = {r["ключ"]: r for r in csv.DictReader(fh)}
    current = {k: (bg, en) for k, _, _, bg, en in collect()}

    changes = []
    for key, (bg, en) in current.items():
        row = incoming.get(key)
        if row is None:
            continue
        new_bg = (row.get("български") or "").strip()
        new_en = (row.get("english") or "").strip()
        if new_bg and new_bg != bg:
            changes.append((key, "bg", bg, new_bg))
        if new_en and new_en != en:
            changes.append((key, "en", en, new_en))

    unknown = [k for k in incoming if k not in current]
    print("%d промени, %d непознати ключа" % (len(changes), len(unknown)))
    for k in unknown[:10]:
        print("   ? %s" % k)
    for key, lang, old, new in changes[:15]:
        print("   %s [%s]\n      беше: %s\n      става: %s" % (key, lang, old[:70], new[:70]))
    if len(changes) > 15:
        print("   … и още %d" % (len(changes) - 15))
    if not apply:
        print("\n(само отчет — добави --apply, за да се запише)")
        return

    by_key = {}
    for key, lang, _, new in changes:
        by_key.setdefault(key, {})[lang] = new

    # CSV гридовете
    for name in RESONANCE_FILES + CUSP_FILES:
        rows = read_csv(name)
        if not rows:
            continue
        cols = list(rows[0].keys())
        touched = False
        for r in rows:
            if "point" in r:
                base = "resonance|%s|%s|%s" % (r["point"], r["colour"], r["outcome"])
            else:
                base = "cusp|%s|%s|%s" % (r["kind"], r["sign"], r["outcome"])
            for suffix, fields in (("|title", ("title_bg", "title_en")), ("|body", ("bg", "en"))):
                ch = by_key.get(base + suffix)
                if not ch:
                    continue
                if "bg" in ch:
                    r[fields[0]] = ch["bg"]; touched = True
                if "en" in ch:
                    r[fields[1]] = ch["en"]; touched = True
        if touched:
            write_csv(name, rows, cols)
            print("записан %s" % name)

    rows = read_csv("band.csv")
    if rows:
        touched = False
        for r in rows:
            ch = by_key.get("band|" + r["key"])
            if ch:
                if "bg" in ch: r["bg"] = ch["bg"]; touched = True
                if "en" in ch: r["en"] = ch["en"]; touched = True
        if touched:
            write_csv("band.csv", rows, ["key", "bg", "en"])
            print("записан band.csv")

    for key, ch in by_key.items():
        if not key.startswith("ui|"):
            continue
        name = key[3:]
        if "bg" in ch: write_string(STRINGS_BG, name, ch["bg"])
        if "en" in ch: write_string(STRINGS_EN, name, ch["en"])
    if any(k.startswith("ui|") for k in by_key):
        print("записани низовете на интерфейса")

    print("\nсега: python3 tools/interpretations/build_assets.py")


if __name__ == "__main__":
    args = sys.argv[1:]
    if args and args[0] == "export":
        out = args[args.index("--out") + 1] if "--out" in args \
            else os.path.expanduser("~/Desktop/astro-key-синастрия.csv")
        do_export(out)
    elif len(args) >= 2 and args[0] == "import":
        do_import(args[1], "--apply" in args)
    else:
        print(__doc__)
