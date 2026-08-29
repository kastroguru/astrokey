package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * Hand-written Midheaven-in-sign texts: what a person becomes known for and which direction their
 * work pulls in. Completes the angle table together with [AscendantInSign].
 */
object MidheavenInSign {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val entries: Map<String, Bilingual> = mapOf(
        "mc_aries" to t(
            "You get known for going first. Work that lets you start things, decide quickly and take the risk suits you; work that requires waiting for consensus wears you down. Expect a path made of jumps rather than a steady climb.",
            "Известни ставате с това, че тръгвате първи. Работа, която ви дава да започвате, да решавате бързо и да поемате риска, ви подхожда; работа, която иска да чакате консенсус, ви изтощава. Очаквайте път от скокове, а не равномерно изкачване."
        ),
        "mc_taurus" to t(
            "You build a reputation slowly and it lasts. People come to you because you are reliable and because what you make holds up, and your career tends to grow by accumulation rather than by leaps.",
            "Изграждате репутация бавно и тя се задържа. Хората идват при вас, защото сте надежден и защото направеното от вас издържа, а кариерата ви расте с натрупване, не със скокове."
        ),
        "mc_gemini" to t(
            "You are known for what you say and how you connect things — words, teaching, explaining, moving information between people. Several parallel occupations suit you better than one narrow title.",
            "Известни сте с това, което казвате, и с това как свързвате нещата — думи, преподаване, обясняване, пренасяне на информация между хората. Няколко занимания наведнъж ви подхождат повече от една тясна титла."
        ),
        "mc_cancer" to t(
            "You end up in a caring or protective role in public, whatever the job title says. Work that involves homes, families, food, safety or looking after people fits, and you need to feel personally invested to do your best.",
            "Пред света се озовавате в грижовна или защитна роля, каквото и да пише в титлата. Подхожда ви работа с домове, семейства, храна, безопасност или грижа за хора, и трябва да сте лично вложени, за да сте в най-добрата си форма."
        ),
        "mc_leo" to t(
            "You are meant to be visible. Recognition is not vanity in your case, it is fuel — work where your contribution has your name on it suits you, and anonymity slowly drains you.",
            "Вашето място е да сте видими. Признанието при вас не е суета, а гориво — подхожда ви работа, в която приносът ви носи вашето име, а анонимността бавно ви изцежда."
        ),
        "mc_virgo" to t(
            "You get known for doing it properly. Precision, expertise and being the one who catches the error build your standing, and you would rather be respected by people who know the craft than famous.",
            "Известни ставате с това, че го правите както трябва. Точността, вещината и това да сте човекът, който хваща грешката, изграждат името ви, и предпочитате да сте уважавани от хора, които разбират занаята, отколкото известни."
        ),
        "mc_libra" to t(
            "Your public role involves other people: representing, mediating, pairing things up, making something agreeable. You do well where fairness and taste matter, and badly where you must be the one who fights.",
            "Общественото ви място минава през другите: да представлявате, да посредничите, да съчетавате, да направите нещо приемливо. Успявате там, където значение имат справедливостта и вкусът, и не успявате там, където трябва вие да сте онзи, който се бие."
        ),
        "mc_scorpio" to t(
            "You are drawn to work with the hidden or the difficult — what people do not discuss openly. Your reputation carries weight and a certain edge, and you gain authority by not flinching where others do.",
            "Тегли ви към работа със скритото или трудното — с онова, което хората не обсъждат открито. Името ви носи тежест и известна острота, а власт печелите с това, че не трепвате там, където други трепват."
        ),
        "mc_sagittarius" to t(
            "You are known for the bigger picture: teaching, travel, publishing, belief, anything that widens the frame. A career confined to one room and one procedure will not hold you.",
            "Известни сте с широката картина: преподаване, пътуване, издаване, вяра — всичко, което разширява рамката. Кариера, затворена в една стая и една процедура, няма да ви удържи."
        ),
        "mc_capricorn" to t(
            "You are built for structure and the long climb. Titles, standards and institutions mean something to you, you take responsibility early, and your standing usually arrives later and more solidly than your peers'.",
            "Направени сте за структура и дълго изкачване. Титлите, стандартите и институциите значат нещо за вас, поемате отговорност рано, а името ви обикновено идва по-късно и по-стабилно от това на връстниците ви."
        ),
        "mc_aquarius" to t(
            "You get known for doing it differently. Work involving systems, technology, groups or reform suits you, and you are of most use exactly where the established way has stopped working.",
            "Известни ставате с това, че го правите различно. Подхожда ви работа със системи, технология, групи или реформа, и сте най-полезни точно там, където утвърденият начин е спрял да работи."
        ),
        "mc_pisces" to t(
            "Your public direction is not a straight line, and forcing one rarely works. Art, care, healing, faith and anything requiring imagination fit; clear boundaries and a definite title are the parts you have to build deliberately.",
            "Посоката ви пред света не е права линия и насилването ѝ рядко помага. Подхождат ви изкуство, грижа, лечение, вяра и всичко, което иска въображение; ясните граници и определената титла са частта, която трябва да изградите съзнателно."
        ),
    )
}
