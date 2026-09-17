#!/usr/bin/env python3
"""
One workbook holding every written text in the app, and a way back into the code.

    python3 tools/texts/textsheet.py export --out ~/Desktop/astrokey-texts.xlsx
    # ... in LibreOffice: put 1 in "промяна" on the rows you edited, save ...
    python3 tools/texts/textsheet.py import --in ~/Desktop/astrokey-texts.xlsx           # report
    python3 tools/texts/textsheet.py import --in ~/Desktop/astrokey-texts.xlsx --apply   # write

The visible sheet has exactly five columns:

    промяна | етикет | бг | en | брой промени
      0/1     address  text  text   yours to keep

  промяна        1 = "I edited this row, read it and update the app". The cell only accepts 0
                 or 1 (a real spreadsheet dropdown), and the importer refuses anything else.
                 After a successful import every imported row is set back to 0 automatically.
  етикет         the address, in readable form ("Астро · Юпитер в 1 дом · владетел …",
                 "HD висящ гейт · 1"). Do not edit it — it is how a row finds its way home.
  брой промени   never touched by this tool. Yours, to count how often you reworked a text.

A second worksheet, `_адреси`, is hidden and holds where each label lives plus the SHA-1 of the
text as exported. That is what keeps the round trip safe, and it travels inside the same file.

Two kinds of source, both round-trippable:

  csv     — the astrological corpus in tools/interpretations/content/*.csv, addressed by its
            natural key (chain: planet,house,ruler,ruler_house | placement: planet,house,sign).
  kotlin  — texts embedded as string literals in domain/**/*.kt, addressed by the SHA-1 of the
            original literal, not by line number. The importer finds that exact text and
            replaces it; if the file changed underneath, the hash no longer matches and the row
            is refused rather than written to the wrong place.

Only rows flagged 1 are considered, and within those only cells whose text actually differs from
its hash are written. A row flagged 1 with no change is simply reset to 0. A row left at 0 whose
text differs is reported as a possible forgotten flag and left alone.

Escapes: Kotlin literals here use no raw strings and no concatenation (verified), so only
\\" \\\\ \\n \\t \\$ need decoding on the way out and encoding on the way back.
"""
import argparse
import csv
import glob
import hashlib
import os
import re
import shutil
import subprocess
import sys

def _need_openpyxl():
    """The system python3 here ships an openpyxl too old for the installed numpy; python3.11
    has a working one. Re-exec transparently instead of making the caller remember which."""
    try:
        import openpyxl  # noqa: F401
        return
    except Exception:
        pass
    if os.environ.get("TEXTSHEET_REEXEC"):
        sys.exit("openpyxl is not importable — install it for this interpreter")
    for alt in ("python3.11", "python3.12", "python3.10"):
        path = shutil.which(alt)
        if not path:
            continue
        probe = subprocess.run([path, "-c", "import openpyxl"], capture_output=True)
        if probe.returncode == 0:
            os.environ["TEXTSHEET_REEXEC"] = "1"
            os.execv(path, [path] + sys.argv)
    sys.exit("no interpreter with a working openpyxl was found (tried python3.11/3.12/3.10)")


sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from sheet_labels import app_key, csv_key, csv_label, kotlin_label, uniquify  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
CONTENT = os.path.join(ROOT, "tools", "interpretations", "content")
KOTLIN_ROOTS = [os.path.join(ROOT, "app", "src", "main", "java", "eu", "kastroguru", "astrodiary", "domain")]

CHAIN_KEYS = ["planet", "house", "ruler", "ruler_house"]
PLACEMENT_KEYS = ["planet", "house", "sign"]
SHEET = "текстове"
MANIFEST = "_адреси"
COL_FLAG, COL_KEY, COL_LABEL = "промяна", "ключ", "етикет"
COL_BG, COL_EN, COL_COUNT = "бг", "en", "брой промени"
VISIBLE = [COL_FLAG, COL_KEY, COL_LABEL, COL_BG, COL_EN, COL_COUNT]
HIDDEN = ["ключ", "source", "kind", "csv_key", "bg_hash", "en_hash"]
# 1-based positions in the visible sheet
C_FLAG, C_KEY, C_LABEL, C_BG, C_EN, C_COUNT = 1, 2, 3, 4, 5, 6

MIN_TEXT = 60          # shorter literals are keys, labels and glue, not prose

# Two kinds of long literal are code, not content, and must never reach the sheet: a string
# template (editing "${link.planetKey}_..." in a spreadsheet would break the build) and a log
# message. Both are recognised without a per-file blacklist.
SKIP_LITERAL = re.compile(r"\$\{")
SKIP_CONTEXT = re.compile(r"\b(?:Log\.[dewiv]|error|require|check|throw|assert)\s*\($")
csv.field_size_limit(10 ** 7)


def sha(s: str) -> str:
    return hashlib.sha1(s.encode("utf-8")).hexdigest()[:12]


def is_cyrillic(s: str) -> bool:
    cyr = sum(1 for c in s if "Ѐ" <= c <= "ӿ")
    lat = sum(1 for c in s if c.isascii() and c.isalpha())
    return cyr > lat


# ── Kotlin literals ──────────────────────────────────────────────────────────────

DECODE = {'\\"': '"', "\\\\": "\\", "\\n": "\n", "\\t": "\t", "\\$": "$", "\\'": "'"}
LITERAL = re.compile(r'"((?:[^"\\\n]|\\.)*)"')


def decode(raw: str) -> str:
    out, i = [], 0
    while i < len(raw):
        if raw[i] == "\\" and i + 1 < len(raw):
            two = raw[i:i + 2]
            out.append(DECODE.get(two, two))
            i += 2
        else:
            out.append(raw[i])
            i += 1
    return "".join(out)


def encode(text: str) -> str:
    out = []
    for c in text:
        if c == "\\":
            out.append("\\\\")
        elif c == '"':
            out.append('\\"')
        elif c == "\n":
            out.append("\\n")
        elif c == "\t":
            out.append("\\t")
        elif c == "$":
            out.append("\\$")
        else:
            out.append(c)
    return "".join(out)


def kotlin_files():
    for root in KOTLIN_ROOTS:
        for dirpath, _, names in os.walk(root):
            for n in sorted(names):
                if n.endswith(".kt"):
                    yield os.path.join(dirpath, n)


def kotlin_literals(src: str):
    """Every string literal in file order, as (raw, decoded, start, end)."""
    for m in LITERAL.finditer(src):
        yield m.group(1), decode(m.group(1)), m.start(1), m.end(1)


def context_before(src: str, pos: int) -> str:
    """The few characters of code just before a literal — the key, as a human sees it."""
    line_start = src.rfind("\n", 0, pos) + 1
    head = src[line_start:pos].strip()
    if not head or head in ('"', "("):
        prev_start = src.rfind("\n", 0, line_start - 1) + 1
        head = (src[prev_start:line_start].strip() + " " + head).strip()
    head = re.sub(r'"\s*$', "", head).strip()
    return head[-90:]


KEY_LINE = re.compile(
    r"^\s*(?:"
    r"[A-Za-z0-9_.\"]+\s+to\s+[A-Za-z_]*\(?\s*$"        # HdCenter.HEAD to CenterInfo(
    r"|[0-9]+\s+to\s*\(\s*$"                             # 1 to (
    r"|map\[[^\]]*\]\s*=.*$"                             # map[key(9,52)] = "…
    r"|keyFor\([^)]*\)\s+to\s*\(\s*$"                   # keyFor(HdType.X, HdAuthority.Y) to (
    r"|\"[^\"]+\"\s+to\b.*$"                             # "sun_1" to t(
    r"|p\(\"[^\"]+\",.*$"                                # p("sun_sun",
    r")")


def enclosing_key(src: str, pos: int, back: int = 45) -> str:
    """The nearest map key above a literal — what tells one CenterInfo field from another."""
    head = src[:pos].splitlines()
    for line in reversed(head[-back:]):
        if KEY_LINE.match(line):
            return line.strip()
    return ""


def extract_kotlin(path: str):
    """Pair consecutive prose literals as (en, bg): every file here writes English first."""
    src = open(path, encoding="utf-8").read()
    rel = os.path.relpath(path, ROOT)
    prose = [(raw, dec, s, e) for raw, dec, s, e in kotlin_literals(src)
             if len(dec) >= MIN_TEXT
             and not SKIP_LITERAL.search(dec)
             and not SKIP_CONTEXT.search(context_before(src, s))]
    rows, i = [], 0
    while i < len(prose):
        raw_a, a, sa, _ = prose[i]
        if i + 1 < len(prose):
            raw_b, b, sb, _ = prose[i + 1]
            if not is_cyrillic(a) and is_cyrillic(b):
                rows.append(dict(source=rel, kind="kotlin",
                                 key=enclosing_key(src, sa),
                                 context=context_before(src, sa), en=a, bg=b,
                                 en_hash=sha(a), bg_hash=sha(b)))
                i += 2
                continue
        # An unpaired literal — keep it rather than dropping it silently.
        lang = "bg" if is_cyrillic(a) else "en"
        rows.append(dict(source=rel, kind="kotlin",
                         key=enclosing_key(src, sa),
                         context=context_before(src, sa),
                         en="" if lang == "bg" else a, bg=a if lang == "bg" else "",
                         en_hash="" if lang == "bg" else sha(a),
                         bg_hash=sha(a) if lang == "bg" else ""))
        i += 1
    return rows


# ── The astrological CSV corpus ──────────────────────────────────────────────────

def extract_csv():
    rows = []
    for path in sorted(glob.glob(os.path.join(CONTENT, "*.csv"))):
        rel = os.path.relpath(path, ROOT)
        with open(path, encoding="utf-8") as fh:
            reader = csv.DictReader(fh)
            keys = PLACEMENT_KEYS if "sign" in (reader.fieldnames or []) else CHAIN_KEYS
            kind = "placement" if keys is PLACEMENT_KEYS else "chain"
            for r in reader:
                key = "|".join(r[k] for k in keys)
                rows.append(dict(source=rel, kind=kind, key=key, context="",
                                 bg=r["bg"], en=r["en"],
                                 bg_hash=sha(r["bg"]), en_hash=sha(r["en"])))
    return rows


# ── the workbook ─────────────────────────────────────────────────────────────────

def collect():
    """Every text plus a unique app_key (the identifier the code uses) and a readable label."""
    rows = extract_csv()
    for path in kotlin_files():
        rows.extend(extract_kotlin(path))
    raw = [csv_label(r["kind"], r["key"]) if r["kind"] != "kotlin"
           else kotlin_label(r["source"], r["context"], r["key"]) for r in rows]
    for r, lab in zip(rows, uniquify(raw)):
        r["label"] = lab
        r["app_key"] = (csv_key(r["kind"], r["key"]) if r["kind"] != "kotlin"
                        else app_key(r["source"], r["context"], r["key"]))
    keys = [r["app_key"] for r in rows]
    if len(set(keys)) != len(keys):
        seen, bad = set(), []
        for k in keys:
            if k in seen:
                bad.append(k)
            seen.add(k)
        sys.exit(f"app keys are not unique — {len(bad)} collision(s), e.g. {bad[:5]}")
    return rows


def do_export(out_path: str):
    _need_openpyxl()
    from openpyxl import Workbook
    from openpyxl.styles import Alignment, Font, PatternFill
    from openpyxl.worksheet.datavalidation import DataValidation

    rows = collect()
    wb = Workbook()
    ws = wb.active
    ws.title = SHEET
    ws.append(VISIBLE)
    for r in rows:
        ws.append([0, r["app_key"], r["label"], r["bg"], r["en"], 0])

    head = Font(bold=True)
    fill = PatternFill("solid", fgColor="EEEEEE")
    for c in ws[1]:
        c.font = head
        c.fill = fill
    ws.freeze_panes = "D2"                      # flag, key and label stay in view
    for col, width in zip("ABCDEF", (9, 40, 46, 90, 90, 14)):
        ws.column_dimensions[col].width = width
    for col in ("A", "F"):
        for c in ws[col][1:]:
            c.alignment = Alignment(horizontal="center")

    # The flag column accepts nothing but 0 and 1.
    dv = DataValidation(type="list", formula1='"0,1"', allow_blank=False, showErrorMessage=True)
    dv.errorTitle, dv.error = "Само 0 или 1", "Сложете 1, ако сте променили реда, иначе 0."
    ws.add_data_validation(dv)
    dv.add(f"A2:A{ws.max_row}")

    man = wb.create_sheet(MANIFEST)
    man.append(HIDDEN)
    for r in rows:
        man.append([r["app_key"], r["source"], r["kind"], r["key"], r["bg_hash"], r["en_hash"]])
    man.sheet_state = "hidden"

    wb.save(out_path)
    by_kind = {}
    for r in rows:
        by_kind[r["kind"]] = by_kind.get(r["kind"], 0) + 1
    print(f"wrote {len(rows)} rows to {out_path}")
    for k, v in sorted(by_kind.items()):
        print(f"  {k:10s} {v:6d}")
    print(f"  {'texts':10s} {sum(1 for r in rows for c in ('bg', 'en') if r[c]):6d}  (bg + en cells)")
    print(f"  size       {os.path.getsize(out_path)/1e6:.1f} MB")


# ── import ───────────────────────────────────────────────────────────────────────

def _load(in_path):
    from openpyxl import load_workbook
    wb = load_workbook(in_path)
    if SHEET not in wb.sheetnames or MANIFEST not in wb.sheetnames:
        sys.exit(f"{in_path} is not a textsheet workbook (needs sheets '{SHEET}' and '{MANIFEST}')")
    ws, man = wb[SHEET], wb[MANIFEST]
    head = [c.value for c in ws[1]]
    if head[:len(VISIBLE)] != VISIBLE:
        sys.exit(f"unexpected columns {head[:len(VISIBLE)]}; expected {VISIBLE}")
    addr = {}
    for row in man.iter_rows(min_row=2, values_only=True):
        if row and row[0]:
            addr[str(row[0])] = dict(zip(HIDDEN[1:], row[1:]))
    return wb, ws, man, addr


def do_import(in_path: str, apply: bool):
    _need_openpyxl()
    wb, ws, man, addr = _load(in_path)

    edits, refused, forgotten, flagged_rows = [], [], [], []
    # A flagged row whose edit was refused or could not be written must KEEP its 1, so the
    # owner sees it is still outstanding instead of believing it went in.
    problem_rows, row_of_key = set(), {}
    for i in range(2, ws.max_row + 1):
        flag, key, label, bg, en = (ws.cell(i, c).value
                                    for c in (C_FLAG, C_KEY, C_LABEL, C_BG, C_EN))
        if key is None:
            continue
        key, label = str(key), str(label or key)
        row_of_key[key] = i
        a = addr.get(key)
        if a is None:
            refused.append((label, f"ключът «{key}» не е в _адреси — редактиран ли е?"))
            continue
        texts = {"bg": bg or "", "en": en or ""}
        changed = [l for l in ("bg", "en") if a[f"{l}_hash"] and sha(texts[l]) != a[f"{l}_hash"]]

        if str(flag).strip() not in ("0", "1"):
            refused.append((label, f"промяна = {flag!r}; допустими са само 0 и 1"))
            continue
        if str(flag).strip() == "0":
            if changed:
                forgotten.append(label)
            continue

        flagged_rows.append(i)
        if not changed:
            continue
        for l in changed:
            if not texts[l].strip():
                refused.append((label, f"{l} е изтрит до празно"))
                problem_rows.add(i)
                continue
            edits.append((key, label, a, l, texts[l]))

    print(f"{len(flagged_rows)} ред(а) с промяна=1, {len(edits)} променена клетка")
    for label in forgotten[:10]:
        print(f"  ! промяна=0, но текстът е различен — забравен флаг?  {label}")
    if len(forgotten) > 10:
        print(f"  ! … и още {len(forgotten)-10}")
    for label, why in refused:
        print(f"  ОТКАЗАН  {label}: {why}")

    if not apply:
        for _key, label, a, l, text in edits[:15]:
            print(f"  {l}  {label}\n      {text[:110]}")
        if len(edits) > 15:
            print(f"  … и още {len(edits)-15}")
        print("\nнищо не е записано — пуснете отново с --apply")
        return

    per_file = {}
    for key, label, a, l, text in edits:
        per_file.setdefault((a["source"], a["kind"]), []).append((key, label, a, l, text))

    written, failed, done_labels = 0, [], set()
    for (rel, kind), items in sorted(per_file.items()):
        path = os.path.join(ROOT, rel)
        if kind == "kotlin":
            src = open(path, encoding="utf-8").read()
            for key, label, a, l, text in items:
                target = next(((st, en) for _raw, dec, st, en in kotlin_literals(src)
                               if sha(dec) == a[f"{l}_hash"]), None)
                if not target:
                    failed.append((key, l, f"{label}: оригиналният текст не е намерен в {rel}"))
                    continue
                st, en = target
                src = src[:st] + encode(text) + src[en:]
                written += 1
                done_labels.add((key, l, sha(text)))
            open(path, "w", encoding="utf-8").write(src)
        else:
            keys = PLACEMENT_KEYS if kind == "placement" else CHAIN_KEYS
            with open(path, encoding="utf-8") as fh:
                reader = csv.DictReader(fh)
                fields, data = reader.fieldnames, list(reader)
            index = {"|".join(row[k] for k in keys): row for row in data}
            for key, label, a, l, text in items:
                row = index.get(a["csv_key"])
                if row is None or sha(row[l]) != a[f"{l}_hash"]:
                    failed.append((key, l, f"{label}: ключ {a['csv_key']} липсва или е променен в {rel}"))
                    continue
                row[l] = text
                written += 1
                done_labels.add((key, l, sha(text)))
            with open(path, "w", encoding="utf-8", newline="") as fh:
                w = csv.DictWriter(fh, fieldnames=fields, quoting=csv.QUOTE_ALL)
                w.writeheader()
                w.writerows(data)

    # Reset промяна to 0 on everything that went in, and re-hash so the sheet stays the truth.
    new_hash = {(k, l): h for k, l, h in done_labels}
    man_row = {}
    for i in range(2, man.max_row + 1):
        v = man.cell(i, 1).value
        if v:
            man_row[str(v)] = i
    # writes that failed (hash no longer in the file) also keep their flag
    for fkey, _l, _why in failed:
        if fkey in row_of_key:
            problem_rows.add(row_of_key[fkey])

    reset = 0
    for i in flagged_rows:
        if i in problem_rows:
            continue
        key = str(ws.cell(i, C_KEY).value)
        ws.cell(i, C_FLAG).value = 0
        reset += 1
        mi = man_row.get(key)
        if mi:
            for l, col in (("bg", 5), ("en", 6)):
                if (key, l) in new_hash:
                    man.cell(mi, col).value = new_hash[(key, l)]
    wb.save(in_path)

    print(f"записани {written} клетки в {len(per_file)} файла; промяна върната на 0 за {reset} реда")
    for fkey, l, why in failed:
        print(f"  ПРОВАЛ {l} {why}")
    if written:
        print("\nследва: python3 tools/interpretations/build_assets.py   (ако са пипани астро текстове)")
        print("        bash ./gradlew :app:testDebugUnitTest")


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="cmd", required=True)
    e = sub.add_parser("export")
    e.add_argument("--out", required=True, help="path to the .xlsx to write")
    i = sub.add_parser("import")
    i.add_argument("--in", dest="inp", required=True)
    i.add_argument("--apply", action="store_true", help="actually write; without it, only report")
    a = ap.parse_args()
    if a.cmd == "export":
        do_export(os.path.expanduser(a.out))
    else:
        do_import(os.path.expanduser(a.inp), a.apply)


if __name__ == "__main__":
    main()
