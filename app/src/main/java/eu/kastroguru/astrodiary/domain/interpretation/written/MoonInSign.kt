package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * Hand-written Moon-in-sign texts: what a person needs in order to feel safe, and how they react
 * before they have had time to think. Read second only to the Sun, and usually the placement people
 * recognise fastest in themselves.
 */
object MoonInSign {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val entries: Map<String, Bilingual> = mapOf(
        "moon_aries" to t(
            "You settle by doing something about it. Sitting with a feeling makes it worse, so you react fast, say it plainly, and are usually over it before the other person has caught up. What you need is somewhere the flare-up is allowed and not held against you.",
            "Успокоявате се, като направите нещо по въпроса. Да седите с чувството го влошава, затова реагирате бързо, казвате го направо и обикновено ви минава, преди другият да е стигнал дотам. Имате нужда от място, където избухването е позволено и не ви се връща после."
        ),
        "moon_taurus" to t(
            "You calm down through the body and through routine: food, familiar places, the same mug, no sudden changes. You are hard to upset and slow to forgive, and comfort for you is something physical rather than something said. Too much turbulence and you simply stop moving.",
            "Успокоявате се през тялото и през рутината: храна, познати места, същата чаша, никакви внезапни промени. Трудно се разстройвате и бавно прощавате, а утехата за вас е нещо физическо, а не изречено. При прекалено много раздрусване просто спирате да се движите."
        ),
        "moon_gemini" to t(
            "You process by talking, and you feel better once the thing has been put into words — sometimes several times, from different angles. Silence reads as danger to you. The catch is explaining a feeling so well that you never quite feel it.",
            "Преработвате нещата, като говорите, и ви става по-леко, щом бъде изречено — понякога по няколко пъти, от различни ъгли. Тишината ви звучи като опасност. Уловката е да обясните едно чувство толкова добре, че така и да не го изживеете."
        ),
        "moon_cancer" to t(
            "You need to belong somewhere and to someone. You look after people almost automatically, remember what was said years ago, and go quiet and inward when you are hurt rather than saying so. Being needed feels safe, which is worth watching.",
            "Имате нужда да принадлежите някъде и на някого. Грижите се за хората почти автоматично, помните какво е било казано преди години, и когато сте наранени, замлъквате и се затваряте, вместо да го кажете. Да сте нужни ви се усеща сигурно — и точно това си струва да следите."
        ),
        "moon_leo" to t(
            "You need warmth aimed at you personally, not approval in general. Given it, you are hugely generous; without it, you feel unseen and start working for attention. A small honest gesture reaches you further than a big impersonal one.",
            "Имате нужда от топлина, насочена лично към вас, а не от одобрение изобщо. Когато я има, сте изключително щедри; без нея се чувствате невидими и започвате да работите за внимание. Малък искрен жест стига по-далеч при вас от голям безличен."
        ),
        "moon_virgo" to t(
            "You feel better when something is in order — the list written, the drawer sorted, the problem named. Worry is your version of caring, and you show love by being useful rather than by saying it. Rest is the thing you postpone.",
            "По-добре сте, когато нещо е подредено — списъкът написан, чекмеджето сортирано, проблемът назован. Притеснението е вашата версия на грижа, а обичта показвате с полза, вместо да я изричате. Почивката е онова, което отлагате."
        ),
        "moon_libra" to t(
            "You are unsettled by conflict in the room, even conflict that has nothing to do with you. You steady yourself by putting things right between people, which makes you gracious company and can leave your own needs last on the list.",
            "Разстройва ви напрежението в стаята, дори когато няма нищо общо с вас. Стабилизирате се, като оправяте нещата между хората, което ви прави приятни за компания и може да остави собствените ви нужди последни в списъка."
        ),
        "moon_scorpio" to t(
            "You feel everything at full volume and show very little of it. Trust is given slowly, tested quietly, and withdrawn permanently. What you need is one person who does not flinch at the depth of it.",
            "Чувствате всичко на пълна сила и показвате много малко от него. Доверието се дава бавно, проверява се тихо и се отнема окончателно. Имате нужда от един човек, който не трепва пред тази дълбочина."
        ),
        "moon_sagittarius" to t(
            "You need air. Space, movement, a plan for something ahead — that is what settles you, more than reassurance does. Held too close you get restless, and you would rather laugh a heavy moment off than sit inside it.",
            "Имате нужда от въздух. Простор, движение, план за нещо напред — това ви успокоява повече от увещанията. Държани прекалено близо, ставате неспокойни, и предпочитате да обърнете тежкия момент на шега, вместо да седите в него."
        ),
        "moon_capricorn" to t(
            "You cope by taking charge. Feelings get postponed until the situation is handled, and often they never quite get their turn. You were probably the responsible one early, and asking for help still feels like a failure rather than an option.",
            "Справяте се, като поемате нещата. Чувствата се отлагат, докато ситуацията се овладее, и често редът им така и не идва. Вероятно сте били отговорният отрано и молбата за помощ още ви се усеща като провал, а не като възможност."
        ),
        "moon_aquarius" to t(
            "You need room to step back and look at your own feelings from the outside, and you are calmest when nothing is being demanded of you emotionally. Closeness on someone else's schedule makes you cooler, not warmer — and the distance is comfort, not indifference.",
            "Имате нужда от възможност да отстъпите и да погледнете чувствата си отвън, и сте най-спокойни, когато никой не изисква нищо от вас емоционално. Близост по чужд график ви прави по-хладни, не по-топли — а разстоянието ви е утеха, не безразличие."
        ),
        "moon_pisces" to t(
            "You absorb the mood around you without meaning to, so you need time alone to work out which of it was yours. Music, water, sleep and imagination restore you. Your kindness is real, and so is the difficulty of saying no.",
            "Попивате настроението наоколо, без да искате, затова имате нужда от време сами, за да разберете кое от него е било ваше. Музиката, водата, сънят и въображението ви възстановяват. Добротата ви е истинска — истинска е и трудността да кажете „не“."
        ),
    )
}
