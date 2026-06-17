#!/usr/bin/env bash
# Publishes the kastro_android source to the public AGPL repo (github.com/kastroguru/astrokey)
# so the published source matches the shipped Google Play binary.
# Copies ONLY app source — excludes build artifacts, keystore, and local config.
set -euo pipefail

SRC="/home/krasem/Desktop/system/ASTRO_MY_TOOLS/AstroDiaryProjectPythonAngular/kastro_android/"
WORK="/home/krasem/Desktop/system/ASTRO_MY_TOOLS/astrokey-public"

# 1. Clone the public repo (or refresh an existing clone)
if [ -d "$WORK/.git" ]; then
  echo ">> refreshing existing clone"
  git -C "$WORK" fetch origin
  git -C "$WORK" reset --hard origin/main
else
  echo ">> cloning public repo"
  rm -rf "$WORK"
  git clone git@github.com:kastroguru/astrokey.git "$WORK"
fi

# 2. Sync source into it (mirror), excluding anything build-related or secret
rsync -a --delete \
  --exclude='.git/' --exclude='build/' --exclude='.gradle/' --exclude='.idea/' \
  --exclude='*.iml' --exclude='keystore.properties' --exclude='local.properties' \
  --exclude='*.keystore' --exclude='*.jks' --exclude='publish-agpl-source.sh' \
  "$SRC" "$WORK/"

# 3. Safety: abort if any secret slipped through
if find "$WORK" -path "$WORK/.git" -prune -o \
     \( -name 'keystore.properties' -o -name 'local.properties' \
        -o -name '*.jks' -o -name '*.keystore' \) -print | grep -q .; then
  echo "!! ABORT: a secret file is present in the snapshot" >&2
  exit 1
fi

# 4. Commit & push
cd "$WORK"
git add -A
if git diff --cached --quiet; then
  echo ">> nothing changed — public repo already matches source"
  exit 0
fi
git commit -m "Snapshot for release v1.1 (versionCode 3)

Matches the Google Play binary (eu.kastroguru.astrokey) for AGPL-3.0
source-availability. Adds event images + gallery, person/tag filters,
richer thumbnails."
git push origin HEAD:main
echo ">> DONE — public source updated to match v3"
