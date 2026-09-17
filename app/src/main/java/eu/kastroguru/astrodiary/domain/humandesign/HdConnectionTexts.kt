package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Text for the connection chart. Four state texts do the work here rather than 144 per-channel
 * variants: the state is what decides how a shared channel feels, and the channel's own meaning
 * is already written once in [HdDescriptions.channelInfo] and printed beside it.
 */
object HdConnectionTexts {

    val intro: Pair<String, String> = (
        "Two charts laid over the same 36 channels. What matters is not how many you share but how you share each one — a channel one of you completes in the other pulls very differently from a channel you both already have. Read the electromagnetic list first: that is the part neither of you can explain. TEST."
            to "Две карти, положени върху едни и същи 36 канала. Важното не е колко споделяте, а как споделяте всеки един — канал, който единият довършва в другия, тегли съвсем различно от канал, който и двамата вече имате. Прочетете първо електромагнитния списък: това е частта, която никой от двамата не може да обясни. ПРОБА.")

    val state: Map<HdConnectionKind, Pair<String, String>> = mapOf(

        HdConnectionKind.ELECTROMAGNETIC to (
            "Each of you holds one half, so this circuit exists only while you are together. It is the strongest kind of pull and the reason people say they cannot explain the attraction — apart, you are both missing it; together, it switches on. It is also where the friction lives, because what completes you is by definition not you, and over time the same thing that drew you in is the thing you argue about."
                to "Всеки от двамата държи по една половина, така че тази верига съществува само докато сте заедно. Това е най-силният вид привличане и причината хората да казват, че не могат да обяснят притеглянето — поотделно и на двамата липсва; заедно се включва. Тук живее и търкането, защото онова, което ви допълва, по определение не сте вие, и с времето същото, което ви е привлякло, е онова, за което спорите."),

        HdConnectionKind.COMPANIONSHIP to (
            "You both carry the whole channel, so you recognise each other here immediately and effortlessly — this is the part of the relationship that feels like home. Nothing is exchanged, though: neither of you learns anything in this circuit, and a bond built mostly out of companionship is comfortable and slowly becomes flat. It works best as ballast under the parts that do move."
                to "И двамата носите целия канал, така че тук се разпознавате веднага и без усилие — това е частта от връзката, която се усеща като дом. Тук обаче нищо не се разменя: никой от двамата не научава нищо в тази верига, а връзка, изградена главно от другарство, е удобна и бавно става плоска. Работи най-добре като баласт под частите, които наистина се движат."),

        HdConnectionKind.DOMINANCE to (
            "One of you has the whole channel and the other has neither half of it, so there is nothing to negotiate — the one without it simply receives that energy, and mostly experiences it as \"that is how they are\". It is the quietest of the four states and the easiest to live with, provided the one who holds it does not mistake being unopposed for being right. The other person cannot meet you here; they can only be near it."
                to "Единият от двамата има целия канал, а другият няма нито една от двете половини, така че няма какво да се договаря — човекът без него просто получава тази енергия и най-често я преживява като „такъв си е“. Това е най-тихото от четирите състояния и най-лесното за живеене, стига онзи, който го държи, да не приема липсата на съпротива за правота. Другият не може да ви срещне тук; може само да е близо до това."),

        HdConnectionKind.COMPROMISE to (
            "One of you holds the whole channel and the other holds exactly one gate of it, and that is the harder arrangement: the one with the single gate has an opinion about a circuit they do not actually run, so they keep half-participating and keep bending. This is where the repetitive arguments live — the same disagreement, in the same place, for years — and naming which of you is the one compromising takes most of the heat out of it. It does not resolve; it becomes workable once both of you know it is structural rather than personal."
                to "Единият от двамата държи целия канал, а другият държи точно един гейт от него, и това е по-трудната подредба: човекът с единия гейт има мнение за верига, която всъщност не движи, затова участва наполовина и постоянно отстъпва. Тук живеят повтарящите се спорове — същото несъгласие, на същото място, години наред — и да назовете кой от двамата е онзи, който отстъпва, изважда почти цялата горещина от него. Не се разрешава; става поносимо, когато и двамата знаят, че е структурно, а не лично.")
    )

    /** Centres one person defines and the other does not — steady, one-directional conditioning. */
    val conditioning: Pair<String, String> = (
        "Where one of you has definition and the other does not, the definition wins: the open person takes on that energy in the other's presence and often cannot tell it is borrowed. It is not domination and it is not a fault — it is simply constant, and it explains why one of you seems like a different person in this relationship than outside it. The correction is time apart, not less closeness: whatever leaves when you are alone was never yours."
            to "Там, където единият има дефиниция, а другият не, дефиницията печели: отвореният човек поема тази енергия в присъствието на другия и често не може да усети, че е заета. Не е потискане и не е вина — просто е постоянно, и обяснява защо единият от двамата изглежда като различен човек в тази връзка, отколкото извън нея. Поправката е време поотделно, а не по-малко близост: всичко, което си отива, когато сте сами, никога не е било ваше.")

    /** Centres neither of them defines — the pair's shared blind spot. */
    val bothOpen: Pair<String, String> = (
        "Neither of you defines these, so nobody in this pair is a source here — and that means two things at once. There is no friction between you in these areas, which is restful, and there is also nobody to hold the ground when pressure arrives from outside: you will both take on whatever the room, the family or the situation brings, and amplify it for each other. When a difficulty keeps arriving from nowhere and neither of you can explain it, look at this list first."
            to "Никой от двамата не дефинира тези, така че тук няма кой да е източник — и това значи две неща наведнъж. Между вас няма търкане в тези области, което е спокойно, но също няма кой да задържи почвата, когато натискът дойде отвън: и двамата ще поемете онова, което носи стаята, семейството или ситуацията, и ще си го усилите взаимно. Когато една трудност все идва отникъде и никой от двамата не може да я обясни, вижте първо този списък.")
}
