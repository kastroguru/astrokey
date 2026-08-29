#!/usr/bin/env python3
"""Compose the silent Astro Key promo video from the recorded app segments."""
import math, os, subprocess, sys
from PIL import Image, ImageDraw, ImageFont, ImageFilter

SP   = os.path.dirname(os.path.abspath(__file__))
CLIP = SP + '/clips'
WORK = SP + '/work'
os.makedirs(WORK, exist_ok=True)

W, H = 1920, 1080
FPS  = 30

BG_TOP   = (11, 10, 23)
BG_BOT   = (30, 22, 64)
VIOLET   = (155, 140, 255)
VIOLET_D = (110, 95, 192)
YELLOW   = (255, 212, 0)
TEXT     = (238, 236, 248)
MUTED    = (176, 170, 205)
FOOT     = (110, 104, 145)

F_TITLE  = '/usr/share/fonts/opentype/cantarell/Cantarell-ExtraBold.otf'
F_BOLD   = '/usr/share/fonts/opentype/cantarell/Cantarell-Bold.otf'
F_REG    = '/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf'

def font(path, size): return ImageFont.truetype(path, size)

# phone screen window (the video shows through this hole)
PX, PY, PW, PH = 1306, 50, 452, 1004
RAD = 40

def gradient_bg():
    bg = Image.new('RGB', (W, H))
    d  = ImageDraw.Draw(bg)
    for y in range(H):
        t = y / (H - 1)
        d.line([(0, y), (W, y)], fill=tuple(
            int(BG_TOP[i] + (BG_BOT[i] - BG_TOP[i]) * t) for i in range(3)))
    # soft violet glow behind the phone
    glow = Image.new('L', (W, H), 0)
    ImageDraw.Draw(glow).ellipse(
        [PX - 330, PY - 200, PX + PW + 330, PY + PH + 200], fill=90)
    glow = glow.filter(ImageFilter.GaussianBlur(160))
    bg = Image.composite(Image.new('RGB', (W, H), (86, 68, 168)), bg, glow)
    # faint decorative rings, bottom left
    ring = Image.new('RGBA', (W, H), (0, 0, 0, 0))
    rd = ImageDraw.Draw(ring)
    for r, a in ((520, 26), (700, 18), (880, 12)):
        rd.ellipse([170 - r, 980 - r, 170 + r, 980 + r], outline=(160, 145, 255, a), width=2)
    bg = Image.alpha_composite(bg.convert('RGBA'), ring)
    return bg

BASE = gradient_bg()

def spaced(draw, xy, text, fnt, fill, extra=7):
    x, y = xy
    for ch in text:
        draw.text((x, y), ch, font=fnt, fill=fill)
        x += draw.textlength(ch, font=fnt) + extra
    return x

def wrap(draw, text, fnt, maxw):
    words, lines, cur = text.split(), [], ''
    for w_ in words:
        t = (cur + ' ' + w_).strip()
        if draw.textlength(t, font=fnt) <= maxw: cur = t
        else: lines.append(cur); cur = w_
    if cur: lines.append(cur)
    return lines

def phone_frame(img):
    """Punch a rounded hole for the video and draw the device bezel around it."""
    d = ImageDraw.Draw(img)
    for i, (col, wdt) in enumerate(((VIOLET_D + (200,), 8), ((40, 32, 78, 255), 3))):
        off = -10 - i * 6
        d.rounded_rectangle([PX + off, PY + off, PX + PW - off, PY + PH - off],
                            radius=RAD + abs(off), outline=col, width=wdt)
    hole = Image.new('L', (W, H), 255)
    ImageDraw.Draw(hole).rounded_rectangle([PX, PY, PX + PW, PY + PH], radius=RAD, fill=0)
    img.putalpha(hole)
    return img

def footer(d):
    f = font(F_REG, 26)
    d.text((120, H - 78), 'ASTRO KEY  ·  KASTRO GURU', font=f, fill=FOOT)

def overlay(kicker, title, bullets, out):
    img = BASE.copy()
    d = ImageDraw.Draw(img)
    x = 120
    y = 300
    if kicker:
        spaced(d, (x, y), kicker.upper(), font(F_BOLD, 32), VIOLET, 8)
        y += 78
    for line in wrap(d, title, font(F_TITLE, 82), 1080):
        d.text((x, y), line, font=font(F_TITLE, 82), fill=TEXT); y += 100
    y += 34
    fb = font(F_REG, 37)
    for b in bullets:
        d.ellipse([x + 4, y + 17, x + 18, y + 31], fill=YELLOW)
        for i, line in enumerate(wrap(d, b, fb, 1010)):
            d.text((x + 44, y), line, font=fb, fill=MUTED); y += 52
        y += 22
    footer(d)
    phone_frame(img).save(out)

def card(out, title, lines, big=False, icon=None, accent=YELLOW):
    img = BASE.copy()
    d = ImageDraw.Draw(img)
    cy = 300
    if icon:
        ic = Image.open(icon).convert('RGBA').resize((250, 250))
        mask = Image.new('L', (250, 250), 0)
        ImageDraw.Draw(mask).ellipse([0, 0, 249, 249], fill=255)
        ic.putalpha(mask)
        img.alpha_composite(ic, (W // 2 - 125, 190))
        cy = 500
    ft = font(F_TITLE, 132 if big else 96)
    for line in wrap(d, title, ft, 1500):
        tw = d.textlength(line, font=ft)
        d.text(((W - tw) / 2, cy), line, font=ft, fill=TEXT); cy += (150 if big else 112)
    cy += 36
    for i, (txt, size, col) in enumerate(lines):
        f = font(F_REG, size)
        for line in wrap(d, txt, f, 1400):
            tw = d.textlength(line, font=f)
            d.text(((W - tw) / 2, cy), line, font=f, fill=col); cy += size + 22
        cy += 18
    d.rounded_rectangle([W // 2 - 190, cy + 20, W // 2 + 190, cy + 26], radius=3, fill=accent)
    footer(d)
    img.convert('RGB').save(out)

# ------------------------------------------------------------------ content
SEGMENTS = [
    dict(kind='card', name='c0_intro', dur=5.5, title='Астро Ключ',
         icon=SP + '/icon_launcher.png', big=True,
         lines=[('Личен астрологичен дневник за Android', 46, MUTED),
                ('Натален хороскоп · Събития · Транзити · Дирекции · Хюман дизайн', 34, FOOT)]),

    dict(kind='clip', name='s00', clip='seg0_launch', ss=1.9, dur=3.0,
         kicker='Приложението', title='Отваря се и работи офлайн',
         bullets=['Без регистрация и без акаунт — всичко се смята на телефона.',
                  'Интернет трябва само при търсене на град.']),

    dict(kind='clip', name='s01', clip='seg1_charts', ss=1.5, dur=20.5,
         kicker='Рождени карти', title='Въвеждате дата, час и град',
         bullets=['Пълна карта, изчислена със Swiss Ephemeris.',
                  'Колело на хороскопа със знаци, домове и аспекти.',
                  'Толкова карти, колкото поискате — за цялото семейство.']),

    dict(kind='clip', name='s02', clip='seg1_charts', ss=34.0, dur=12.0,
         kicker='Позиции', title='Таблица на планетите',
         bullets=['От Слънце до Плутон, плюс Хирон, Раху и Лилит.',
                  'Знак, градус, дом и куспиди на домовете.',
                  'Отделна таблица с аспектите.']),

    dict(kind='clip', name='s03', clip='seg2_events', ss=2.0, dur=11.0,
         kicker='Дневник', title='Събитията от живота ви',
         bullets=['Галерия в три колони — като в Instagram.',
                  'Всяко квадратче показва най-точния аспект за момента на събитието.',
                  'Отдолу: слънчев и лунен знак, град, дата и час.']),

    dict(kind='clip', name='s04', clip='seg2_events', ss=13.0, dur=14.0,
         kicker='Филтри', title='Търсите по личност и по таг',
         bullets=['Изберете чии събития да виждате.',
                  'Комбинирайте няколко тага наведнъж.',
                  'Задържане върху събитие го изтрива — с възможност за връщане.']),

    dict(kind='clip', name='s05', clip='seg2_events', ss=41.0, dur=8.5,
         kicker='Детайли', title='Всяко събитие има своя карта',
         bullets=['Описание, тагове и връзка с личност.',
                  'Може да качите и собствена снимка.',
                  'Хороскоп за точния момент на събитието.']),

    dict(kind='clip', name='s06', clip='seg3_now', ss=1.0, dur=14.5,
         kicker='Сега', title='Небето в този момент',
         bullets=['Текущи позиции на всички планети.',
                  'Стъпка напред и назад: час, ден, седмица.',
                  'Връщане към „сега“ с един бутон.']),

    dict(kind='clip', name='s07', clip='seg4_transits', ss=1.5, dur=20.5,
         kicker='Транзити', title='Транзити към вашата карта',
         bullets=['Избирате коя натална карта да следите.',
                  'Списък с активните аспекти, орб и посока.',
                  'Приближаващи и отдалечаващи се аспекти са отбелязани.']),

    dict(kind='clip', name='s08', clip='seg4_transits', ss=26.0, dur=19.0,
         kicker='Примарни дирекции', title='Дирекции по Placidus',
         bullets=['Полудъгов метод на Placidus, ключ True Solar Equatorial Arc.',
                  'Двойно колело: наталната карта плюс дирекциите.',
                  'Рядко се среща в мобилно приложение.']),

    dict(kind='clip', name='s09', clip='seg5_design', ss=1.0, dur=15.5,
         kicker='Хюман дизайн', title='Пълен бодиграф',
         bullets=['Центрове, канали и портали от същите изчисления.',
                  'Тип, стратегия, авторитет, профил и дефиниция.']),

    dict(kind='card', name='c1_outro', dur=9.0, title='Имате въпроси?',
         lines=[('Напишете ги в коментарите под видеото — ще отговоря на всеки.', 46, TEXT),
                ('Кажете ми и какво не работи, какво липсва и какво да добавя.', 44, MUTED),
                ('Astro Key · Google Play · eu.kastroguru.astrokey', 34, FOOT)]),
]

def run(cmd):
    p = subprocess.run(cmd, capture_output=True, text=True)
    if p.returncode:
        print(' '.join(cmd[:9]), '...\n', p.stderr[-1500:]); sys.exit(1)

def normalize(clip):
    """screenrecord output is variable-frame-rate; make it CFR so trims are exact."""
    src = '%s/%s.mp4' % (CLIP, clip)
    dst = '%s/norm_%s.mp4' % (WORK, clip)
    if not os.path.exists(dst):
        run(['ffmpeg','-v','error','-i',src,
             '-vf','fps=%d,setpts=PTS-STARTPTS,scale=%d:%d' % (FPS, PW, PH),
             '-c:v','libx264','-preset','fast','-crf','16','-an','-y',dst])
        print('normalized', clip, flush=True)
    return dst

for c in sorted({s['clip'] for s in SEGMENTS if s['kind'] == 'clip'}):
    normalize(c)

parts = []
for s in SEGMENTS:
    png = '%s/%s.png' % (WORK, s['name'])
    mp4 = '%s/%s.mp4' % (WORK, s['name'])
    fade = 'fade=t=in:st=0:d=0.45,fade=t=out:st=%.2f:d=0.5' % (s['dur'] - 0.5)
    if s['kind'] == 'card':
        card(png, s['title'], s['lines'], big=s.get('big', False), icon=s.get('icon'))
        run(['ffmpeg','-v','error','-loop','1','-t','%.2f' % s['dur'],'-i',png,
             '-vf','scale=%d:%d,%s,format=yuv420p,fps=%d' % (W,H,fade,FPS),
             '-c:v','libx264','-preset','medium','-crf','17','-an','-y',mp4])
    else:
        overlay(s['kicker'], s['title'], s['bullets'], png)
        fc = ('[0:v]setpts=PTS-STARTPTS,fps=%d,scale=%d:%d,setsar=1[v];'
              'color=c=black:s=%dx%d:r=%d[bg];'
              '[bg][v]overlay=x=%d:y=%d:shortest=1[b];'
              '[b][1:v]overlay=0:0,%s,format=yuv420p[o]'
              % (FPS, PW, PH, W, H, FPS, PX, PY, fade))
        run(['ffmpeg','-v','error','-ss','%.2f' % s['ss'],'-t','%.2f' % s['dur'],
             '-i',normalize(s['clip']),'-loop','1','-i',png,
             '-filter_complex',fc,'-map','[o]','-t','%.2f' % s['dur'],
             '-c:v','libx264','-preset','medium','-crf','17','-an','-y',mp4])
    parts.append(mp4)
    print('built', s['name'], flush=True)

with open(WORK + '/list.txt','w') as f:
    for p in parts: f.write("file '%s'\n" % p)
out = SP + '/astro_key_promo_bg.mp4'
run(['ffmpeg','-v','error','-f','concat','-safe','0','-i',WORK + '/list.txt',
     '-c:v','libx264','-preset','slow','-crf','19','-pix_fmt','yuv420p',
     '-movflags','+faststart','-an','-y',out])
print('FINAL', out, flush=True)
