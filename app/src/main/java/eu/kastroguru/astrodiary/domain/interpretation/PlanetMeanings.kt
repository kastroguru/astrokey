package eu.kastroguru.astrodiary.domain.interpretation

/**
 * What each body is about, in ordinary words — the building block every other interpretation leans
 * on. Keys are the same planet keys the rest of the app uses ("sun", "moon", …), so this joins
 * straight onto BirthDataEntity and AstroData without a mapping table.
 */
object PlanetMeanings {

    val byKey: Map<String, Bilingual> = mapOf(
        "sun" to t(
            "Who you are at core: your sense of purpose, vital energy, and the areas where you naturally shine. The Sun points to how you want to be seen, the roles you assume, and the direction you aim toward. It often shows your creative drive and need for recognition, as well as where you feel most alive. It can also highlight ego-related tensions and the balance between self-expression and responsibility.",
            "Кой сте, когато сте сами по себе си: централното чувство за цел и посока, вашата жизнена енергия и областите, в които естествено блестите. Слънцето насочва стремежа ви да се изявите, ролите, които поемате, и начина, по който търсите признание. То често показва как формулирате личната си идентичност и какво ви кара да се чувствате значими; може да даде и предизвикателства около егото и необходимостта от автономност и отговорност."
        ),
        "moon" to t(
            "Your emotional landscape and what you need to feel secure. The Moon describes instinctive responses, moods, and the comforts you reach for automatically. It carries early caregiving patterns, emotional memory, and how you soothe yourself. The Moon also shapes home life, routines, and intimacy needs; it may point to moodiness, attachment styles, and what makes you feel nurtured in private settings.",
            "Емоционалният ви свят и това, от което имате нужда, за да се почувствате в безопасност. Луната описва инстинктивните реакции, настроенията и утехата, към която посягате без да мислите. Тя носи спомените за първите грижи, моделите на емоционална подкрепа и начина, по който сте се научили да успокоявате себе си. Влияе върху домашните навици, рутините и нуждата от емоционална сигурност, но може и да показва склонност към настроения и привързаности."
        ),
        "mercury" to t(
            "How you think, learn, and communicate: the speed and style of your mind and how you express ideas. Mercury describes how you process information—whether you are analytical, concrete, or abstract—and what makes conversations feel clear to you. It influences learning preferences, speaking tempo, and listening habits, and may also reveal nervous energy, quick wit, or a tendency to over-rationalize feelings.",
            "Как мислите, научавате и общувате: темпът на ума, стилът на разсъждение и начинът, по който изразявате идеи. Меркурий описва как обработвате информацията, дали сте аналитични, конкретни или склонни към абстракции, и как правите разговорите да „седят“ за вас. Влиятелни са и темпото на говорене, предпочитанията за учене и уменията за слушане; може да осветли нервност, многословие или склонност към рационализиране."
        ),
        "venus" to t(
            "What you find beautiful and the kinds of relationships and pleasures you seek. Venus describes your values, tastes, and how you show affection and pursue harmony. It points to what you’re willing to invest time or money in, what makes you feel appreciated, and what attracts you in people and objects. Venus can highlight a tendency to avoid conflict, or the challenge of balancing desire with healthy boundaries.",
            "Какво намирате за красиво и какви връзки и удоволствия търсите. Венера описва ценностите ви, вкуса и начина, по който показвате привързаност и търсите хармония. Тя насочва към това за което сте склонни да похарчите време или пари, какво ви кара да се чувствате оценени и какво ви привлича в хора и вещи. Може да покаже склонност към избягване на конфликти или, обратно, нуждата да създавате баланс между желания и граници."
        ),
        "mars" to t(
            "How you take initiative and assert your desires: your drive, impulse, and the way you begin things. Mars shows your assertiveness, reactivity, and sexual energy, as well as how you handle conflict—directly or indirectly. It can indicate courage and stamina, but also impatience or volatility. Constructive outlets include physical activity, targeted projects, and learning to channel anger into purposeful action.",
            "Как поемате инициативата и отстоявате желанията си: енергията, импулсът и начинът, по който започвате нещата. Марс показва вашата борбеност, реактивност и сексуална сила, както и предпочитанията ви при конфликт — директни или заобикалящи. Той може да насочи към смелост и издръжливост, но и към избухливост или прибързаност; конструктивните изрази включват спорт, проекти и честна агресия, насочена към целите."
        ),
        "jupiter" to t(
            "Where you seek expansion, belief, and meaning. Jupiter shows the areas in which life can grow—education, travel, philosophy, and generosity. It points to what inspires you to reach for more and where growth feels natural rather than forced. Jupiter often brings optimism and opportunities, but can also encourage excess or overconfidence; useful practice is combining big vision with grounded, steady effort.",
            "Къде търсите разширение, вяра и смисъл. Юпитер описва областите, в които животът може да се разширява — обучение, пътуване, философия и щедрост. Показва какво ви вдъхновява да търсите повече и къде имате склонност да растете естествено. Юпитер може да носи оптимизъм и възможности, но и прибързаност или склонност към преувеличение; ползотворно е внимателното търсене на смислени перспективи и дисциплинираното развитие."
        ),
        "saturn" to t(
            "Where you have to work and take responsibility. Saturn shows the structures, discipline, and limits that shape your maturity. It often points to areas of insecurity or to things that only become solid through persistent effort and time. Saturn can bring fears, perfectionism, or self-imposed constraints, but also resilience, mastery, and the capacity to build lasting results through steady practice.",
            "Къде трябва да положите усилия и да поемете отговорност. Сатурн показва структурите, дисциплината и ограниченията, които оформят зрялостта ви. Често посочва области, в които чувствате несигурност или където успехът идва след постоянство и търпение. Може да заяви страхове, перфекционизъм или самоналожени граници, но и устойчивост, майсторство и умението да създавате трайни резултати чрез системна работа."
        ),
        "uranus" to t(
            "What refuses to stay the same: the realm of the unexpected, individuality, and breaking patterns. Uranus points to sudden change, originality, and the need for freedom; it often reveals where you differ from family or social expectations. Uranus can bring disruptive surprises and breakthroughs, as well as awakenings and inventive solutions. It’s most productive when its restlessness is channeled into constructive innovation rather than chaotic rebellion.",
            "Това, което отказва да остане същото: мястото на неочакваното, индивидуалността и пробива на шаблоните. Уран насочва към внезапни промени, оригиналност и нуждата от свобода; често открива къде се отклонявате от семейните или социалните очаквания. Може да донесе внезапни пробиви или раздори, но и пробуждане и иновативни решения. Работи добре, когато се насочва към създаване на нови форми вместо разрушителна бунтарщина."
        ),
        "neptune" to t(
            "Where firm lines soften and experience becomes symbolic. Neptune often points to longings, ideals and the impulse to escape harsh facts — through faith, art, or spiritual practice. It can open empathy, imagination and aesthetic sensitivity, but also invite confusion, self-deception or addictive patterns. Themes include dissolution, compassion and boundary-blurring; the task is to learn when to trust intuition and when to keep practical anchors so Neptune’s gifts become creative or healing rather than evasive.",
            "Мястото, където границите се размиват и реалността става символ. Нептун често насочва към копнежи, идеали и нуждата да избягате от суровите факти — във вяра, изкуство или спиритуална практика. Той може да отключи състрадание и творческо въображение, но и илюзии, самозалъгване или склонност към зависимост. Темите включват сливане, жертвоготовност и нуждата да различавате интуицията от заблудата, така че да използвате Нептуновите дарове конструктивно."
        ),
        "pluto" to t(
            "Pluto points toward what goes deep and refuses to let go. It often appears through experiences of power, loss, obsession and crisis that unsettle identity. Under Pluto you may undergo breakdown and subsequent rebuilding — painful but potentially liberating. Its work is shadow confrontation: excavating buried drives, exposing control dynamics, and purging what no longer serves so a new, more authentic authority can emerge. Pluto’s intensity can be destructive or regenerative depending on how one engages it.",
            "Плутон насочва към онова, което прониква дълбоко и отказва да се освободи лесно. Той често се проявява през опитности на власт, загуба, обсебване и крайности, които разклащат идентичността. Под негово влияние може да се преживее разрушение и последващо преизграждане — процес, който е болезнен, но потенциално освобождаващ. Плутон ни кани да сблъскаме сенките си, да променим структурите, които ни задържат, и да възстановим лично властване и сила чрез трансформация."
        ),
        "chiron" to t(
            "Chiron marks the old wound you keep tending across a lifetime. That vulnerability can become a resource: lived wisdom, empathy and an ability to guide others through similar hurts. People with strong Chiron themes often channel their pain into teaching, healing or advocacy, yet may also develop defenses like withdrawal or overcompensation. The constructive path is to acknowledge and care for the wound, setting boundaries so your sensitivity becomes a source of service rather than perpetual depletion.",
            "Хирон отбелязва старата рана, която продължавате да оплаквате и лекувате през годините. Тази уязвимост може да ви даде специален ресурс — опит, емпатия и умение да водите другите през подобни трудности. Често компенсирате болката чрез учене, наставничество или лечебни практики, но също така можете да срещнете защитни механизми като отдръпване или прекомерна самопожертвователност. Задачата е да признаете раната, да се грижите за нея и да използвате преживяното, за да подкрепяте другите без да се изтощавате."
        ),
        "rahu" to t(
            "Rahu indicates the direction you are pulled toward even when it feels unfamiliar and instinct is thin. It brings appetite for novelty, ambition and a willingness to step into uncharted territory. Rahu can amplify drive for social recognition, unconventional achievements or rapid growth, but it may also fuel illusions or craving for external validation. Practically, it invites experimentation: try new roles and learn by doing, while keeping a grounded perspective so appetite leads to genuine expansion rather than short-lived excess.",
            "Раху обозначава посоката, към която сте привлечени, но която често е непозната и непроинстинктивна. Тук има апетит за нови преживявания, амбиция и готовност да рискувате онова, което вече ви е познато. Раху може да усилва желанието за социално признание, нетипични успехи или екзотични пътища към растеж, но носи и риска от илюзии или прекомерно желание за външно потвърждение. Полезно е да действате любознателно и постепено, чрез опити и корекции, вместо да очаквате незабавни гаранции."
        ),
        "lilith" to t(
            "Lilith points to what you were told to hide: the untamed, inconvenient part of yourself that refuses to be domesticated. Denied, it can turn bitter, provoke shame or isolation; acknowledged, it becomes a source of power, autonomy and refusal to perform for others. Lilith themes include sexuality, boundary assertion, anger and resistance to societal scripts. Working with Lilith is about integrating these impulses so they inform honest self-expression rather than self-sabotage.",
            "Лилит сочи към онова, което са ви казали да скриете — дива, неудобна част от себе си, която не се подчини и често се наказва. Когато я отричате, тя може да горчи, да провокира вина или изолация; когато я признаете, става източник на лична сила, автономия и ненужна покорност отпада. Темите включват сексуалност, граници, гняв и отказ да се впишете в очакванията. Изследването на Лилит помага да интегрирате тези качества конструктивно и да заявите себе си по-цялостно."
        ),
    )

    fun of(key: String): Bilingual? = byKey[key]
}
