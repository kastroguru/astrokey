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
            "How you arrive: the Ascendant shows the first impression you give, the way you enter situations, and the approach you take when something begins. It isn’t a mask but the doorway through which the rest of the chart expresses itself — a pattern of bodily presence and instinctive responses shaped by the moment of birth. The ASC often reveals automatic habits you can become aware of and refine, so noticing it helps you choose when to lean into a first impulse or to adjust your presentation.",
            "Как пристигате: Асцендентът показва първото впечатление, стила на влизане в нови ситуации и начина, по който започвате взаимодействия. Това не е маска, а начинът, по който тялото, поведението и моментът създават „вратата“ към останалата част от картата. Асцендентът може да обрисува повтарящи се реакции и автоматични подходи, които после съзнателно развивате или омекотявате; осъзнаването му често дава контрол върху първия импулс и позволява да подберете как да се представите според целта си."
        ),
        "mc" to t(
            "The chart’s highest point describes your standing before the world — reputation, vocation, or the public role you’re drawn toward. The Midheaven outlines the kinds of responsibilities, authority, or ambitions that shape your outward direction. It speaks more to public life than to private matters: the stage on which your work and status are seen. Life changes can shift how you express your MC, but understanding it helps you steer career choices and public behavior with greater clarity.",
            "Най-високата точка на картата говори за позицията ви пред света — репутацията, кариерата или обществената роля, към която сте привлечени. Меридианът описва какво хората би трябвало да видят: типа отговорности, авторитет или амбиция, която ви тегли. Той не определя интимния живот, а сцената, на която се измерят вашите усилия. Животните обрати могат да изместят фокусa към или от МС, но разбирането му помага да насочвате професионални избори и публични прояви по-осъзнато."
        ),
    )

    fun of(key: String): Bilingual? = byKey[key]
}
