#!/usr/bin/env python3
"""Record scripted app-tour segments off the emulator."""
import os, subprocess, sys, time
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from ui import nodes
OUT = HERE + "/clips"
os.makedirs(OUT, exist_ok=True)
PKG = 'eu.kastroguru.astrokey'
ACT = PKG + '/eu.kastroguru.astrodiary.MainActivity'

def adb(*a): return subprocess.run(['adb','shell']+list(a), capture_output=True, text=True).stdout
def tap(x, y, w=1.2): adb('input','tap',str(x),str(y)); time.sleep(w)
def swipe(x1,y1,x2,y2,ms=900,w=1.4):
    adb('input','swipe',str(x1),str(y1),str(x2),str(y2),str(ms)); time.sleep(w)
def by_text(t, exact=False):
    for n in nodes():
        v = n['text']
        if (v == t) if exact else (t and t in v): return n
def tap_text(t, exact=False, w=1.5):
    n = by_text(t, exact)
    if not n: print('   MISS', t, flush=True); return False
    tap(n['cx'], n['cy'], w); return True
def tab(x, w=3.0): tap(x, 2263, w)
def tap_id(rid, w=1.5):
    for n in nodes():
        if n['id'].endswith('/'+rid):
            tap(n['cx'], n['cy'], w); return True
    print('   MISS id', rid, flush=True); return False

TABS = dict(birth=108, events=323, now=540, transits=755, hd=971)

def record(name, secs, actions):
    dev = '/sdcard/%s.mp4' % name
    adb('rm','-f',dev)
    p = subprocess.Popen(['adb','shell','screenrecord','--bit-rate','16000000',
                          '--time-limit',str(secs),dev],
                         stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    time.sleep(2.0)
    t0 = time.time()
    actions()
    print('   actions took %.1fs of %ds' % (time.time()-t0, secs), flush=True)
    p.wait()
    time.sleep(1.5)
    subprocess.run(['adb','pull',dev,'%s/%s.mp4' % (OUT,name)],
                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    print('   saved', name, flush=True)

# ---------------------------------------------------------------- segments
def seg0():                              # cold start from the home screen
    adb('am','start','-n',ACT); time.sleep(9)

def seg1():                              # natal charts: detail -> wheel -> table
    time.sleep(1.0)
    tap(300, 656, 3.5)                   # Мария
    time.sleep(1.0)
    tap_id('buttonViewChart', 4.5)       # ХОРОСКОП -> wheel
    time.sleep(1.5)
    for _ in range(2): swipe(540,1900,540,1100,1200)
    time.sleep(1.5)
    for _ in range(2): swipe(540,1100,540,1900,1200)
    tap(74, 136, 3.0)                    # back to detail
    tap_id('buttonViewTable', 4.0)       # ТАБЛИЦА
    time.sleep(1.0)
    swipe(540,1900,540,1200,1200)
    time.sleep(1.5)
    tap(74, 136, 2.5)
    tap(74, 136, 2.5)                    # back to list
    tap(300, 350, 3.5)                   # Иван
    tap_id('buttonViewChart', 4.5)
    time.sleep(2.5)

def seg2():                              # events gallery + filters
    time.sleep(1.5)
    tap(275, 289, 2.0); tap_text('Мария', w=3.0)          # person filter
    time.sleep(2.0)
    tap(805, 289, 2.0)                                     # tags dialog
    tap_text('море', w=1.4)
    tap_id('button1', 3.0)                                 # ДОБРЕ
    time.sleep(2.5)
    tap(805, 289, 2.0)                                     # clear tags
    tap_id('button3', 3.0)                                 # БЕЗ ТАГОВЕ
    tap(275, 289, 2.0); tap_text('Всички личности', w=3.0)
    time.sleep(1.0)
    tap(196, 614, 4.0)                                     # open first event
    time.sleep(1.5)
    for _ in range(2): swipe(540,1900,540,1100,1100)
    time.sleep(1.5)

def seg3():                              # Now
    time.sleep(1.5)
    tap(466, 687, 1.2)                   # step = ДЕН
    for _ in range(3): tap(805, 687, 1.4)
    time.sleep(1.0)
    tap(947, 687, 2.0)                   # back to now
    for _ in range(2): swipe(540,1900,540,1100,1100)
    time.sleep(1.5)

def seg4():                              # transits + primary directions
    time.sleep(1.5)
    tap(540, 395, 2.0); tap_text('Мария', w=4.0)           # natal chart
    time.sleep(2.0)
    for _ in range(2): swipe(540,1900,540,1100,1100)
    time.sleep(2.0)
    for _ in range(2): swipe(540,1100,540,1900,1100)
    tap(400, 623, 2.0)                                     # mode spinner
    tap_text('Примарни дирекции', w=8.0)
    time.sleep(4.0)
    for _ in range(2): swipe(540,1900,540,1100,1100)
    time.sleep(2.0)

def seg5():                              # human design
    time.sleep(1.5)
    for _ in range(3): swipe(540,1900,540,1200,1200)
    time.sleep(1.5)
    for _ in range(2): swipe(540,1200,540,1900,1100)
    time.sleep(1.0)

PLAN = [
    ('seg0_launch',   14, seg0,  'kill'),
    ('seg1_charts',   58, seg1,  'birth'),
    ('seg2_events',   50, seg2,  'events'),
    ('seg3_now',      26, seg3,  'now'),
    ('seg4_transits', 52, seg4,  'transits'),
    ('seg5_design',   26, seg5,  'hd'),
]
only = sys.argv[1:] or None
for name, secs, fn, start in PLAN:
    if only and name not in only: continue
    print('REC', name, flush=True)
    if start == 'kill':
        adb('input','keyevent','3')                      # home screen, nothing else on top
        time.sleep(2)
        adb('am','force-stop',PKG); time.sleep(2)
    else:
        adb('am','force-stop',PKG); time.sleep(1.5)      # always start from a clean root
        adb('am','start','-n',ACT); time.sleep(7)
        tab(TABS[start], 3.0)
        adb('input','swipe','540','900','540','2000','300'); time.sleep(1.5)  # scroll to top
    record(name, secs, fn)
print('ALL DONE', flush=True)
