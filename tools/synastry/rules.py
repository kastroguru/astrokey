#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Изброява всички правила на резонансния модел в един CSV.

Списъкът не се поддържа на ръка. Извежда се от три малки таблици — владетелства,
знаци на планета, домове на планета — и точно тези три таблици живеят и в
`domain/synastry/SymbolicSignature.kt`. Тестът `SynastryRulesTest` чете изхода тук и
проверява, че `Resonance.kt` наистина прилага същото; ако двете се разминат, билдът
пада, вместо потребителят да чете правила, по които приложението не смята.

Изход: content/resonance_rules.csv, две колони, UTF-8 с BOM за отваряне в таблица.

    python3 tools/synastry/rules.py            # пише CSV-то
    python3 tools/synastry/rules.py --print    # печата го
"""

import csv
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "content", "resonance_rules.csv")

SIGNS = ["Овен", "Телец", "Близнаци", "Рак", "Лъв", "Дева",
         "Везни", "Скорпион", "Стрелец", "Козирог", "Водолей", "Риби"]

# Древните владетелства — същите като RulershipChain.rulers, без външни планети.
RULER = {
    "Овен": "Марс", "Телец": "Венера", "Близнаци": "Меркурий", "Рак": "Луна",
    "Лъв": "Слънце", "Дева": "Меркурий", "Везни": "Венера", "Скорпион": "Марс",
    "Стрелец": "Юпитер", "Козирог": "Сатурн", "Водолей": "Сатурн", "Риби": "Юпитер",
}

# Тези седем могат да БЪДАТ искани. Само първите пет могат да ИСКАТ — Юпитер, Сатурн и
# външните три са цветове, не точки.
CLASSIC = ["Слънце", "Луна", "Меркурий", "Венера", "Марс", "Юпитер", "Сатурн"]
# Редът е този, по който четенето ги обхожда — същият като Resonance.POINTS.
POINTS = ["Луна", "Слънце", "Венера", "Меркурий", "Марс"]
OUTER = ["Уран", "Нептун", "Плутон"]

# Новите управители влизат САМО при двете куспиди. Навсякъде другаде таблицата е древна и
# точно затова цветът на външните планети се ражда единствено по аспект.
MODERN = {"Скорпион": "Плутон", "Водолей": "Уран", "Риби": "Нептун"}

# Знаците на една планета, в реда на зодиака; домът е поредният номер на знака.
SIGNS_OF = {}
for _i, _s in enumerate(SIGNS, start=1):
    SIGNS_OF.setdefault(RULER[_s], []).append((_i, _s))

HOUSE = {1: "1-ви", 2: "2-ри", 3: "3-ти", 4: "4-ти", 5: "5-и", 6: "6-и",
         7: "7-и", 8: "8-и", 9: "9-и", 10: "10-и", 11: "11-и", 12: "12-и"}

# „с“ става „със“ пред с- и з-; Слънцето и Луната вървят с определителен член.
WITH = {
    "Слънце": "със Слънцето", "Луна": "с Луната", "Меркурий": "с Меркурий",
    "Венера": "с Венера", "Марс": "с Марс", "Юпитер": "с Юпитер",
    "Сатурн": "със Сатурн", "Уран": "с Уран", "Нептун": "с Нептун", "Плутон": "с Плутон",
}


def either(items):
    return items[0] if len(items) == 1 else " или ".join(items)


def cover(point, colour):
    """Всичко, което покрива цвета `colour` върху точката `point`."""
    signs = [s for _, s in SIGNS_OF[colour]]
    houses = [HOUSE[i] for i, _ in SIGNS_OF[colour]]
    return "; ".join([
        "%s в %s" % (point, either(signs)),
        "%s в %s дом" % (point, either(houses)),
        "%s в аспект %s" % (point, WITH[colour]),
        "%s в един знак %s" % (point, WITH[colour]),
    ])


def section_sign():
    """Раздел 1 — нужда от знака, в който стои точката."""
    rows = []
    for point in POINTS:
        for sign in SIGNS:
            colour = RULER[sign]
            if colour == point:
                rows.append(("%s в %s" % (point, sign),
                             "не ражда нужда — точката стои в собствения си знак"))
            else:
                rows.append(("%s в %s" % (point, sign), cover(point, colour)))
    return rows


def section_classical_aspect():
    """Раздел 2 — нужда от аспект към класическа планета."""
    return [("%s в аспект %s" % (point, WITH[colour]), cover(point, colour))
            for point in POINTS for colour in CLASSIC if colour != point]


def section_outer_aspect():
    """Раздел 3 — нужда от аспект към Уран, Нептун, Плутон.

    Външните не владеят знак, затова нужда от техния цвят се ражда само по аспект, а
    напрегнатият аспект иска по-лека форма насреща.
    """
    rows = []
    for point in POINTS:
        for colour in OUTER:
            w = WITH[colour]
            rows.append(("%s в труден аспект %s" % (point, w),
                         "%s в лесен аспект %s; %s в съвпад %s; %s в един знак %s"
                         % (point, w, point, w, point, w)))
            rows.append(("%s в лесен аспект или съвпад %s" % (point, w),
                         "%s в какъвто и да е аспект %s; %s в един знак %s"
                         % (point, w, point, w)))
    return rows


def section_cusp(name):
    """Раздели 4 и 5 — десцендент и имум цели.

    Нуждата е чисто по знака. При Лъв четвъртият показател отпада, защото владетелят е
    Слънцето и то не може да аспектира само себе си — това не е нито автоматично
    покритие, нито автоматична загуба: гледат се останалите.
    """
    rows = []
    for i, sign in enumerate(SIGNS, start=1):
        rulers = [RULER[sign]] + ([MODERN[sign]] if sign in MODERN else [])
        parts = [
            "асцендент на партньора в %s" % sign,
            "три или повече планети на партньора в %s дом" % HOUSE[i],
            "Слънце на партньора в %s" % sign,
        ]
        for r in rulers:
            if r != "Слънце":
                parts.append("%s на партньора в аспект със Слънцето на партньора" % r)
        for r in rulers:
            parts.append("%s на партньора в един знак със собствения му асцендент" % r)
        rows.append(("%s в %s" % (name, sign), "; ".join(parts)))
    return rows


SECTIONS = [
    ("нужда от знака, в който стои точката", section_sign),
    ("нужда от аспект към класическа планета", section_classical_aspect),
    ("нужда от аспект към Уран, Нептун, Плутон", section_outer_aspect),
    ("десцендент — липсата вади 10 точки", lambda: section_cusp("Десцендент")),
    ("имум цели — липсата вади 5 точки", lambda: section_cusp("Имум цели")),
]


def all_rows():
    rows = []
    for _, build in SECTIONS:
        rows.extend(build())
    return rows


def check(rows):
    seen = {}
    for left, right in rows:
        if left in seen:
            sys.exit("дублирано правило: %s" % left)
        seen[left] = right
    expected = 5 * 12 + (5 * 7 - 5) + (5 * 3 * 2) + 12 + 12
    if len(rows) != expected:
        sys.exit("очаквани %d правила, получени %d" % (expected, len(rows)))


def main():
    rows = all_rows()
    check(rows)
    if "--print" in sys.argv:
        w = csv.writer(sys.stdout, quoting=csv.QUOTE_ALL, lineterminator="\n")
        w.writerow(["нужда в картата на А", "покрива се в картата на Б"])
        w.writerows(rows)
        return
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w", encoding="utf-8-sig", newline="") as fh:
        w = csv.writer(fh, quoting=csv.QUOTE_ALL, lineterminator="\n")
        w.writerow(["нужда в картата на А", "покрива се в картата на Б"])
        w.writerows(rows)
    print("записани %d правила в %s" % (len(rows), os.path.relpath(OUT)))
    for title, build in SECTIONS:
        print("  %-45s %3d" % (title, len(build())))


if __name__ == "__main__":
    main()
