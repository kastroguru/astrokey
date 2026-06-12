package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Human Design descriptive text content.
 * Each value is a Pair<String, String> — (English, Bulgarian).
 */
object HdDescriptions {

    // ── Types ─────────────────────────────────────────────────────────────────
    data class TypeInfo(
        val name: Pair<String, String>,
        val percent: String,
        val aura: Pair<String, String>,
        val description: Pair<String, String>,
        val strategy: Pair<String, String>,
        val signature: Pair<String, String>,
        val notSelf: Pair<String, String>
    )

    val typeInfo: Map<HdType, TypeInfo> = mapOf(
        HdType.GENERATOR to TypeInfo(
            name = "Generator" to "Генератор",
            percent = "~36%",
            aura = "Open & Enveloping" to "Отворена и обгръщаща",
            description = ("Generators are the vital life force of the planet. They have a defined Sacral center that generates sustainable energy for work, creativity, and relationships. Their sacral energy is magnetic — it naturally attracts people and opportunities to respond to.")
                    to ("Генераторите са жизнената сила на планетата. Имат дефиниран Сакрален център, който произвежда устойчива енергия за работа, творчество и взаимоотношения. Тяхната сакрална енергия е магнетична — тя естествено привлича хора и възможности, на които да откликнат."),
            strategy = "Wait to respond. Life will bring what needs a response. Trust the gut sounds — ah-huh (yes) or uhn-un (no)."
                    to "Изчакай и откликни. Животът ще донесе онова, на което трябва да се откликне. Доверявай се на сакралните звуци — ъъъ-ха (да) или ъъъ-не (не).",
            signature = "Satisfaction" to "Удовлетворение",
            notSelf = "Frustration — arises when initiating instead of responding, or doing work that doesn't light up the sacral."
                    to "Разочарование — появява се при иницииране вместо отклик, или при работа, която не \"запалва\" сакралния отговор."
        ),
        HdType.MANIFESTING_GENERATOR to TypeInfo(
            name = "Manifesting Generator" to "Манифестиращ Генератор",
            percent = "~32%",
            aura = "Open & Enveloping" to "Отворена и обгръщаща",
            description = ("Manifesting Generators are a hybrid type with both sacral energy and a direct motor-to-Throat connection. They are multi-passionate, fast-moving, and can do many things simultaneously. They may skip steps others can't — and need to check if it feels right to go back and fill in what was missed.")
                    to ("Манифестиращите Генератори са хибриден тип с сакрална енергия и пряка връзка мотор-Гърло. Те са страстни към много неща, движат се бързо и могат да правят много неща едновременно. Понякога прескачат стъпки — и трябва да проверят дали трябва да се върнат и запълнят пропуснатото."),
            strategy = "Wait to respond, then inform before acting to reduce resistance and friction."
                    to "Изчакай и откликни, след това информирай преди да действаш, за да намалиш съпротивата.",
            signature = "Satisfaction" to "Удовлетворение",
            notSelf = "Frustration and Anger — from forcing things or not informing others before acting."
                    to "Разочарование и гняв — от форсиране или от неинформиране на другите преди действие."
        ),
        HdType.MANIFESTOR to TypeInfo(
            name = "Manifestor" to "Манифестор",
            percent = "~8%",
            aura = "Closed & Repelling" to "Затворена и отблъскваща",
            description = ("Manifestors are the only type with the natural ability to initiate without waiting. They have a motor directly connected to the Throat, giving them a powerful impact aura. They are designed to act, to start things — not to ask permission. Others may feel unsettled by their energy without knowing why.")
                    to ("Манифесторите са единственият тип с естествена способност да инициират без чакане. Имат мотор, пряко свързан с Гърлото, което им дава мощна аура. Проектирани са да действат, да започват неща — не да искат разрешение. Другите може да се чувстват неспокойни от тяхната енергия, без да знаят защо."),
            strategy = "Inform the people in your life before taking action. This reduces resistance and allows others to support you."
                    to "Информирай хората в живота си преди да предприемеш действие. Това намалява съпротивата и позволява на другите да те подкрепят.",
            signature = "Peace" to "Мир",
            notSelf = "Anger — from being stopped, questioned, or not understood when acting without informing."
                    to "Гняв — от спирането, разпитването или неразбирането при действие без предупреждение."
        ),
        HdType.PROJECTOR to TypeInfo(
            name = "Projector" to "Проектор",
            percent = "~20%",
            aura = "Focused & Absorbing" to "Фокусирана и поглъщаща",
            description = ("Projectors are designed to guide, manage, and direct the energy of others. They have a penetrating, focused aura that sees deeply into people. They are the natural advisors and leaders — but only when their wisdom is recognized and invited. Without an invitation, their guidance is not received well.")
                    to ("Проекторите са проектирани да насочват, управляват и ръководят енергията на другите. Имат проникваща, фокусирана аура, която вижда дълбоко в хората. Те са естествените съветници и лидери — но само когато тяхната мъдрост е разпозната и поканена. Без покана техните напътствия не се приемат добре."),
            strategy = "Wait for the recognition and invitation before offering guidance or making big moves in life."
                    to "Изчакай разпознаване и покана преди да предложиш напътствие или да предприемеш важни стъпки в живота.",
            signature = "Success" to "Успех",
            notSelf = "Bitterness — from giving unsolicited advice or working as hard as energy types."
                    to "Горчивина — от даване на нежелани съвети или от работа толкова усилено като енергийните типове."
        ),
        HdType.REFLECTOR to TypeInfo(
            name = "Reflector" to "Рефлектор",
            percent = "~1%",
            aura = "Resistant & Sampling" to "Устойчива и вземаща проби",
            description = ("Reflectors have no defined centers — they are completely open and reflect the health of their environment back to the community. They are deeply influenced by the people around them and by the lunar cycle. Each day feels different as the moon activates different centers in their chart.")
                    to ("Рефлекторите нямат дефинирани центрове — те са напълно отворени и отразяват здравето на своята среда обратно към общността. Дълбоко се влияят от хората около тях и от лунния цикъл. Всеки ден се чувства различно, тъй като луната активира различни центрове в тяхната карта."),
            strategy = "Wait a full 28-day lunar cycle before making major decisions. Talk to many different people over this period."
                    to "Изчакай пълен 28-дневен лунен цикъл преди да вземеш важни решения. Говори с много различни хора в този период.",
            signature = "Delight" to "Удоволствие",
            notSelf = "Disappointment — from rushing decisions or being in the wrong environment."
                    to "Разочарование — от бързане с решения или от пребиваване в неправилна среда."
        )
    )

    // ── Authorities ───────────────────────────────────────────────────────────
    val authorityInfo: Map<HdAuthority, Pair<String, String>> = mapOf(
        HdAuthority.EMOTIONAL to (
            "Your truth lives in the wave of emotions. There is no truth in the now — wait through the emotional wave before deciding. Clarity comes over time: hours, days, or weeks. Never commit when at an emotional high or low."
                    to "Твоята истина живее в емоционалната вълна. Няма истина в момента — изчакай вълната да премине преди да решиш. Яснотата идва с времето: часове, дни или седмици. Никога не се ангажирай в емоционален пик или дъно."
        ),
        HdAuthority.SACRAL to (
            "Your authority is the immediate gut response — ah-huh (yes) or uhn-un (no). The sacral speaks in the moment, through sounds and physical sensation. Trust these responses without mental override. Only works when asked direct yes/no questions."
                    to "Твоят авторитет е непосредственият сакрален отговор — ъъъ-ха (да) или ъъъ-не (не). Сакралният говори в момента чрез звуци и физически усещания. Доверявай се на тези отговори без ментална намеса. Работи само при директни въпроси с отговор да/не."
        ),
        HdAuthority.SPLENIC to (
            "Splenic authority is the quiet voice of in-the-moment intuition. It whispers once and never repeats — you must be present enough to hear it. It is the oldest awareness, tied to survival and what is healthy for you right now."
                    to "Спленичният авторитет е тихият глас на интуицията в момента. Той шепне веднъж и никога не повтаря — трябва да си достатъчно присъстващ, за да го чуеш. Той е най-старото осъзнаване, свързано с оцеляването и онова, което е здравословно за теб точно сега."
        ),
        HdAuthority.EGO to (
            "Your authority is the Ego/Heart. Decisions are made based on what the heart genuinely wants — what serves your will and self-interest. Speak your decisions out loud; if they feel right to say, they are right to do. Only commit to what you truly want."
                    to "Твоят авторитет е Егото/Сърцето. Решенията се вземат на базата на онова, което сърцето наистина иска — онова, което служи на волята и интересите ти. Изричай решенията си на глас; ако им е удобно да се кажат, правилно е да се направят. Ангажирай се само с онова, което наистина искаш."
        ),
        HdAuthority.SELF_PROJECTED to (
            "Your authority comes from the G/Self Center — your identity and love. Talk through decisions with trusted people and listen to your own words. The direction that feels most 'you' is the right one. Your voice speaking reveals your truth."
                    to "Твоят авторитет идва от G/Центъра на Аза — твоята идентичност и любов. Обсъждай решенията с доверени хора и слушай собствените си думи. Посоката, която се чувства най-\"ти\", е правилната. Твоят глас, когато говориш, разкрива истината ти."
        ),
        HdAuthority.MENTAL to (
            "You have no inner authority — your authority is environmental and mental. Talk to trusted people and sounding boards, not to get their advice, but to hear yourself speak. The right environment and the right people help you find clarity."
                    to "Нямаш вътрешен авторитет — твоят авторитет е на средата и менталния план. Говори с доверени хора, не за да получиш техния съвет, а за да се чуеш как говориш. Правилната среда и правилните хора ти помагат да намериш яснота."
        ),
        HdAuthority.LUNAR to (
            "As a Reflector, your authority is the lunar cycle. Wait the full 28 days before making major decisions, ideally tracking how you feel about the decision as the Moon moves through your chart. Speak with many different people through this period."
                    to "Като Рефлектор твоят авторитет е лунният цикъл. Изчакай пълните 28 дни преди да вземеш важни решения, следейки как се чувстваш спрямо решението, докато Луната преминава през картата ти. Говори с много различни хора в този период."
        )
    )

    // ── Profiles ──────────────────────────────────────────────────────────────
    val profileInfo: Map<String, Pair<String, String>> = mapOf(
        "1/3" to ("Investigator/Martyr: You need a solid foundation of knowledge before acting. You learn primarily through trial and error — your 'mistakes' are your greatest teacher and data source."
                to "Изследовател/Мъченик: Имаш нужда от солидна основа от знания преди да действаш. Учиш се главно чрез проба и грешка — твоите \"грешки\" са най-великият ти учител и източник на данни."),
        "1/4" to ("Investigator/Opportunist: You need deep personal foundations, and your opportunities come through your social network. Research thoroughly, then share what you've mastered with trusted connections."
                to "Изследовател/Опортюнист: Имаш нужда от дълбоки лични основи, а твоите възможности идват чрез социалната ти мрежа. Проучи задълбочено, след което сподели онова, което си овладял, с доверени връзки."),
        "2/4" to ("Hermit/Opportunist: You have natural talents you may not even be aware of. You need time alone to develop them, but your opportunities come through your network. The right call from the right person can change your life."
                to "Отшелник/Опортюнист: Имаш природни дарби, за които може дори да не знаеш. Имаш нужда от самота за да ги развиеш, но твоите възможности идват чрез мрежата ти. Правилното обаждане от правилния човек може да промени живота ти."),
        "2/5" to ("Hermit/Heretic: You are projected onto as a 'savior' or practical problem-solver, even if you don't see yourself that way. You need solitude to recharge, yet the world calls on your unconscious gifts to fix things."
                to "Отшелник/Еретик: Другите те проектират като 'спасител' или практически решавач на проблеми, дори ако ти не се виждаш така. Имаш нужда от самота за зареждане, но светът призовава несъзнателните ти дарби да поправят нещата."),
        "3/5" to ("Martyr/Heretic: You learn through life experience — bonds that work and don't work, jobs tried and rejected. This experiential wisdom makes you a practical, karmic teacher who others turn to for real-world solutions."
                to "Мъченик/Еретик: Учиш се чрез житейски опит — връзки, които работят и не работят, работи, опитани и отхвърлени. Тази придобита мъдрост те прави практически, кармичен учител, към когото другите се обръщат за решения в реалния живот."),
        "3/6" to ("Martyr/Role Model: Three life phases — first half spent in trial and error, then a period of stepping back and observing, then becoming a wise role model embodying the lessons of your experience."
                to "Мъченик/Ролеви модел: Три жизнени фази — първата половина в проба и грешка, след това период на отдръпване и наблюдение, после ставане на мъдър ролеви модел, въплъщаващ уроците от опита ти."),
        "4/6" to ("Opportunist/Role Model: Your opportunities come through your close personal network. In the first half of life you build those networks; in the second half you become a trusted role model who others look to for guidance."
                to "Опортюнист/Ролеви модел: Твоите възможности идват чрез близката ти лична мрежа. В първата половина на живота изграждаш тези мрежи; в втората ставаш доверен ролеви модел, към когото другите се обръщат за насоки."),
        "4/1" to ("Opportunist/Investigator: You need a stable personal foundation to feel secure, and opportunities come through your network. You must build solid knowledge before you can comfortably share with others."
                to "Опортюнист/Изследовател: Имаш нужда от стабилна лична основа за да се чувстваш сигурен, и възможностите идват чрез мрежата ти. Трябва да изградиш солидни знания преди удобно да споделяш с другите."),
        "5/1" to ("Heretic/Investigator: You are projected onto by others as someone who can 'save' or provide universal solutions. You need solid foundations to avoid the projection trap. Fame (and infamy) come easily to you."
                to "Еретик/Изследовател: Другите те проектират като някой, който може да 'спаси' или да предостави универсални решения. Имаш нужда от солидни основи за да избегнеш капана на проекцията. Слава (и лоша слава) ти идват лесно."),
        "5/2" to ("Heretic/Hermit: Your unconscious gifts are sought out by others who project great practicality onto you. You need significant alone time to recharge, yet you are constantly called upon by the world to step in and fix things."
                to "Еретик/Отшелник: Несъзнателните ти дарби са търсени от другите, които проектират голяма практичност върху теб. Имаш нужда от значително самотно време за зареждане, но светът постоянно те вика да се намесиш и поправиш нещата."),
        "6/2" to ("Role Model/Hermit: Three phases of life — a first phase of trial and error (Lines 3&2), a second phase of going 'on the roof' to observe and reflect, and a third phase after ~50 of living as a trusted, embodied role model."
                to "Ролеви модел/Отшелник: Три жизнени фази — първа фаза на проба и грешка (Линии 3&2), втора фаза на 'качване на покрива' за наблюдение и размисъл, и трета фаза след ~50 години на живеене като доверен, въплътен ролеви модел."),
        "6/3" to ("Role Model/Martyr: You embody wisdom through lived experience. The first half of life is rich with lessons through trial and error; the second half is about becoming the lived example others look to for authentic guidance."
                to "Ролеви модел/Мъченик: Въплъщаваш мъдрост чрез преживян опит. Първата половина на живота е богата с уроци чрез проба и грешка; втората половина е за ставане на живия пример, към когото другите се обръщат за автентично напътствие.")
    )

    // ── Definition ────────────────────────────────────────────────────────────
    val definitionInfo: Map<HdDefinition, Pair<String, String>> = mapOf(
        HdDefinition.SINGLE to (
            "Single Definition means all your defined centers are connected in one circuit. Your energy is consistent, reliable, and self-contained. You don't need others to 'complete' you energetically, which gives you a stable, independent nature."
                    to "Единичната Дефиниция означава, че всички твои дефинирани центрове са свързани в една верига. Твоята енергия е последователна, надеждна и самодостатъчна. Нямаш нужда от другите да те 'допълнят' енергийно, което ти дава стабилна, независима природа."
        ),
        HdDefinition.SPLIT to (
            "Split Definition means your defined centers form two separate groups. You instinctively seek people or transits that bridge your split, creating a feeling of completeness. You are more flexible than single definitions, open to different perspectives."
                    to "Сплит Дефиницията означава, че дефинираните ти центрове образуват две отделни групи. Инстинктивно търсиш хора или транзити, които да мостят твоя сплит, създавайки усещане за цялост. Ти си по-гъвкав от единичните дефиниции и отворен към различни гледни точки."
        ),
        HdDefinition.TRIPLE_SPLIT to (
            "Triple Split Definition has three separate groups of defined centers. You are highly flexible and benefit from many different types of people. You need time to process information as it comes in waves from different circuits activating at different times."
                    to "Тройната Сплит Дефиниция има три отделни групи дефинирани центрове. Ти си много гъвкав и се облагодетелстваш от много различни типове хора. Имаш нужда от време за обработка на информация, тъй като тя идва на вълни от различни вериги, активирани в различни моменти."
        ),
        HdDefinition.QUADRUPLE_SPLIT to (
            "Quadruple Split Definition is very rare — four separate groups of defined centers. You have the greatest openness of all definition types and benefit enormously from the right community and social environment around you."
                    to "Четворната Сплит Дефиниция е много рядка — четири отделни групи дефинирани центрове. Имаш най-голяма отвореност от всички типове дефиниция и много се облагодетелстваш от правилната общност и социална среда около теб."
        ),
        HdDefinition.NONE to (
            "No Definition (Reflector): All centers are open and undefined. You are a mirror of the people and environment around you. Your openness is a gift — you can sample and reflect the full range of human experience."
                    to "Без Дефиниция (Рефлектор): Всички центрове са отворени и недефинирани. Ти си огледало на хората и средата около теб. Твоята отвореност е дар — можеш да вземаш проби и да отразяваш целия спектър на човешкото преживяване."
        )
    )

    // ── Centers ───────────────────────────────────────────────────────────────
    data class CenterInfo(val defined: Pair<String, String>, val undefined: Pair<String, String>)

    val centerInfo: Map<HdCenter, CenterInfo> = mapOf(
        HdCenter.HEAD to CenterInfo(
            defined = ("Defined Head: You have a consistent, reliable source of mental pressure and inspiration. You generate your own questions, doubts, and creative impulses. Others may be drawn to your ideas and find your mental energy inspiring.")
                    to ("Дефинирана Глава: Имаш последователен и надежден источник на ментален натиск и вдъхновение. Сам генерираш въпроси, съмнения и творчески импулси. Другите може да бъдат привлечени от твоите идеи и да намират менталната ти енергия вдъхновяваща."),
            undefined = ("Undefined Head: You amplify and sample others' mental pressure. You may feel pressured to think about things that don't really matter to you. The wisdom here is recognising which questions are yours to answer and which belong to others.")
                    to ("Недефинирана Глава: Усилваш и вземаш проби от менталния натиск на другите. Може да се чувстваш натиснат да мислиш за неща, които всъщност не те касаят. Мъдростта тук е да разпознаеш кои въпроси са твои за отговаряне и кои принадлежат на другите.")
        ),
        HdCenter.AJNA to CenterInfo(
            defined = ("Defined Ajna: You have a fixed and consistent way of processing information and forming opinions. Your mind works in a reliable pattern. You may be certain about your views, which can be a strength, though it may limit openness to other perspectives.")
                    to ("Дефинирана Аджна: Имаш фиксиран и последователен начин за обработка на информация и формиране на мнения. Умът ти работи по надеждна схема. Може да си убеден в своите виждания, което може да е сила, макар да ограничава откритостта към други гледни точки."),
            undefined = ("Undefined Ajna: You have a flexible and adaptable mind that can process information in many different ways. You are not fixed in your thinking, which gives you great mental versatility. The challenge is pretending to be certain when you are not.")
                    to ("Недефинирана Аджна: Имаш гъвкав и адаптивен ум, който може да обработва информация по много различни начини. Не си фиксиран в мисленето си, което ти дава голяма ментална гъвкавост. Предизвикателството е да не се преструваш, че си сигурен, когато не си.")
        ),
        HdCenter.THROAT to CenterInfo(
            defined = ("Defined Throat: You have a consistent and reliable voice and presence. You can initiate communication and manifestation reliably. What you say carries consistent energy, and you are naturally able to attract attention through your voice and expression.")
                    to ("Дефинирано Гърло: Имаш последователен и надежден глас и присъствие. Можеш надеждно да инициираш комуникация и манифестация. Казаното от теб носи последователна енергия и естествено привличаш внимание чрез гласа и изразяването си."),
            undefined = ("Undefined Throat: You are naturally a good listener, absorbing others' communication styles. You may feel pressure to talk to attract attention, but waiting to be spoken to first is your wisdom. When invited to speak, your words carry a unique and surprising quality.")
                    to ("Недефинирано Гърло: Ти си естествено добър слушател, поглъщащ начините на общуване на другите. Може да чувстваш натиск да говориш за да привлечеш внимание, но мъдростта ти е да чакаш първо да ти заговорят. Когато си поканен да говориш, думите ти имат уникално и изненадващо качество.")
        ),
        HdCenter.G to CenterInfo(
            defined = ("Defined G Center: You have a consistent, reliable sense of identity, self-love, and direction in life. You tend to know who you are and where you are headed, even if you don't know exactly how. Your identity remains stable regardless of who surrounds you.")
                    to ("Дефиниран G Център: Имаш последователно и надеждно чувство за идентичност, самолюбие и посока в живота. Склонен си да знаеш кой си и накъде отиваш, дори и да не знаеш точно как. Идентичността ти остава стабилна независимо от това кой е около теб."),
            undefined = ("Undefined G Center: Your identity and direction shift depending on who you are with and where you are. This is not a weakness — it is a gift of adaptability. The wisdom is not trying to fix your identity; instead, find environments and people where you feel most yourself.")
                    to ("Недефиниран G Център: Твоите идентичност и посока се променят в зависимост от това с кого си и където се намираш. Това не е слабост — то е дар на адаптивност. Мъдростта е да не се опитваш да фиксираш идентичността си; вместо това, намери среди и хора, при които се чувстваш най-много себе си.")
        ),
        HdCenter.HEART to CenterInfo(
            defined = ("Defined Heart/Ego: You have consistent access to willpower, self-worth, and the energy of the material world. You can make commitments and keep them. It is important for you to only commit to what you genuinely want, and to honour your need for rest after exertion.")
                    to ("Дефинирано Сърце/Его: Имаш последователен достъп до воля, самооценка и енергия на материалния свят. Можеш да поемаш ангажименти и да ги спазваш. Важно е да се ангажираш само с онова, което наистина искаш, и да зачиташ нуждата си от почивка след усилие."),
            undefined = ("Undefined Heart/Ego: Your willpower fluctuates — you have it sometimes and not others. This is natural and healthy; you are not designed to have consistent willpower. The wisdom is not to prove your worth or make promises you can't keep during high-energy moments.")
                    to ("Недефинирано Сърце/Его: Волята ти се колебае — понякога я имаш, друг път не. Това е естествено и здравословно; не си проектиран да имаш последователна воля. Мъдростта е да не доказваш стойността си или да не даваш обещания в моменти на висока енергия, които не можеш да спазиш.")
        ),
        HdCenter.SACRAL to CenterInfo(
            defined = ("Defined Sacral: You are an energy type with a powerful motor for life, work, and sexuality. Your energy regenerates through the night and is available again each morning. You are designed to respond to life with your gut, using this energy for work that truly satisfies you.")
                    to ("Дефиниран Сакрален: Ти си енергиен тип с мощен мотор за живот, работа и сексуалност. Твоята енергия се регенерира за нощта и е налична отново всяка сутрин. Проектиран си да откликваш на живота с червата си, използвайки тази енергия за работа, която наистина те удовлетворява."),
            undefined = ("Undefined Sacral: You do not have consistent generative energy. You absorb and amplify the sacral energy of others, which can lead to taking on more than you can sustain. Honouring your need for rest and not working like Sacral-defined types is crucial for your health.")
                    to ("Недефиниран Сакрален: Нямаш последователна генеративна енергия. Поглъщаш и усилваш сакралната енергия на другите, което може да доведе до поемане на повече, отколкото можеш да поддържаш. Зачитането на нуждата ти от почивка и не работенето като дефинираните Сакрални типове е от решаващо значение за здравето ти.")
        ),
        HdCenter.SPLEEN to CenterInfo(
            defined = ("Defined Spleen: You have consistent access to intuition, immune awareness, and primal instinct. Your body knows immediately what is healthy or unhealthy for you in any moment. This intuitive awareness is your greatest guide — trust the quiet impulses of your body.")
                    to ("Дефинирана Слезка: Имаш последователен достъп до интуиция, имунно осъзнаване и първичен инстинкт. Тялото ти знае незабавно какво е здравословно или нездравословно за теб в момента. Това интуитивно осъзнаване е най-великият ти водач — доверявай се на тихите импулси на тялото си."),
            undefined = ("Undefined Spleen: You amplify the fears and well-being awareness of those around you. You may hold on to people, jobs, or situations that are not healthy because they feel 'good' in the moment. The wisdom is learning to let go of what is no longer serving your health.")
                    to ("Недефинирана Слезка: Усилваш страховете и осъзнаването за благополучие на тези около теб. Може да се задържаш за хора, работни места или ситуации, които не са здравословни, защото се чувстват 'добре' в момента. Мъдростта е да се научиш да пускаш онова, което вече не служи на здравето ти.")
        ),
        HdCenter.SOLAR_PLEXUS to CenterInfo(
            defined = ("Defined Solar Plexus: You ride an emotional wave that moves between highs and lows — this is your nature and not something to fix. There is no truth in the now for you; clarity comes over time. Waiting through the wave before making important decisions is your key to living correctly.")
                    to ("Дефиниран Слънчев Сплит: Яздиш емоционална вълна, която се движи между върхове и дъна — това е твоята природа и нещо, което не трябва да 'поправяш'. За теб няма истина в момента; яснотата идва с времето. Изчакването на вълната да премине преди важни решения е ключът ти към правилния живот."),
            undefined = ("Undefined Solar Plexus: You absorb and amplify the emotions of those around you — you feel others' feelings as if they were your own. This sensitivity is a gift when understood. The wisdom is knowing which emotions are truly yours and not trying to 'fix' others' emotional states.")
                    to ("Недефиниран Слънчев Сплит: Поглъщаш и усилваш емоциите на тези около теб — чувстваш чувствата на другите все едно са твои собствени. Тази чувствителност е дар, когато е разбрана. Мъдростта е да знаеш кои емоции са наистина твои и да не се опитваш да 'поправяш' емоционалните състояния на другите.")
        ),
        HdCenter.ROOT to CenterInfo(
            defined = ("Defined Root: You have a consistent adrenal pressure that drives you to complete tasks and move through life at your own pace. This pressure is your engine. The key is recognising when you are rushing to relieve the pressure vs. when you are responding to a genuine impulse.")
                    to ("Дефиниран Корен: Имаш последователно адреналинно налягане, което те движи да завършваш задачи и да се придвижваш в живота в собствено темпо. Това налягане е твоят двигател. Ключът е да разпознаеш кога бързаш за да облекчиш налягането и кога откликваш на истински импулс."),
            undefined = ("Undefined Root: You absorb and amplify the adrenal stress of people around you. You may rush to get things done simply to relieve this amplified pressure. The wisdom is that there is no real hurry — you can slow down and make decisions without rushing to reduce the pressure.")
                    to ("Недефиниран Корен: Поглъщаш и усилваш адреналиновия стрес на хората около теб. Може да бързаш да свършиш нещата просто за да облекчиш това усилено налягане. Мъдростта е, че няма истинско бързане — можеш да забавиш и да вземаш решения без да бързаш за да намалиш налягането.")
        )
    )

    // ── Channels ──────────────────────────────────────────────────────────────
    val channelInfo: Map<String, Pair<String, String>> by lazy {
        val map = mutableMapOf<String, Pair<String, String>>()
        // Key is "min-max" gate numbers
        fun key(a: Int, b: Int) = "${minOf(a,b)}-${maxOf(a,b)}"

        map[key(1,8)]   = "Inspiration (1-8): The channel of creative individual expression. You have a consistent drive to be creatively seen and heard. Your creative output naturally inspires others." to "Вдъхновение (1-8): Каналът на творческото индивидуално изразяване. Имаш последователен стремеж да бъдеш творчески видян и чут. Творческото ти творчество естествено вдъхновява другите."
        map[key(2,14)]  = "The Beat (2-14): The channel of the keeper of the keys — you have a natural magnetic quality that directs life-force energy. Others are drawn to your direction even when you don't try." to "Ритъмът (2-14): Каналът на пазителя на ключовете — имаш естествено магнетично качество, което насочва жизнената енергия. Другите са привлечени към твоята посока дори когато не се стараеш."
        map[key(3,60)]  = "Mutation (3-60): The channel of energy mutation. You feel the pressure to break through limitation and bring something new into being. Change comes in bursts — you can't force it; it happens when it's ready." to "Мутация (3-60): Каналът на енергийната мутация. Чувстваш натиска да преодолееш ограничения и да внесеш нещо ново. Промяната идва на тласъци — не можеш да я форсираш; тя се случва, когато е готова."
        map[key(4,63)]  = "Logic (4-63): The channel of mental logic and doubt. You have a consistent pressure to doubt and question patterns, seeking proof and certainty. Your gift is bringing logical rigour to collective understanding." to "Логика (4-63): Каналът на менталната логика и съмнение. Имаш последователен натиск да се съмняваш и да разпитваш модели, търсейки доказателства и сигурност. Твоят дар е да внасяш логическа строгост в колективното разбиране."
        map[key(5,15)]  = "Rhythm (5-15): The channel of natural rhythms and timing. You have a unique relationship with time and flow. Your life moves in natural cycles, and when you honour your own rhythm, everything flows more easily." to "Ритъм (5-15): Каналът на естествените ритми и времето. Имаш уникална връзка с времето и потока. Животът ти се движи в естествени цикли и когато зачиташ собствения си ритъм, всичко тече по-лесно."
        map[key(6,59)]  = "Mating (6-59): The tribal channel of sexuality and intimacy. You have a powerful drive for deep bonding and connection. Your energy naturally breaks down barriers to create genuine intimacy and trust." to "Чифтосване (6-59): Племенният канал на сексуалността и интимността. Имаш мощен стремеж към дълбока връзка и контакт. Твоята енергия естествено разрушава бариерите за създаване на истинска интимност и доверие."
        map[key(7,31)]  = "The Alpha (7-31): The channel of influence and leadership. You have a natural ability to guide others toward the future. Your voice carries the energy of the alpha — people instinctively look to you for direction." to "Алфата (7-31): Каналът на влиянието и лидерството. Имаш естествена способност да насочваш другите към бъдещето. Гласът ти носи енергията на алфата — хората инстинктивно гледат към теб за посока."
        map[key(9,52)]  = "Concentration (9-52): The channel of focus and detail. You have the ability to concentrate deeply and notice what others miss. Your gift is sustained attention — staying with something until you truly understand it." to "Концентрация (9-52): Каналът на фокуса и детайлите. Имаш способността да се концентрираш дълбоко и да забелязваш онова, което другите пропускат. Твоят дар е продължителното внимание — оставаш с нещо докато наистина го разбереш."
        map[key(10,20)] = "Awakening (10-20): Integration channel. The behaviour of the Self expressed in the now. You integrate love of self with present-moment awareness — your authentic behaviour naturally communicates your direction." to "Пробуждане (10-20): Интеграционен канал. Поведението на Аза изразено в момента. Интегрираш себелюбието с осъзнаването в момента — автентичното ти поведение естествено съобщава посоката ти."
        map[key(10,34)] = "Exploration (10-34): Integration channel. The power of following one's own convictions. You have an enormous drive to live by your own rules and explore what it means to be authentically yourself, regardless of others' opinions." to "Изследване (10-34): Интеграционен канал. Силата на следването на собствените убеждения. Имаш огромен стремеж да живееш по своите правила и да изследваш какво означава да бъдеш автентично себе си, независимо от мнението на другите."
        map[key(10,57)] = "Perfected Form (10-57): Integration channel. Intuitive survival through authentic behaviour. You have a powerful intuition guiding you to behave in ways that are healthy and safe for you in any moment." to "Перфектна Форма (10-57): Интеграционен канал. Интуитивно оцеляване чрез автентично поведение. Имаш мощна интуиция, която те насочва да се държиш по начини, здравословни и безопасни за теб в момента."
        map[key(11,56)] = "Curiosity (11-56): The channel of the storyteller. You are driven to seek out new ideas and experiences, and to share them as stories. Your gift is inspiring others with the stimulating diversity of human experience." to "Любопитство (11-56): Каналът на разказвача. Движен си да търсиш нови идеи и преживявания и да ги споделяш като истории. Твоят дар е да вдъхновяваш другите с вълнуващото многообразие на човешкото преживяване."
        map[key(12,22)] = "Openness (12-22): The channel of grace and emotional expression. Your emotions speak through sound, tone, and creative expression. When you are in the right emotional space, your voice has the power to move others deeply." to "Откритост (12-22): Каналът на грацията и емоционалното изразяване. Твоите емоции говорят чрез звук, тон и творческо изразяване. Когато си в правилното емоционално пространство, гласът ти има силата дълбоко да трогне другите."
        map[key(13,33)] = "The Prodigal (13-33): The channel of the witness. You are a keeper of secrets and stories, designed to listen deeply and then share the wisdom of what you've witnessed at the right moment." to "Блудният Син (13-33): Каналът на свидетеля. Ти си пазител на тайни и истории, проектиран да слушаш дълбоко и после да споделяш мъдростта на онова, на което си бил свидетел, в правилния момент."
        map[key(16,48)] = "The Wavelength (16-48): The channel of talent and mastery. You have access to deep wells of skill and potential. Your gift is the ability to develop mastery in a chosen area through repeated practice and refinement." to "Дължината на Вълната (16-48): Каналът на таланта и майсторството. Имаш достъп до дълбоки извори на умения и потенциал. Твоят дар е способността да развиваш майсторство в избрана област чрез повторна практика и усъвършенстване."
        map[key(17,62)] = "Acceptance (17-62): The channel of logical organisation and opinion. You have a natural ability to organise information into logical patterns and express structured opinions. You see how things should work and communicate this clearly." to "Приемане (17-62): Каналът на логическата организация и мнението. Имаш естествена способност да организираш информацията в логически модели и да изразяваш структурирани мнения. Виждаш как нещата трябва да работят и го комуникираш ясно."
        map[key(18,58)] = "Judgment (18-58): The channel of insightful correction. You have a keen eye for what is wrong or could be improved. This gift, when correctly applied, serves the health and vitality of the whole community." to "Съждение (18-58): Каналът на проницателната корекция. Имаш остро oko за онова, което е грешно или може да бъде подобрено. Този дар, когато е приложен правилно, служи на здравето и жизнеността на цялата общност."
        map[key(19,49)] = "Synthesis (19-49): The tribal channel of sensitivity to needs. You have a profound sensitivity to the basic needs — food, shelter, belonging — of your community. Your gift is recognising and providing for what the tribe needs to thrive." to "Синтез (19-49): Племенният канал на чувствителността към нуждите. Имаш дълбока чувствителност към основните нужди — храна, подслон, принадлежност — на твоята общност. Твоят дар е разпознаването и осигуряването на онова, от което племето се нуждае, за да просперира."
        map[key(20,57)] = "The Brainwave (20-57): Integration channel. Intuitive awareness expressed in the present moment. You have a gift for instantly knowing what is right and safe in any situation, communicating this awareness through your presence and being." to "Мозъчната Вълна (20-57): Интеграционен канал. Интуитивното осъзнаване изразено в настоящия момент. Имаш дар да знаеш незабавно кое е правилно и безопасно в дадена ситуация, предавайки това осъзнаване чрез своето присъствие и битие."
        map[key(21,45)] = "Money (21-45): The tribal channel of material mastery. You have the will and the ability to control material resources, manage money, and ensure the tribe's material wellbeing. You need to be in charge of your own resources." to "Пари (21-45): Племенният канал на материалното майсторство. Имаш волята и способността да контролираш материалните ресурси, да управляваш пари и да гарантираш материалното благополучие на племето. Трябва да отговаряш за собствените си ресурси."
        map[key(23,43)] = "Structuring (23-43): The channel of unique individual insights. You receive sudden, inexplicable knowing — flashes of insight that can restructure how things are understood. The challenge is translating these unique insights into words others can grasp." to "Структуриране (23-43): Каналът на уникалните индивидуални прозрения. Получаваш внезапно, необяснимо знание — проблясъци на прозрение, които могат да преструктурират начина, по който нещата се разбират. Предизвикателството е да преведеш тези уникални прозрения в думи, които другите могат да схванат."
        map[key(24,61)] = "Awareness (24-61): The channel of mental mysticism. You feel constant internal pressure to know the unknowable — to find the truth behind existence. This channel creates a restless, seeking mind drawn to deep mysteries." to "Осъзнаване (24-61): Каналът на менталния мистицизъм. Чувстваш постоянен вътрешен натиск да познаеш непознаваемото — да намериш истината зад съществуването. Този канал създава неспокоен, търсещ ум, привлечен от дълбоки тайни."
        map[key(25,51)] = "Initiation (25-51): The channel of cosmic shock. You have access to universal love and the capacity to be initiated through unexpected, shocking experiences that ultimately open you to higher consciousness." to "Посвещение (25-51): Каналът на космическия шок. Имаш достъп до универсалната любов и способността да бъдеш посветен чрез неочаквани, шокиращи преживявания, които в крайна сметка те отварят към по-висше съзнание."
        map[key(26,44)] = "Surrender (26-44): The tribal channel of the transmitter. You have a natural ability to sell, persuade, and communicate what is valuable to your community. Your instinct for what the tribe needs — and your charm in delivering it — is your gift." to "Предаване (26-44): Племенният канал на предавателя. Имаш естествена способност да продаваш, убеждаваш и комуникираш онова, което е ценно за твоята общност. Твоят инстинкт за онова, от което племето се нуждае — и чара ти в предоставянето му — е твоят дар."
        map[key(27,50)] = "Preservation (27-50): The channel of caring and values. You are deeply attuned to what nurtures and preserves life. You have the instinct to care for others — whether people, animals, or cultural values — and an innate sense of what is right." to "Съхранение (27-50): Каналът на грижата и ценностите. Ти си дълбоко настроен към онова, което подхранва и запазва живота. Имаш инстинкт да се грижиш за другите — хора, животни или културни ценности — и вродено чувство за онова, което е правилно."
        map[key(28,38)] = "Struggle (28-38): The channel of purposeful struggle. You are here to fight for what has meaning and value. Life may feel like a constant struggle, but this struggle is purposeful — it reveals what is truly worth fighting for." to "Борба (28-38): Каналът на целенасочената борба. Ти си тук, за да се бориш за онова, което има смисъл и ценност. Животът може да изглежда като постоянна борба, но тази борба е целенасочена — тя разкрива онова, за което наистина си струва да се борим."
        map[key(29,46)] = "Discovery (29-46): The channel of dedication and the love of the body. You have the sacral energy to commit deeply to experiences and people. Through committed engagement, you discover the love of being in a body — of being alive." to "Откритие (29-46): Каналът на преданието и любовта към тялото. Имаш сакрална енергия да се посветиш дълбоко на преживявания и хора. Чрез ангажирано участие откриваш любовта да бъдеш в тяло — да бъдеш жив."
        map[key(30,41)] = "Recognition (30-41): The channel of fantasy and desire. You have a constant pressure to feel new emotions and experiences. You are fuelled by desire — the dream of what could be — which drives you to seek out new adventures." to "Признание (30-41): Каналът на фантазията и желанието. Имаш постоянен натиск да изпитваш нови емоции и преживявания. Движен си от желание — мечтата за онова, което може да бъде — което те тласка да търсиш нови приключения."
        map[key(32,54)] = "Transformation (32-54): The channel of driven ambition. You have a constant pressure to transform, rise, and succeed. You instinctively sense what will survive and thrive over time, and you are driven to climb toward your highest potential." to "Трансформация (32-54): Каналът на движещото честолюбие. Имаш постоянен натиск да се трансформираш, издигаш и успяваш. Инстинктивно усещаш онова, което ще оцелее и ще процъфтее с течение на времето, и си движен да се издигаш към най-высокия си потенциал."
        map[key(34,57)] = "Power (34-57): Integration channel. Pure power to survive guided by intuition. You have an enormously powerful sacral energy directed by splenic intuition. Your body knows instantly what is safe and healthy, and you have the power to act on that knowing." to "Сила (34-57): Интеграционен канал. Чиста сила за оцеляване, ръководена от интуиция. Имаш изключително мощна сакрална енергия, насочвана от спленичната интуиция. Тялото ти знае незабавно кое е безопасно и здравословно, и имаш силата да действаш на базата на това знание."
        map[key(35,36)] = "Transitoriness (35-36): The channel of emotional experience. You are driven to seek new human experiences — the full range of what it means to be alive. Your emotional depth comes from having lived through many different experiences." to "Преходност (35-36): Каналът на емоционалното преживяване. Движен си да търсиш нови човешки преживявания — целия спектър на онова, което означава да бъдеш жив. Емоционалната ти дълбочина идва от преживяването на много различни преживявания."
        map[key(37,40)] = "Community (37-40): The tribal channel of bargaining and community. You have the gift of creating community through clear agreements and mutual support. Your sense of belonging is strong, and you honour the bonds you make." to "Общност (37-40): Племенният канал на договарянето и общността. Имаш дара да създаваш общност чрез ясни договорености и взаимна подкрепа. Чувството ти за принадлежност е силно и зачиташ връзките, които правиш."
        map[key(39,55)] = "Emoting (39-55): The channel of individual emotional provocation. Your energy provokes and stirs up the emotions of others to help them find their spirit and passion. You can irritate and inspire in equal measure." to "Емотиране (39-55): Каналът на индивидуалната емоционална провокация. Твоята енергия провокира и разбунва емоциите на другите, за да им помогне да намерят духа и страстта си. Можеш да дразниш и вдъхновяваш в равна мяра."
        map[key(42,53)] = "Maturation (42-53): The channel of life cycles and completion. You have a strong pressure to begin new cycles and — crucially — to complete them fully. Each cycle brings growth and wisdom when honoured from beginning to end." to "Зреене (42-53): Каналът на жизнените цикли и завършването. Имаш силен натиск да започваш нови цикли и — решаващо — да ги завършваш напълно. Всеки цикъл носи растеж и мъдрост, когато е почетен от началото до края."
        map[key(47,64)] = "Abstraction (47-64): The channel of mental abstraction. You are driven to make sense of the past — to find the pattern and meaning behind what has happened. Your gift is transforming confusing experiences into abstract wisdom." to "Абстракция (47-64): Каналът на менталната абстракция. Движен си да намериш смисъл в миналото — да откриеш модела и значението зад онова, което се е случило. Твоят дар е трансформирането на объркващи преживявания в абстрактна мъдрост."

        map.toMap()
    }

    private fun channelKey(ch: HdChannel) = "${minOf(ch.a, ch.b)}-${maxOf(ch.a, ch.b)}"

    fun channelDescriptionFor(ch: HdChannel): Pair<String, String>? = channelInfo[channelKey(ch)]

    fun profileKey(personalityLine: Int, designLine: Int) = "$personalityLine/$designLine"
}
