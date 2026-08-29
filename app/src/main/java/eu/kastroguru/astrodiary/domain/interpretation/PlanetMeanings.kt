package eu.kastroguru.astrodiary.domain.interpretation

/**
 * What each body is about, in ordinary words — the building block every other interpretation leans
 * on. Keys are the same planet keys the rest of the app uses ("sun", "moon", …), so this joins
 * straight onto BirthDataEntity and AstroData without a mapping table.
 */
object PlanetMeanings {

    val byKey: Map<String, Bilingual> = mapOf(
        "sun" to t(
            "Who you are when you are being yourself: what you want to become, what gives you a sense of purpose, and where you shine when you are not performing for anyone.",
            "Кой сте, когато сте себе си: какъв искате да станете, какво ви дава усещане за смисъл и къде светите, когато не се представяте пред никого."
        ),
        "moon" to t(
            "What you need in order to feel safe. Your instinctive reactions, your moods, the comfort you reach for without thinking, and the way you were cared for early on.",
            "От какво имате нужда, за да се чувствате сигурни. Инстинктивните реакции, настроенията, утехата, към която посягате без да мислите, и начинът, по който за вас са се грижили в началото."
        ),
        "mercury" to t(
            "How you think and how you say it: the speed of your mind, the way you learn, and what makes a conversation feel right or wrong to you.",
            "Как мислите и как го изказвате: скоростта на ума, начинът, по който учите, и какво прави един разговор да ви усеща правилен или грешен."
        ),
        "venus" to t(
            "What you find beautiful and what you want to be close to. How you show affection, what you value enough to spend on, and what makes you feel appreciated.",
            "Какво намирате за красиво и до какво искате да сте близо. Как показвате привързаност, какво цените достатъчно, за да похарчите за него, и какво ви кара да се чувствате ценени."
        ),
        "mars" to t(
            "How you go after what you want. Your drive, your temper, the way you start things, and what you are willing to fight for.",
            "Как гоните това, което искате. Устремът, нервът, начинът, по който започвате нещата, и за какво сте склонни да се борите."
        ),
        "jupiter" to t(
            "Where life opens up for you. What you believe in, what you want more of, and the kind of growth that feels natural rather than forced.",
            "Където животът ви се отваря. В какво вярвате, от какво искате повече и какъв растеж ви се усеща естествен, а не насилен."
        ),
        "saturn" to t(
            "Where you have to work for it. What you take seriously, where you feel not good enough until you have earned it, and what becomes solid precisely because it took time.",
            "Където трябва да се потрудите. Какво приемате насериозно, къде се чувствате недостатъчни, докато не си го заслужите, и какво става устойчиво точно защото е отнело време."
        ),
        "uranus" to t(
            "What refuses to stay the same. Where you break your own patterns, where you are unlike your family, and where sudden change arrives whether you asked for it or not.",
            "Онова, което отказва да остане същото. Където чупите собствените си шаблони, където не сте като семейството си и където внезапната промяна идва, независимо дали сте я искали."
        ),
        "neptune" to t(
            "Where the edges blur. What you long for, what you are willing to believe without proof, and where you either create something beautiful or fool yourself.",
            "Където границите се размиват. За какво тъгувате, в какво сте склонни да повярвате без доказателство и къде или създавате нещо красиво, или се самозалъгвате."
        ),
        "pluto" to t(
            "What goes deep and does not let go. Where you meet power, loss and obsession, and where you are rebuilt into someone the earlier version would not recognise.",
            "Онова, което влиза дълбоко и не пуска. Където срещате власт, загуба и обсебване, и където се преизграждате в човек, когото предишната ви версия не би разпознала."
        ),
        "chiron" to t(
            "The old wound you never quite stop tending — and, because you have tended it for so long, the place you can help other people most.",
            "Старата рана, за която никога не спирате съвсем да се грижите — и точно защото сте се грижили толкова дълго, мястото, където най-много можете да помогнете на други."
        ),
        "rahu" to t(
            "The direction you are pulled towards even though it is unfamiliar. Where you have little instinct and much appetite, and where growth means doing what you have not done before.",
            "Посоката, към която ви тегли, макар да е непозната. Където имате малко инстинкт и много апетит, и където растежът значи да правите онова, което не сте правили досега."
        ),
        "lilith" to t(
            "What you were told to hide. The part of you that will not be tamed or made convenient, and which turns bitter when denied and powerful when owned.",
            "Онова, което са ви казали да криете. Частта от вас, която не се опитомява и не става удобна, и която горчи, когато я отричате, и дава сила, когато я приемете."
        ),
    )

    fun of(key: String): Bilingual? = byKey[key]
}
