package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * Hand-written Ascendant-in-sign texts: the first impression, and the way someone opens a situation.
 * The third placement people read, and the one that most often explains why someone is described by
 * others in a way that does not match how they feel inside.
 */
object AscendantInSign {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val entries: Map<String, Bilingual> = mapOf(
        "asc_aries" to t(
            "You come across as direct and quick off the mark — people expect you to take the lead, sometimes before you have decided to. New situations get met head-on rather than studied, which reads as confidence even on the days it is not.",
            "Излизате пред хората като прям и бърз — очакват от вас да поемете водачеството, понякога преди да сте решили. Новите ситуации ги посрещате направо, вместо да ги изучавате, и това звучи като увереност дори в дните, когато не е."
        ),
        "asc_taurus" to t(
            "You give the impression of being settled and unhurried, someone who will not be rushed into anything. That calm makes people trust you quickly — and it also means they underestimate how much is happening under it.",
            "Оставяте впечатление на установен и небързащ човек, когото няма да припрят за нищо. Това спокойствие кара хората бързо да ви се доверят — и същевременно ги подвежда да подценяват колко много става под него."
        ),
        "asc_gemini" to t(
            "You arrive talking, asking, connecting one thing to another. People find you easy company and quick, and often assume you are lighter than you are because you would rather be interesting than earnest at a first meeting.",
            "Пристигате с говорене, питане и връзване на едно с друго. Хората ви намират лесни за компания и бързи, и често ви приемат за по-лек човек, отколкото сте, защото при първа среща предпочитате да сте интересни, а не сериозни."
        ),
        "asc_cancer" to t(
            "You come across as gentle and careful, someone who notices how other people are doing. You test the water before stepping in, and your first move in a new place is usually to work out whether it is safe.",
            "Излизате пред хората като мек и внимателен човек, който забелязва как са другите. Пробвате водата, преди да влезете, а първото ви движение на ново място обикновено е да прецените дали е безопасно."
        ),
        "asc_leo" to t(
            "You are noticed whether or not you set out to be — presence, warmth, something in the bearing. People expect you to carry the room, which is flattering on a good day and a burden on a tired one.",
            "Забелязват ви, независимо дали сте се стремили към това — присъствие, топлина, нещо в държането. Хората очакват вие да носите стаята, което ласкае в добър ден и тежи в изморен."
        ),
        "asc_virgo" to t(
            "You come across as capable, observant and a little reserved — the person who has read the details. New situations get assessed before you commit, and your first instinct is to be useful rather than to be liked.",
            "Излизате пред хората като способен, наблюдателен и малко резервиран — човекът, който е прочел подробностите. Новите ситуации ги преценявате, преди да се обвържете, а първият ви инстинкт е да сте полезен, а не да ви харесат."
        ),
        "asc_libra" to t(
            "You arrive pleasant and accommodating, and people relax around you. The reflex is to smooth things over, which makes first meetings easy and can make it hard for others to tell what you actually think.",
            "Пристигате приятен и отзивчив и хората се отпускат около вас. Рефлексът е да изгладите нещата, което прави първите срещи леки и може да затрудни другите да разберат какво всъщност мислите."
        ),
        "asc_scorpio" to t(
            "You give little away, and people feel it. You come across as self-contained and watchful, which draws some and keeps others at a distance — and you tend to know a great deal about a person before they know anything about you.",
            "Издавате малко и хората го усещат. Излизате пред тях като самодостатъчен и наблюдаващ, което привлича някои и държи други на разстояние — и обикновено знаете много за човека, преди той да знае каквото и да е за вас."
        ),
        "asc_sagittarius" to t(
            "You arrive open, frank and ready to go somewhere. People find you refreshing and occasionally too blunt, and your first response to a new situation is to look for what is interesting in it.",
            "Пристигате отворен, откровен и готов да отидете някъде. Хората ви намират освежаващ и понякога прекалено директен, а първата ви реакция към нова ситуация е да потърсите какво е интересното в нея."
        ),
        "asc_capricorn" to t(
            "You come across as serious and dependable, often older than you are. People hand you responsibility without asking, and it can take a while before they see the humour underneath.",
            "Излизате пред хората като сериозен и надежден, често по-възрастен, отколкото сте. Дават ви отговорност, без да питат, и им трябва време, преди да видят чувството за хумор отдолу."
        ),
        "asc_aquarius" to t(
            "You strike people as your own person — a bit apart, hard to place, not doing it the usual way. That makes you memorable, and it also means first impressions of you vary wildly depending on who is forming them.",
            "Хората ви усещат като човек сам за себе си — малко встрани, труден за категоризиране, който не го прави по обичайния начин. Това ви прави запомнящ се и същевременно значи, че първите впечатления за вас се различават силно според това кой ги съставя."
        ),
        "asc_pisces" to t(
            "You come across as soft-edged and receptive, and people tell you things sooner than they planned to. You take on the tone of whoever you are with, which makes you easy to be around and hard to pin down.",
            "Излизате пред хората с меки контури и възприемчивост, и те ви разказват нещата по-рано, отколкото са смятали. Поемате тона на онзи, с когото сте, което ви прави лесен за общуване и труден за уловим."
        ),
    )
}
