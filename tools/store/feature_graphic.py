"""Builds the Google Play feature graphic (1024x500) for Astro Key in English and Bulgarian.

Renders an HTML page with headless Chrome at 2x, then downsamples to exactly 1024x500 RGB.
The phone shows a real screenshot from the app; the zodiac ring uses the app's own glyph shapes
(ported from ui/chart/ZodiacGlyphs.kt), never the emoji.

Inputs, next to this script: {lang}_chart.png (natal chart screen) and {lang}_hd.png (Human Design
screen), 1080x2400 captures from the API 36 emulator in that app language, plus store_icon.png
(the 512 px Play icon). Output: astrokey_feature_{lang}.png, 1024x500 RGB, no alpha.
Needs google-chrome (headless) and network for Google Fonts (Montserrat).
Usage: python3 feature_graphic.py [en] [bg]
"""
import math, random, subprocess, sys, os
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))

# ── The app's zodiac glyphs, ported 1:1 from ZodiacGlyphs.draw (s = 1, y down) ──────────────
def glyph(sign):
    x1, x2, x3, yT, yM = -0.42, 0.0, 0.42, -0.28, -0.7
    m_tail = (f"M{x1},{yT} C{x1},{yM} {x2},{yM} {x2},{yT} L{x2},0.12 "
              f"M{x2},{yT} C{x2},{yM} {x3},{yM} {x3},{yT} L{x3},0.4 "
              f"C{x3-0.12},0.56 {x3+0.35},0.56 {x3+0.35},0.3 C{x3+0.35},0.1 {x3},0.1 {x3},0.12")
    ae = x3 + 0.42
    return {
        "aries": "M0,0 L0,0.45 M0,0 A0.25,0.4 0 0 0 -0.5,0 M0,0 A0.25,0.4 0 0 1 0.5,0",
        "taurus": "M-0.4,0.15 A0.4,0.4 0 1 0 0.4,0.15 A0.4,0.4 0 1 0 -0.4,0.15 "
                  "M-0.28,-0.22 Q-0.55,-0.15 -0.45,-0.5 M0.28,-0.22 Q0.55,-0.15 0.45,-0.5",
        "gemini": "M-0.22,-0.45 L-0.22,0.45 M0.22,-0.45 L0.22,0.45 M-0.44,-0.45 L0.44,-0.45 M-0.44,0.45 L0.44,0.45",
        "cancer": "M-0.36,-0.26 A0.18,0.18 0 1 0 0,-0.26 A0.18,0.18 0 1 0 -0.36,-0.26 "
                  "M-0.36,-0.26 A0.36,0.36 0 0 1 0.36,-0.26 "
                  "M0,0.26 A0.18,0.18 0 1 0 0.36,0.26 A0.18,0.18 0 1 0 0,0.26 "
                  "M-0.36,0.26 A0.36,0.36 0 0 0 0.36,0.26",
        "leo": "M-0.28,0.22 A0.22,0.22 0 1 0 0.16,0.22 A0.22,0.22 0 1 0 -0.28,0.22 "
               "M0.16,0.22 C0.52,-0.62 0.66,0.15 0.44,0.5",
        "virgo": f"M{x1},{yT} L{x1},0.12 " + m_tail,
        "libra": "M-0.42,0 C-0.42,-0.55 0.42,-0.55 0.42,0 M-0.62,0 L0.62,0 M-0.58,0.32 L0.58,0.32",
        "scorpio": (f"M{x1},{yT} L{x1},0.1 M{x1},{yT} C{x1},{yM} {x2},{yM} {x2},{yT} L{x2},0.1 "
                    f"M{x2},{yT} C{x2},{yM} {x3},{yM} {x3},{yT} L{x3},0.35 L{ae},0.35 "
                    f"M{ae-0.2},0.18 L{ae},0.35 L{ae-0.2},0.52"),
        "sagittarius": "M-0.38,0.38 L0.42,-0.42 M0.42,-0.42 L0.15,-0.42 M0.42,-0.42 L0.42,-0.15 M-0.18,-0.18 L0.18,0.18",
        "capricorn": m_tail,
        "aquarius": " ".join(f"M-0.5,{y} Q-0.25,{y-0.24} 0,{y} Q0.25,{y+0.24} 0.5,{y}" for y in (-0.18, 0.18)),
        "pisces": "M-0.48,-0.38 A0.38,0.38 0 0 1 -0.48,0.38 M0.48,-0.38 A0.38,0.38 0 0 0 0.48,0.38 M-0.58,0 L0.58,0",
    }[sign]

SIGNS = ["aries", "taurus", "gemini", "cancer", "leo", "virgo",
         "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces"]


def ring_svg(cx, cy, r_out, r_in, r_core):
    """Decorative gold zodiac ring: two bands, our glyphs between them, ticks, a constellation core."""
    g = []
    gold = "url(#gold)"
    g.append(f'<circle cx="{cx}" cy="{cy}" r="{r_out}" stroke="{gold}" stroke-width="2.2" fill="none"/>')
    g.append(f'<circle cx="{cx}" cy="{cy}" r="{r_out-6}" stroke="{gold}" stroke-width="0.7" fill="none" opacity=".7"/>')
    g.append(f'<circle cx="{cx}" cy="{cy}" r="{r_in}" stroke="{gold}" stroke-width="1.6" fill="none"/>')
    g.append(f'<circle cx="{cx}" cy="{cy}" r="{r_core}" stroke="{gold}" stroke-width="0.9" fill="none" opacity=".75"/>')
    for d in range(0, 360, 5):  # degree ticks on the inner edge of the sign band
        a = math.radians(d)
        long_ = d % 30 == 0
        r1 = r_in if long_ else r_in
        r2 = r_out - 6 if long_ else r_in + (9 if d % 10 == 0 else 5)
        g.append(f'<line x1="{cx+r1*math.cos(a):.2f}" y1="{cy+r1*math.sin(a):.2f}" '
                 f'x2="{cx+r2*math.cos(a):.2f}" y2="{cy+r2*math.sin(a):.2f}" stroke="{gold}" '
                 f'stroke-width="{1.4 if long_ else 0.8}" opacity="{0.95 if long_ else 0.6}"/>')
    # Signs counter-clockwise from the left (Aries rising), as on a chart.
    rg = (r_out - 6 + r_in) / 2
    s = (r_out - 6 - r_in) * 0.30
    for i, sign in enumerate(SIGNS):
        a = math.radians(180 + (i * 30 + 15))  # downward from the left, as on a chart
        x, y = cx + rg * math.cos(a), cy - rg * math.sin(a)
        g.append(f'<path d="{glyph(sign)}" transform="translate({x:.2f},{y:.2f}) scale({s:.2f})" '
                 f'stroke="#F6D36B" stroke-width="{0.17:.2f}" fill="none" stroke-linecap="round" '
                 f'stroke-linejoin="round"/>')
    # Constellation core: points on the core circle joined like aspect lines.
    rnd = random.Random(7)
    pts = [math.radians(d) for d in (12, 75, 131, 160, 214, 263, 302, 338)]
    xy = [(cx + r_core * math.cos(a), cy + r_core * math.sin(a)) for a in pts]
    pairs = [(0, 3), (0, 5), (1, 4), (1, 6), (2, 5), (2, 7), (3, 6), (4, 7), (0, 2)]
    for i, j in pairs:
        g.append(f'<line x1="{xy[i][0]:.1f}" y1="{xy[i][1]:.1f}" x2="{xy[j][0]:.1f}" y2="{xy[j][1]:.1f}" '
                 f'stroke="#F6D36B" stroke-width="0.8" opacity="{rnd.uniform(.25,.55):.2f}"/>')
    for (x, y) in xy:
        g.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="2.4" fill="#FFE8A3"/>')
    return "\n".join(g)


def stars_svg(w, h, seed=3):
    rnd = random.Random(seed)
    out = []
    for _ in range(170):
        x, y = rnd.uniform(0, w), rnd.uniform(0, h)
        r = rnd.choice([0.5, 0.6, 0.7, 0.8, 1.0, 1.2])
        out.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="{r}" fill="#fff" opacity="{rnd.uniform(.25,.85):.2f}"/>')
    for (x, y, k) in [(140, 70, 1.0), (470, 60, .8), (560, 430, .9), (980, 90, .7), (40, 400, .7), (880, 460, .8), (330, 455, .6)]:
        out.append(f'<g transform="translate({x},{y}) scale({k})" opacity=".9">'
                   f'<path d="M0,-9 L1.2,-1.2 L9,0 L1.2,1.2 L0,9 L-1.2,1.2 L-9,0 L-1.2,-1.2 Z" fill="#FFF6D5"/>'
                   f'<circle r="2.2" fill="#fff"/></g>')
    return "\n".join(out)


TEXT = {
    "en": dict(a="Astro", b="Key",
               l1="Natal chart · Transits", l2="Human Design · Synastry",
               v="Your sky, explained in plain words"),
    "bg": dict(a="Астро", b="Ключ",
               l1="Натален хороскоп · Транзити", l2="Хюман дизайн · Синастрия",
               v="Вашето небе, обяснено с прости думи"),
}


def prepare_screen(src, dst, top=100, bottom=2190):
    im = Image.open(src).convert("RGB").crop((0, top, 1080, bottom))
    im.save(dst)


def page(lang, front, back):
    t = TEXT[lang]
    W, H = 1024, 500
    ring = ring_svg(705, 250, 285, 228, 150)
    return f"""<!doctype html><html><head><meta charset="utf-8">
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,500;0,600;0,700;0,800;0,900;1,500&display=block" rel="stylesheet">
<style>
*{{margin:0;padding:0;box-sizing:border-box}}
html,body{{width:{W}px;height:{H}px;overflow:hidden;background:#07061A}}
.stage{{position:relative;width:{W}px;height:{H}px;overflow:hidden;font-family:Montserrat,sans-serif;
  background:
   radial-gradient(ellipse 420px 330px at 705px 250px, rgba(124,92,255,.55), rgba(70,40,170,.18) 55%, transparent 75%),
   radial-gradient(ellipse 360px 260px at 170px 110px, rgba(255,190,70,.13), transparent 70%),
   radial-gradient(ellipse 500px 300px at 120px 520px, rgba(90,40,170,.35), transparent 70%),
   linear-gradient(120deg,#06051A 0%,#0E0A2C 45%,#1B1046 100%);}}
svg.layer{{position:absolute;inset:0}}
.ring{{opacity:.62;filter:drop-shadow(0 0 6px rgba(255,205,90,.45))}}
.phone{{position:absolute;border-radius:20px;
  box-shadow:0 0 0 1.2px rgba(255,214,120,.6),0 26px 60px rgba(0,0,0,.6),0 0 70px rgba(150,120,255,.45)}}
.screen{{position:relative;border-radius:20px;overflow:hidden;background:#E8E6F5;height:100%}}
.screen img{{display:block;width:100%}}
.sb{{height:15px;background:#E8E6F5;display:flex;align-items:center;justify-content:space-between;padding:0 16px;
  font:600 8px Montserrat;color:#333}}
.cam{{position:absolute;top:5px;left:50%;width:7px;height:7px;margin-left:-3.5px;border-radius:50%;background:#111}}
.front{{left:598px;top:34px;width:232px;height:432px}}
.back{{left:806px;top:70px;width:196px;height:380px;transform:rotate(6deg);transform-origin:50% 0;opacity:.96}}
.back::after{{content:"";position:absolute;inset:0;border-radius:20px;background:linear-gradient(90deg,rgba(7,6,26,.12),rgba(7,6,26,0))}}
.copy{{position:absolute;left:92px;top:96px;width:470px}}
.brand{{display:flex;align-items:center;gap:16px}}
.icon{{width:70px;height:70px;border-radius:50%;box-shadow:0 0 0 1.5px rgba(255,214,120,.65),0 0 28px rgba(255,200,80,.45)}}
.word{{font-weight:900;font-size:{68 if lang=='en' else 57}px;line-height:1;letter-spacing:-1.5px;color:#fff;white-space:nowrap;
  text-shadow:0 2px 18px rgba(0,0,0,.45)}}
.word b{{position:relative;font-weight:900;color:#FFD54A;background:linear-gradient(180deg,#FFF4C2 0%,#FFE27A 35%,#FFCB3D 70%,#F7B21C 100%);-webkit-background-clip:text;background-clip:text;-webkit-text-fill-color:transparent;text-shadow:none}}
.word b::before{{content:attr(data-t);position:absolute;left:0;top:0;z-index:-1;-webkit-text-fill-color:rgba(255,200,60,.0);text-shadow:0 0 22px rgba(255,190,40,.55)}}
.rule{{margin:22px 0 18px;height:2px;width:330px;background:linear-gradient(90deg,#FFD54A,rgba(255,213,74,.0))}}
.feat{{font-weight:700;font-size:{24 if lang=='en' else 22}px;line-height:1.35;color:#EDE8FF;letter-spacing:.2px}}
.feat i{{font-style:normal;color:#FFD54A;padding:0 2px}}
.val{{margin-top:16px;font-weight:500;font-style:italic;font-size:17px;color:#BDB3F2;letter-spacing:.2px}}
</style></head><body><div class="stage">
<svg class="layer" width="{W}" height="{H}"><defs>
 <linearGradient id="gold" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFE9A0"/><stop offset=".5" stop-color="#F2C14E"/><stop offset="1" stop-color="#C98E10"/></linearGradient>
</defs>{stars_svg(W, H)}</svg>
<svg class="layer ring" width="{W}" height="{H}"><defs>
 <linearGradient id="gold" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFE9A0"/><stop offset=".5" stop-color="#F2C14E"/><stop offset="1" stop-color="#C98E10"/></linearGradient>
</defs>{ring}</svg>
<div class="phone back"><div class="screen"><img src="{back}"></div></div>
<div class="phone front"><div class="screen"><img src="{front}"></div></div>
<div class="copy">
 <div class="brand"><img class="icon" src="store_icon.png"><div class="word">{t['a']} <b data-t="{t['b']}">{t['b']}</b></div></div>
 <div class="rule"></div>
 <div class="feat">{t['l1'].replace(' · ', ' <i>·</i> ')}<br>{t['l2'].replace(' · ', ' <i>·</i> ')}</div>
 <div class="val">{t['v']}</div>
</div>
</div></body></html>"""


def render(lang, front_src, back_src):
    prepare_screen(front_src, os.path.join(HERE, f"{lang}_front.png"), top=105, bottom=2190)
    # Human Design: toolbar + bodygraph only — the chart card above it carries a Cyrillic name.
    hd = Image.open(back_src).convert("RGB")
    bar, body = hd.crop((0, 100, 1080, 275)), hd.crop((0, 630, 1080, 2190))
    comp = Image.new("RGB", (1080, bar.height + body.height), (232, 230, 245))
    comp.paste(bar, (0, 0)); comp.paste(body, (0, bar.height))
    comp.save(os.path.join(HERE, f"{lang}_back.png"))
    html = os.path.join(HERE, f"feature_{lang}.html")
    open(html, "w", encoding="utf-8").write(page(lang, f"{lang}_front.png", f"{lang}_back.png"))
    raw = os.path.join(HERE, f"feature_{lang}@2x.png")
    subprocess.run(["google-chrome", "--headless=new", "--disable-gpu", "--hide-scrollbars",
                    "--force-device-scale-factor=2", "--window-size=1024,700",
                    "--allow-file-access-from-files", "--virtual-time-budget=10000",
                    f"--screenshot={raw}", "file://" + html],
                   check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    im = Image.open(raw).convert("RGB").crop((0, 0, 2048, 1000))
    out = os.path.join(HERE, f"astrokey_feature_{lang}.png")
    im.resize((1024, 500), Image.LANCZOS).save(out, optimize=True)
    print(out)


if __name__ == "__main__":
    langs = sys.argv[1:] or ["en", "bg"]
    for lang in langs:
        render(lang, os.path.join(HERE, f"{lang}_chart.png"), os.path.join(HERE, f"{lang}_hd.png"))
