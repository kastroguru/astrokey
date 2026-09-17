package eu.kastroguru.astrodiary.domain.synastry

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * One card in the synastry reading: a heading and a paragraph, each in both languages.
 *
 * Unlike the rest of the written corpus, these texts carry their own heading rather than having
 * one composed from the placement. The synastry texts are written to read as ordinary life and
 * never as astrology — a heading naming the body and the sign above the paragraph would hand the
 * reader back exactly the thing the paragraph is keeping out of the way.
 */
data class SynastryCard(val title: Bilingual, val body: Bilingual)
