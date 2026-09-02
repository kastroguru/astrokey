package eu.kastroguru.astrodiary.domain.interpretation

/**
 * The ideas a reader needs before a placement means anything: what a house ruler is, what an
 * intercepted house is, what a direction is. Written for someone who has never opened an astrology
 * book, because those are the people who currently bounce off this app.
 */
object ChartConcepts {

    val houseRuler = t(
            "Each house has a sign on its cusp, and every sign has a ruling planet — the house ruler. Where that ruler sits in the chart points to how and where the house’s matters are likely to unfold: it suggests the approach, tools, and setting through which resolutions are found. If the ruler is in the same house it can reinforce the theme; if it sits elsewhere, the answer comes via another area of life and may require trade-offs or external help. Aspects to the ruler reveal tensions, allies, and timing. Practically, follow the ruler to see resources, constraints, and how a house’s question is most constructively pursued.",
            "Всеки дом в картата има знак на своята граница, а всеки знак има планета, която го управлява — това е владетелят на дома. Позицията на владетеля в картата насочва къде и как се реализират темите на самия дом: той показва подхода, инструментите и средата, през които въпросът ще намери отговор. Ако владетелят стои в същия дом, темата може да се самоутвърждава; ако е в друг дом, отговорът идва по различен път и може да изиска подкрепа или компромис. Аспектите към владетеля разкриват пречки, помощници и времето, в което въпросът става актуален. Практически: потърсете владетеля, за да разберете ресурсите, ограниченията и реалните възможности по въпроса."
        )

    val interceptedHouse = t(
            "A sign is intercepted when it lies entirely inside one house without touching either cusp. Intercepted signs often mean their themes arrive late, indirectly, or only after extra effort — there’s no easy ‘doorway’ into that mode of expression. Such energies may require sustained work, catalysts from relationships, or timing events to surface; yet once accessed, they can become a concentrated strength. Look to the intercepted sign’s ruler, transits, and progressions — these tend to act as keys that open up what was previously out of reach.",
            "Знак е прихванат, когато се намира изцяло в рамките на един дом, без да достига до неговите граници. Това може да означава, че темите на този знак се проявяват по-късно, косвено или трудно — те нямат „врата“ за лесен достъп. Прихванатите енергии често изискват съзнателно усилие, повторение или външен тласък, за да се освободят; въпреки това, когато бъдат отключени, те могат да станат концентриран ресурс и да носят вътрешна сила. Наблюдавайте владетеля на знака, транзитите и прогресиите: те често действат като ключ, който отваря затворените теми."
        )

    val duplicatedSign = t(
            "When the same sign straddles the cusp of two consecutive houses, its style and impulses play out in both areas at once. This creates a unified thread: the same instinct or method gets applied across two contexts, which can bring efficiency but also blind spots — a habit that works in one sphere may be carried over into the other where it’s less suitable. Conscious differentiation helps you harness the duplicated sign’s strength while adapting when each house requires a different approach.",
            "Когато един и същи знак лежи на границата между два последователни дома, неговият характер и начин на действие се проявяват в двете области едновременно. Това често създава интегрирана нишка: една и съща реакция или ресурс се използва в два контекста, което може да дава ефективност, но и да води до слепи петна — когато методът работи в една сфера, може да се опира на същия навик и в другата, дори ако не е оптимален. Съзнателната диференциация помага да се използва силата на дублирания знак, без да се изпуска възможността да се адаптира подходът там, където е нужен различен инструмент."
        )

    val rulershipChain = t(
            "Think of a rulership chain: a planet placed in a house raises a theme; the sign on the cusp shades it; and the sign’s ruling planet — and where that ruler sits — reveals where answers and resources actually come from. This chain explains why two people with the same planet in a house can live that theme very differently: the ruler might bring the issue through career, partnership, inner work, or social networks. The ruler’s aspects and transits set timing and obstacles. When interpreting, trace the chain to see not just the question, but the pathways and conditions by which it’s resolved.",
            "Става дума за верига: планета, която застава в даден дом, срещата на знака на ръба и планетата-владетел на този знак. Първата планета дава темата; знакът определя нюанса; владетелят и мястото му в картата показват откъде идват решенията и ресурсите. Тази верига обяснява защо двама души с една и съща планета в един дом я преживяват различно — защото владетелят може да я довежда чрез кариера, партньорства, вътрешна работа или социален кръг. Асцендентът на владетеля, аспектите към него и неговите транзити задават ритъма и препятствията. При тълкуване следвайте веригата, за да видите не само въпроса, но и пътя към решението."
        )

    val primaryDirections = t(
            "Primary directions move the whole natal chart forward roughly one degree per year. They aren’t about where the planets are day-to-day but about larger structural chapters: when a directed point meets another natal point, a significant chapter often opens — a maturing, turning point, or extended process that can last a year or two. Directions tend to mark enduring shifts rather than moods, and are useful for identifying when something structural was set in motion. Read them alongside aspects and rulers to understand the nature of the change and how it unfolded over time.",
            "Примарните дирекции са техника за времево „преместване“ на цялата рождена карта напред с приблизително един градус на година. Това не е за ежедневните транзити, а за по-големи структурни периоди: когато дирекцията на една точка срещне друга, в живота често се отваря голяма глава — промяна, узряване или дълъг процес, който може да продължи година-две. Дирекциите често маркират моменти на вътрешно преориентиране, съзряване или съдбовни възможности, които после се разгръщат по-дълго. Интерпретацията изисква да се гледат съвпадите, околните аспекти и ролята на владетелите: те дават контекст за това какъв вид промяна е настъпила и как може да се работи с нея в ретроспекция и в настоящето."
        )

    val retrograde = t(
            "A planet is retrograde when it appears to move backward from our vantage point. The effect comes from relative motion, not an actual reversal. In the natal chart retrograde planets often indicate inward processing: the planet’s themes may be worked out privately and require more introspection before they show externally. In transit retrogrades mean the same degree is revisited, so a matter tends to come back for review, correction, or deeper integration before it can move forward. Retrograde periods can be productive times for reworking, refining, and reconsidering earlier choices.",
            "Планета е ретроградна, когато от нашата перспектива изглежда да се движи назад. Небесните движения не се обръщат наистина — ефектът идва от относителната скорост и ъгъл. В рождена карта ретроградата често насочва енергията навътре: темите на тази планета може да се преживяват по-интензивно вътрешно, с нужда от преработване и вътрешна рефлексия, преди да се проявят външно. В транзити ретроградните периоди означават повторни преминавания през един и същ градус — въпросът се връща за доизясняване и може да поиска преглед, корекция или забавяне, преди да продължи напред."
        )

    val orb = t(
            "An orb is the allowable deviation from an exact angle at which an aspect is still considered operative. Aspects don’t switch on and off suddenly; influence increases as the configuration approaches exactness. Narrow orbs emphasize precision; wider orbs allow for more diffuse effect. Context matters: slow-moving bodies and large configurations often function across wider orbs, while faster planets tend to act most strongly near exact degrees. Treat the orb as a gradient of potency rather than a simple on/off rule when assessing aspectual influence.",
            "Орбисът е границата на допустимото отклонение от точния ъгъл, при която един аспект все още се смята за действащ. Аспектите не „включват“ и „изключват“ в един миг; вместо това влиянието се усилва, когато се приближава до точната конфигурация. По-строги интерпретации използват тесни орбиси, широките орбиси оставят място за по-дифузни влияния. Контекстът е важен: бавни планети и големи конфигурации по-често работят на по-широк орбис, докато бързите планети се усещат само близо до точния градус. При тълкуване гледайте орбиса като градиент на сила, не като бинарен ключ."
        )

    val applyingSeparating = t(
            "An applying aspect reads as energy that is building: the topic is moving toward you and may bring chances to act, clarify, or shift direction. It’s a useful time to prepare, set intentions, or begin something, because the influence is still increasing. A separating aspect, by contrast, often signals that the pressure is easing — the main point has largely been experienced and what remains is integration or dealing with the results. Pay attention to speed and orb: how soon the aspect becomes exact or how long it’s been past exact helps decide whether to initiate, complete, or reflect.",
            "Приближаващият аспект изглежда като енергия, която набира интензитет: темата се приближава към вас и може да донесе възможности за действие, ясност или поврат. Това е момент, в който има смисъл да се подготвите, да фокусирате намерения или да започнете проект, защото силата на влиянието още расте. От друга страна, отдалечаващият се аспект често показва отслабване на натиска — основното вече е било преживяно и остава интегриране или поемане на последствията. И в двата случая полезно е да се наблюдава скоростта и орбата: колко скоро аспектът става точен или колко време е изминало от точността, за да се прецени дали е време за инициатива, затваряне или осмисляне."
        )

    val angles = t(
            "The Ascendant is the degree rising on the eastern horizon at birth; the Midheaven (MC) is the chart’s highest point. They aren’t planets but geographic intersections — where the sky meets the particular spot on earth where you were born. That’s why an accurate birth time matters: shifting the time by an hour moves these angles and alters house cusps and how themes show up. In practice, the angles point to first impressions and public standing — the interface between your inner chart and the world.",
            "Асцендентът е градусът, който изгрява по източния хоризонт в момента на раждане, а Меридианът (средното небе) е най-високата точка на картата. Те не са планети, а географски кръстовища — местата, където небето среща конкретната точка на земята, на която сте родени. Затова точният час на раждане влияе силно: промяна от един час променя тези ъгли и с това конфигурацията на домове и проявата на личностните теми. В професионален и социален контекст ъглите подсказват как ви възприемат и къде се срещате със света."
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
