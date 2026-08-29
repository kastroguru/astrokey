#!/usr/bin/env python3
"""Tiny uiautomator helper: dump the view tree, find nodes, tap them."""
import re, subprocess, sys, time, xml.etree.ElementTree as ET

def sh(*a):
    return subprocess.run(a, capture_output=True, text=True).stdout

def dump():
    subprocess.run(['adb','shell','uiautomator','dump','/sdcard/ui.xml'],
                   capture_output=True, text=True)
    return sh('adb','exec-out','cat','/sdcard/ui.xml')

def nodes():
    xml = dump()
    xml = xml[xml.index('<?xml'):]
    out = []
    for n in ET.fromstring(xml).iter('node'):
        b = re.findall(r'-?\d+', n.get('bounds',''))
        if len(b) != 4: continue
        x1,y1,x2,y2 = map(int,b)
        out.append({'text': n.get('text',''), 'desc': n.get('content-desc',''),
                    'id': n.get('resource-id',''), 'cls': n.get('class',''),
                    'cx': (x1+x2)//2, 'cy': (y1+y2)//2,
                    'bounds': (x1,y1,x2,y2)})
    return out

def find(pat, field='text', exact=False):
    for n in nodes():
        v = n[field]
        if (v == pat) if exact else (pat and pat in v):
            return n
    return None

def tap(pat, field='text', exact=False, wait=1.5):
    n = find(pat, field, exact)
    if not n:
        print(f'MISS: {pat!r}'); return False
    subprocess.run(['adb','shell','input','tap',str(n['cx']),str(n['cy'])])
    time.sleep(wait)
    print(f"tapped {pat!r} at {n['cx']},{n['cy']}")
    return True

if __name__ == '__main__':
    cmd = sys.argv[1]
    if cmd == 'ls':
        for n in nodes():
            if n['text'] or n['desc'] or 'Button' in n['cls'] or 'Edit' in n['cls']:
                print(f"{n['bounds']}  txt={n['text']!r} desc={n['desc']!r} id={n['id'].split('/')[-1]} cls={n['cls'].split('.')[-1]}")
    elif cmd == 'tap':
        ok = tap(sys.argv[2], exact=(len(sys.argv)>3 and sys.argv[3]=='exact'))
        sys.exit(0 if ok else 1)
