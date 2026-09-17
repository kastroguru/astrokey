#!/usr/bin/env python3
"""One file with every written text in the app, and the way back.

    corpus.py export    → tools/interpretations/all_texts.csv   (whole corpus, one row per text)
    corpus.py import    → applies only the rows whose first column is 1, then resets the flags

The first column is the point of the thing. Export writes `0` on every row; you set it to `1` on
the rows you edited, and import touches nothing else. That way a 11 600-row file can be edited in
a spreadsheet without the import having to guess what changed, and without a diff of the whole
corpus every time one sentence moves.

Where the texts actually live, and where import writes them back:
  chain      content/chain_*.csv      planet in house, whose house-ruler stands in a house
  placement  content/placement.csv    body in house and sign
  <object>   Kotlin source            Human Design and the older written astrology sets

The Kotlin-backed rows need a fresh dump before either command, because that is the only place
their current text exists:

    bash ./gradlew --offline :app:testDebugUnitTest --tests '*TextCorpusDumpTest*'

After importing astro rows, regenerate the app's JSON assets with `build_assets.py` — it validates
the corpus and will refuse anything that breaks the writing rules.
"""
import csv, glob, os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
CONTENT = HERE + "/content"
KOTLIN_DUMP = HERE + "/kotlin_texts.csv"
ALL = HERE + "/all_texts.csv"
SRC = os.path.abspath(HERE + "/../../app/src/main/java")

COLS = ["changed", "source", "key", "bg", "en"]
CHAIN_COLS = ["planet", "house", "ruler", "ruler_house", "bg", "en"]
PLACEMENT_COLS = ["planet", "house", "sign", "bg", "en"]
csv.field_size_limit(10 ** 7)


def read_rows(path, cols):
    with open(path, encoding="utf-8-sig") as f:
        return [dict(r) for r in csv.DictReader(f)]


def current_corpus():
    """Every text the app ships now, keyed the same way the big file keys them."""
    rows = []
    for path in sorted(glob.glob(CONTENT + "/chain_*.csv")):
        for r in read_rows(path, CHAIN_COLS):
            key = "%s|%s|%s|%s" % (r["planet"], r["house"], r["ruler"], r["ruler_house"])
            rows.append({"source": "chain", "key": key, "bg": r["bg"], "en": r["en"],
                         "file": path})
    for r in read_rows(CONTENT + "/placement.csv", PLACEMENT_COLS):
        key = "%s|%s|%s" % (r["planet"], r["house"], r["sign"])
        rows.append({"source": "placement", "key": key, "bg": r["bg"], "en": r["en"],
                     "file": CONTENT + "/placement.csv"})
    if not os.path.exists(KOTLIN_DUMP):
        sys.exit("!! няма %s — пуснете първо:\n   bash ./gradlew --offline :app:testDebugUnitTest "
                 "--tests '*TextCorpusDumpTest*'" % os.path.basename(KOTLIN_DUMP))
    for r in read_rows(KOTLIN_DUMP, ["source", "key", "bg", "en"]):
        rows.append({"source": r["source"], "key": r["key"], "bg": r["bg"], "en": r["en"],
                     "file": None})
    return rows


def export():
    rows = current_corpus()
    with open(ALL, "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f, quoting=csv.QUOTE_ALL)
        w.writerow(COLS)
        for r in rows:
            w.writerow(["0", r["source"], r["key"], r["bg"], r["en"]])
    by_source = {}
    for r in rows:
        by_source[r["source"]] = by_source.get(r["source"], 0) + 1
    print("%s: %d реда" % (os.path.basename(ALL), len(rows)))
    for s in sorted(by_source, key=lambda s: -by_source[s]):
        print("  %-24s %5d" % (s, by_source[s]))
    print("\nПоставете 1 в първата колона на редовете, които сте променили, и после:"
          "\n   python3 tools/interpretations/corpus.py import")


# ── Kotlin ───────────────────────────────────────────────────────────────────────
def kotlin_files():
    """object name → the file it is declared in."""
    found = {}
    for path in glob.glob(SRC + "/**/*.kt", recursive=True):
        with open(path, encoding="utf-8") as f:
            for m in re.finditer(r"^(?:internal\s+)?object\s+(\w+)", f.read(), re.M):
                found[m.group(1)] = path
    return found


def as_literal(text):
    """The text as it appears inside a Kotlin string literal."""
    return (text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("$", "\\$").replace("\n", "\\n"))


def patch_kotlin(path, old, new):
    """Replace one exact literal. Returns None on success or the reason it could not."""
    with open(path, encoding="utf-8") as f:
        src = f.read()
    for old_form, new_form in ((as_literal(old), as_literal(new)), (old, new)):
        n = src.count(old_form)
        if n == 1:
            with open(path, "w", encoding="utf-8") as f:
                f.write(src.replace(old_form, new_form, 1))
            return None
        if n > 1:
            return "текстът се среща %d пъти в %s" % (n, os.path.basename(path))
    return ("старият текст не е намерен като цял литерал в %s — вероятно е съединен от няколко "
            "части; сменете го ръчно" % os.path.basename(path))


# ── import ──────────────────────────────────────────────────────────────────────
def do_import():
    if not os.path.exists(ALL):
        sys.exit("!! няма %s — пуснете първо `corpus.py export`" % os.path.basename(ALL))
    edited = []
    with open(ALL, encoding="utf-8-sig") as f:
        rows = list(csv.DictReader(f))
    for r in rows:
        if (r.get("changed") or "").strip().lower() in ("1", "x", "да", "yes", "true"):
            edited.append(r)
    if not edited:
        print("няма редове с 1 в първата колона — нищо не е променено")
        return

    current = {(r["source"], r["key"]): r for r in current_corpus()}
    kfiles = kotlin_files()
    touched_files, problems, applied = set(), [], 0

    # Astro rows are grouped per file so each CSV is rewritten once.
    per_file = {}
    for r in edited:
        cur = current.get((r["source"], r["key"]))
        if cur is None:
            problems.append("непознат ключ %s|%s" % (r["source"], r["key"]))
            continue
        if r["bg"] == cur["bg"] and r["en"] == cur["en"]:
            continue                                   # flagged but identical — nothing to do
        if r["source"] in ("chain", "placement"):
            per_file.setdefault(cur["file"], []).append((r["source"], r["key"], r["bg"], r["en"]))
        else:
            path = kfiles.get(r["source"])
            if path is None:
                problems.append("не намирам object %s в кода" % r["source"])
                continue
            for lang in ("bg", "en"):
                if r[lang] == cur[lang]:
                    continue
                why = patch_kotlin(path, cur[lang], r[lang])
                if why:
                    problems.append("%s|%s (%s): %s" % (r["source"], r["key"], lang, why))
                else:
                    applied += 1
                    touched_files.add(path)

    for path, changes in per_file.items():
        cols = CHAIN_COLS if "/chain_" in path else PLACEMENT_COLS
        rows_in = read_rows(path, cols)
        wanted = {(k, ) : (bg, en) for _, k, bg, en in changes}
        for row in rows_in:
            key = ("%s|%s|%s|%s" % (row["planet"], row["house"], row["ruler"], row["ruler_house"])
                   if cols is CHAIN_COLS else
                   "%s|%s|%s" % (row["planet"], row["house"], row["sign"]))
            if (key, ) in wanted:
                row["bg"], row["en"] = wanted[(key, )]
                applied += 1
        with open(path, "w", newline="", encoding="utf-8") as f:
            w = csv.DictWriter(f, fieldnames=cols, quoting=csv.QUOTE_ALL)
            w.writeheader()
            w.writerows(rows_in)
        touched_files.add(path)

    # Only clear the flags that actually landed, so a row that could not be applied stays marked.
    ok_keys = {(r["source"], r["key"]) for r in edited} - {
        tuple(p.split("|")[:2]) for p in problems}
    if applied:
        with open(ALL, "w", newline="", encoding="utf-8") as f:
            w = csv.writer(f, quoting=csv.QUOTE_ALL)
            w.writerow(COLS)
            for r in rows:
                flag = r.get("changed") or "0"
                if (r["source"], r["key"]) in ok_keys:
                    flag = "0"
                w.writerow([flag, r["source"], r["key"], r["bg"], r["en"]])

    print("променени текста: %d, в %d файла" % (applied, len(touched_files)))
    for p in sorted(touched_files):
        print("  " + os.path.relpath(p, os.path.abspath(HERE + "/../..")))
    if problems:
        print("\n!! неприложени:")
        for p in problems:
            print("  " + p)
    if any("/content/" in p for p in touched_files):
        print("\nСега: python3 tools/interpretations/build_assets.py")


if __name__ == "__main__":
    cmd = sys.argv[1] if len(sys.argv) > 1 else "export"
    if cmd == "export":
        export()
    elif cmd == "import":
        do_import()
    else:
        sys.exit(__doc__)
