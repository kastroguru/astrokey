#!/usr/bin/env python3
"""Seed demo charts + events. Robust: keeps the app foreground, navigates by tabs."""
import os, subprocess, sys, time
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from ui import nodes

PKG = 'eu.kastroguru.astrokey'
ACT = PKG + '/eu.kastroguru.astrodiary.MainActivity'

def sh(*a):
    return subprocess.run(list(a), capture_output=True, text=True).stdout
def adb(*a):
    return sh('adb','shell',*a)
def tap_xy(x, y, w=1.2):
    adb('input','tap',str(x),str(y)); time.sleep(w)
def N():
    return nodes()
def by_id(rid, ns=None):
    for n in (ns or N()):
        if n['id'].endswith('/'+rid): return n
def by_text(t, exact=False, ymin=0, ymax=99999, ns=None):
    for n in (ns or N()):
        if not (ymin <= n['cy'] <= ymax): continue
        v = n['text']
        if (v == t) if exact else (t and t in v): return n
def by_desc(d, ns=None):
    for n in (ns or N()):
        if d and d in n['desc']: return n
def tap_id(rid, w=1.5):
    n = by_id(rid)
    if not n: print('  MISS id', rid, flush=True); return False
    tap_xy(n['cx'], n['cy'], w); return True
def tap_text(t, exact=False, w=1.5, ymin=0, ymax=99999):
    n = by_text(t, exact, ymin, ymax)
    if not n: print('  MISS text', t, flush=True); return False
    tap_xy(n['cx'], n['cy'], w); return True
def type_text(s):
    adb('input','text', s); time.sleep(0.8)
def ime_shown():
    return 'mInputShown=true' in adb('dumpsys','input_method')

def esc():
    """Close the soft keyboard — it covers the bottom nav and swallows taps."""
    for _ in range(3):
        if not ime_shown(): return True
        adb('input','keyevent','4'); time.sleep(1.0)
    return not ime_shown()

def clear_field(rid):
    n = by_id(rid)
    if not n: return False
    tap_xy(n['cx'], n['cy'], 0.8)
    adb('input','keyevent','123')                    # move to end
    for _ in range(40):
        adb('input','keyevent','67')                 # delete
    return True

def set_field(rid, value):
    """Type into a field and verify what actually landed there."""
    for attempt in range(3):
        n = by_id(rid)
        if not n: return False
        tap_xy(n['cx'], n['cy'], 0.8)
        type_text(value)
        esc()
        n = by_id(rid)
        if n and n['text'] == value: return True
        print('  retype %s: got %r' % (rid, n['text'] if n else None), flush=True)
        clear_field(rid); esc()
    return False

def focused_ok():
    return PKG in adb('dumpsys','window')

def ensure_app(hard=False):
    if hard or not focused_ok():
        adb('am','force-stop',PKG); time.sleep(1)
        adb('am','start','-n',ACT); time.sleep(5)
    return focused_ok()

def goto_tab(desc):
    for _ in range(3):
        esc()
        n = by_desc(desc)
        if n and n['cy'] > 2100:
            tap_xy(n['cx'], n['cy'], 2.5); return True
        ensure_app(hard=True)
    return False

def pick_date(months_back, day):
    if not tap_id('buttonPickDate', 2): return False
    for _ in range(months_back):
        n = by_id('prev')
        if not n: print('  MISS prev', flush=True); return False
        tap_xy(n['cx'], n['cy'], 0.45)
    n = by_text(str(day), exact=True, ymin=1000, ymax=1620)
    if not n:
        print('  MISS day', day, flush=True); tap_id('button2'); return False
    tap_xy(n['cx'], n['cy'], 1.0)
    return tap_id('button1')

def pick_time(hh, mm):
    if not tap_id('buttonPickTime', 2): return False
    tap_id('toggle_mode', 1.5)
    type_text('%02d%02d' % (hh, mm))
    return tap_id('button1')

def set_city(city):
    if not set_field('editCity', city):
        print('  city field not set:', city, flush=True); return False
    tap_id('buttonSearchCity', 6)
    n = by_text('България')
    if not n:
        ns = N()
        n = by_text(city, ns=ns) or by_text('Sofia', ns=ns)
    if not n:
        print('  MISS city result', city, flush=True); return False
    tap_xy(n['cx'], n['cy'], 2.5)
    return True

def save_form():
    esc()
    if not by_id('buttonSave'):
        adb('input','swipe','540','1800','540','700','400'); time.sleep(1.5)
    ok = tap_id('buttonSave', 9)
    return ok

def add_chart(name, months_back, day, hh, mm, city):
    print('CHART', name, flush=True)
    if not goto_tab('Рождени данни'): return
    if not tap_id('fab', 2.5): return
    set_field('editName', name)
    pick_date(months_back, day)
    pick_time(hh, mm)
    set_city(city)
    save_form()
    goto_tab('Рождени данни')

def add_event(name, months_back, day, hh, mm, city, person=None, glob=False):
    print('EVENT', name, flush=True)
    if not goto_tab('Събития'): return
    if not tap_id('fab', 2.5): return
    set_field('editName', name)
    if person:
        tap_id('text1', 2)
        tap_text(person, w=2)
    pick_date(months_back, day)
    pick_time(hh, mm)
    set_city(city)
    esc()
    adb('input','swipe','540','1800','540','700','400'); time.sleep(1.5)
    if glob:
        n = by_text('Глобално събитие')
        if n: tap_xy(968, n['cy'], 1.0)
    save_form()
    goto_tab('Събития')

ensure_app(hard=True)
add_chart('Ivan', 2, 22, 4, 15, 'Plovdiv')

EVENTS = [
    ('E1',  1, 12,  9, 20, 'Sofia',   'Maria', False),
    ('E2',  3,  5, 18, 45, 'Plovdiv', 'Maria', False),
    ('E3',  6, 21, 14, 10, 'Varna',   'Maria', False),
    ('E4',  9,  8, 11, 30, 'Sofia',   'Ivan',  False),
    ('E5', 14, 17, 20,  5, 'Burgas',  'Ivan',  False),
    ('E6', 20,  2,  8, 40, 'Sofia',   None,    True),
]
for e in EVENTS:
    try:
        add_event(*e)
    except Exception as ex:
        print('  ERROR', e[0], ex, flush=True)
        ensure_app(hard=True)
print('DONE', flush=True)
