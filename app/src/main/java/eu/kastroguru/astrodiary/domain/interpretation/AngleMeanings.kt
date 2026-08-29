package eu.kastroguru.astrodiary.domain.interpretation

/**
 * The Ascendant and the Midheaven. They are not bodies — they are the two points where the sky meets
 * the exact spot on earth someone was born on, which is why they need their own block: a planet
 * describes a drive, an angle describes a position you are seen from.
 *
 * Keys match the rest of the app ("asc", "mc", as used by EventAspects and the cusp columns).
 */
object AngleMeanings {

    val byKey: Map<String, Bilingual> = mapOf(
        "asc" to t(
            "How you arrive: the impression people form in the first minute, the way you walk into a room, and the approach you reach for when something new starts. It is not a mask — it is the doorway to everything else in the chart, and it is built out of the moment and place you were born, not out of your intentions.",
            "Как пристигате: впечатлението, което хората си съставят в първата минута, начинът, по който влизате в стая, и подходът, към който посягате, когато започва нещо ново. Не е маска — то е вратата към всичко останало в картата и е изградено от момента и мястото на раждането ви, не от намеренията ви."
        ),
        "mc" to t(
            "The highest point of the chart: what you are known for, the direction your work pulls in, and the kind of authority you either answer to or grow into. It describes your standing in public rather than your private life — what strangers would say you are, and what you are aiming at.",
            "Най-високата точка на картата: с какво сте известни, в каква посока тегли работата ви и какъв тип власт или зачитате, или се превръщате в нея. Описва мястото ви пред света, а не личния живот — какво биха казали за вас непознати и към какво сте се насочили."
        ),
    )

    fun of(key: String): Bilingual? = byKey[key]
}
