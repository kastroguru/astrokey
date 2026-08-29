package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * The second half of a chain reading: where the matter is actually decided, once per ruler house.
 *
 * Twelve lines that pair with any of the 156 planet-in-house lines, so the 1,872 readings are built
 * from written text rather than composed out of dictionary entries. Light and concrete on purpose —
 * this is the sentence that tells someone what to do about it.
 */
object RulerHouseKey {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val byHouse: Map<Int, Bilingual> = mapOf(
        1 to t(
            "It all starts with how you carry yourself. Change the way you show up and the rest shifts with it; keep the same approach and nothing here will move.",
            "Всичко започва от това как се държите. Смените ли начина, по който се показвате, се мести и останалото; запазите ли същия подход, тук нищо няма да се промени."
        ),
        2 to t(
            "It comes down to money and to what you actually own. Sort that out and this settles; leave it shaky and this stays shaky.",
            "Опира до парите и до това какво наистина имате. Подредите ли това, се подрежда и другото; остане ли клатушкащо се, и тук ще е така."
        ),
        3 to t(
            "It gets decided in conversations and in small daily traffic — who said what to whom, and whether you picked up the phone.",
            "Решава се в разговорите и в дребното ежедневие — кой какво на кого е казал и вдигнали ли сте телефона."
        ),
        4 to t(
            "The root is at home, in the family. That is where it is settled, even when it is the last thing you want to talk about.",
            "Коренът е в дома, в семейството. Там се решава, дори когато точно за това най-малко ви се говори."
        ),
        5 to t(
            "It moves through pleasure and through the nerve to try — if there is no joy in it and no risk taken, it does not get going at all.",
            "Минава през удоволствието и през смелостта да опитате — няма ли радост в него и няма ли поет риск, изобщо не се задвижва."
        ),
        6 to t(
            "It depends on your routine and your health — on the dull things done every day, not on the big decisions.",
            "Зависи от режима и от здравето — от скучните неща, които се правят всеки ден, а не от големите решения."
        ),
        7 to t(
            "It goes through another person. You will not settle this one alone, however much you would prefer to.",
            "Минава през друг човек. Това няма да го решите сами, колкото и да ви се иска."
        ),
        8 to t(
            "It touches other people's money and the things nobody says out loud — debts, inheritances, what is owed and to whom.",
            "Опира до чужди пари и до неща, които никой не изрича на глас — дългове, наследства, кой на кого какво дължи."
        ),
        9 to t(
            "It resolves when you go wider: travel, study, someone else's point of view. Staying in the same circle keeps it stuck.",
            "Решава се, когато излезете по-нашироко: пътуване, учене, чужда гледна точка. Останете ли в същия кръг, стои на място."
        ),
        10 to t(
            "It runs through your work and through how you are seen from outside. Your standing decides it, not your intentions.",
            "Минава през работата и през това как ви виждат отвън. Решава го името ви, не намеренията ви."
        ),
        11 to t(
            "It arrives through friends and through people who want the same thing you do. On your own it takes far longer.",
            "Идва през приятели и през хора, които искат същото като вас. Сами по себе си отнема много повече време."
        ),
        12 to t(
            "It gets settled quietly, on your own — not on the move and not in front of an audience.",
            "Решава се на тихо и насаме — не в движение и не пред публика."
        ),
    )

    fun of(house: Int): Bilingual? = byHouse[house]
}
