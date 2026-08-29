package eu.kastroguru.astrodiary.domain.interpretation

/**
 * The ideas a reader needs before a placement means anything: what a house ruler is, what an
 * intercepted house is, what a direction is. Written for someone who has never opened an astrology
 * book, because those are the people who currently bounce off this app.
 */
object ChartConcepts {

    val houseRuler = t(
        "Every house has a sign on its edge, and every sign has a planet that runs it — that planet is the house's ruler. Wherever the ruler sits in the chart is where that part of life gets decided: the house asks the question, the ruler says where the answer comes from.",
        "Всеки дом има знак на границата си, а всеки знак има планета, която го управлява — тя е владетелят на дома. Където стои владетелят в картата, там се решава тази част от живота: домът задава въпроса, а владетелят казва откъде идва отговорът."
    )

    val interceptedHouse = t(
        "A sign is intercepted when it falls entirely inside one house, without touching either edge. The house then has no clear doorway into that sign, and the themes of that sign tend to reach you late, indirectly, or only after someone else opens the door. Nothing is missing from the chart — it is simply harder to get at, and often becomes a strength later precisely because it had to be worked for.",
        "Един знак е прихванат, когато попада изцяло вътре в един дом, без да докосва нито една от границите му. Тогава домът няма ясна врата към този знак и темите му ви стигат по-късно, косвено или само след като някой друг отвори вратата. Нищо не липсва от картата — просто е по-трудно достъпно и често става силна страна точно защото е трябвало да се извоюва."
    )

    val duplicatedSign = t(
        "When one sign sits on the edge of two houses in a row, its way of doing things covers both areas of life at once — the two get handled with the same instinct, for better and for worse.",
        "Когато един знак стои на границата на два последователни дома, неговият начин на действие покрива и двете области наведнъж — те се разиграват с един и същи инстинкт, за добро и за зло."
    )

    val rulershipChain = t(
        "A planet standing in a house is only half the story. The house has a sign on its edge, that sign has a ruling planet, and wherever that ruler stands is where this part of your life actually gets decided. It is why two people with the same placement live it out completely differently: the house says what the matter is, the ruler's house says what it depends on.",
        "Планета, застанала в даден дом, е само половината история. Домът има знак на границата си, знакът има планета-владетел, а където стои този владетел, там всъщност се решава тази част от живота ви. Затова двама души с една и съща позиция я живеят напълно различно: домът казва какъв е въпросът, а домът на владетеля казва от какво зависи."
    )

    val primaryDirections = t(
        "Directions are not about where the planets are today. The whole chart is moved forward through life at roughly one degree for each year, so a planet slowly arrives at the place another planet held at birth. When it arrives, that pairing becomes the theme of a chapter — a year or two, not a mood. This is why a direction can name the period when something structural happened, long after the transits of that week are forgotten.",
        "Дирекциите не са за това къде са планетите днес. Цялата карта се движи напред през живота с около един градус за всяка година, така че една планета бавно стига до мястото, което друга е заемала при раждането. Когато стигне, тази двойка става тема на цяла глава — година-две, не настроение. Затова дирекцията може да назове периода, в който се е случило нещо структурно, дълго след като транзитите от онази седмица са забравени."
    )

    val retrograde = t(
        "A planet is retrograde when it appears to move backwards from where we stand. Nothing reverses in the sky; the planet is simply passing us. In a birth chart it turns that planet inwards — the theme is worked out privately before it shows. In transits it means the same degree is crossed three times, so a subject comes back twice more before it is finished.",
        "Една планета е ретроградна, когато изглежда да се движи назад от нашата гледна точка. В небето нищо не се обръща — планетата просто ни задминава. В рождена карта това обръща планетата навътре: темата се преработва вътрешно, преди да се покаже. При транзитите значи, че същият градус се минава три пъти, тоест темата се връща още два пъти, преди да е приключила."
    )

    val orb = t(
        "An aspect does not switch on at an exact angle and off a degree later. The orb is how far from exact the app still counts it as working — the closer to exact, the more clearly you feel it.",
        "Аспектът не се включва в точен ъгъл и не се изключва един градус по-късно. Орбисът е колко далеч от точното приложението още го брои за действащ — колкото по-близо до точното, толкова по-ясно се усеща."
    )

    val applyingSeparating = t(
        "An aspect that is still closing in is building: the subject is coming towards you and has not peaked. One that has passed exact is fading: what it had to say has largely been said, and you are living with the result.",
        "Аспект, който още се приближава, набира сила: темата идва към вас и не е достигнала върха си. Онзи, който е минал точното, отслабва: каквото е имал да каже, вече е казано и живеете с резултата."
    )

    val angles = t(
        "The Ascendant is the degree that was rising in the east at birth, and the Midheaven is the highest point of the chart. They are not planets — they are the two places where the sky meets the particular spot on earth you were born on, which is why the birth time matters so much: change it by an hour and these two move.",
        "Асцендентът е градусът, който е изгрявал на изток при раждането, а Меридианът е най-високата точка на картата. Те не са планети — те са двете места, където небето се среща с конкретната точка на земята, на която сте родени. Точно затова часът на раждане е толкова важен: сменете го с един час и тези две неща се преместват."
    )

    /** Everything above, for the coverage test — a concept with only one language is a bug. */
    val all: Map<String, Bilingual> = mapOf(
        "houseRuler" to houseRuler,
        "rulershipChain" to rulershipChain,
        "interceptedHouse" to interceptedHouse,
        "duplicatedSign" to duplicatedSign,
        "primaryDirections" to primaryDirections,
        "retrograde" to retrograde,
        "orb" to orb,
        "applyingSeparating" to applyingSeparating,
        "angles" to angles,
    )
}
