# Astro Key / Астро Ключ

A personal astrology diary for Android — fully offline, no accounts, no servers.
Личен астрологически дневник за Android — изцяло офлайн, без акаунти и сървъри.

**Google Play:** Astro Key (`eu.kastroguru.astrokey`)

## Features

- **Natal charts** — 13 bodies, 12 house cusps, aspects, dignities. Calculated with Swiss Ephemeris.
- **Transits** — live planetary positions vs. the natal chart, with time navigation and an aspectarian view.
- **Primary directions** — Placidus semi-arc method with the True Solar Equatorial Arc time key, verified against Morinus. Shown as a dedicated biwheel with directed (direct + converse) points.
- **Human Design** — full bodygraph with types, centers, gates and channels.
- **Event diary** — record life events with their full astrological chart.
- House systems: Placidus, Whole Sign, Koch, Equal, Regiomontanus, Porphyry.
- Languages: Bulgarian, English.

All data stays on the device (local SQLite database). Nothing is ever uploaded.

## Building

Standard Android Gradle project:

```bash
./gradlew :app:assembleDebug
```

Requirements: JDK 17, Android SDK (compileSdk 35). Open in Android Studio or build from the CLI.
The Swiss Ephemeris data files (`app/src/main/assets/ephe/*.se1`) are bundled.

Release builds expect a `keystore.properties` file (not in the repo) — see `app/build.gradle.kts`.

## License

This project is licensed under the **GNU Affero General Public License v3.0** — see [LICENSE](LICENSE).

It uses the [Swiss Ephemeris](https://www.astro.com/swisseph/) by Astrodienst AG (via
[Thomas Mack's Java port](https://github.com/krishnact/swisseph)), which is dual-licensed
under the AGPL and a commercial license. This app complies via the AGPL: the complete
source code is publicly available in this repository.

© Krasen Dimkov (Kastro Guru). Contact: kastroguru@gmail.com
