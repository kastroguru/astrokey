package eu.kastroguru.astrodiary.domain.interpretation

import eu.kastroguru.astrodiary.domain.model.ZodiacSign

/**
 * How each sign colours whatever falls in it — the "style", not the topic. Combined with
 * [PlanetMeanings] (the topic) this already says something sensible about any placement, which is
 * what lets the app cover all 156 planet-in-sign combinations before every one is written by hand.
 */
object SignMeanings {

    val bySign: Map<ZodiacSign, Bilingual> = mapOf(
        ZodiacSign.ARIES to t(
            "Aries energy is direct and immediate: it prefers action to planning and learns by doing. Initiative and courage often lead, but so can impatience and impulsiveness. Aries brings a pioneering drive and readiness to take risks; the constructive challenge is to temper speed with a little foresight and the willingness to adjust course, so that bold starts become meaningful progress rather than repetitive false starts.",
            "Натискът на Овен е директен и незабавен: предпочита действие пред план и научава чрез опит. Инициативността и смелостта често водят, но може да има и нетърпение, импулсивност или склонност да се прескача подготовката. Овенската енергия вдъхва първична решимост и готовност да се поеме риск; полезно е да култивирате търпение и да оставяте място за корекции, за да превърнете импулса в устойчиво постижение."
        ),
        ZodiacSign.TAURUS to t(
            "Taurus moves slowly and steadily, favoring what can be touched and kept. It values comfort, security and sensory pleasure; loyalty and persistence are strengths, but may harden into stubbornness or resistance to necessary change. Practically, Taurus benefits from building reliable routines and tangible assets, while learning to welcome gradual adaptation so attachment becomes a foundation rather than a constraint.",
            "Телецът действа бавно и устойчиво, търсейки неща, които може да пипне и задържи. Стремежът към комфорт, сигурност и сетивни удоволствия силно влияе на решенията — лоялност и упоритост са предимство, но могат да преминат в инат и пасивност при нужда от промяна. Полезно е да цените материалните и емоционални ресурси, да градите устойчиви навици и да позволявате промяната да се случва постепенно, вместо да я блокирате рязко."
        ),
        ZodiacSign.GEMINI to t(
            "Gemini is curious and word-oriented: it engages the world through speech and information. Its hunger for variety and mental stimulation can make it restless and sometimes avoidant of depth, but it excels at connecting ideas and adapting to new contexts. The practical task is to channel curiosity with focus: follow interest threads and organize what you learn so many conversations and contacts become coherent opportunities rather than scattered noise.",
            "Близнаците са любопитни и вербални — обръщат се към света чрез слово и информация. Нуждата от разнообразие и интелектуален обмен може да ги прави леко разсеяни и склонни да избягват дълбочина, но те са отлични в свързването на идеи и адаптацията към нови контексти. Практическата страна е да използват любопитството целенасочено: да следват нишки на интерес и да структурират знанията, за да превърнат многото контакти в реални възможности."
        ),
        ZodiacSign.CANCER to t(
            "sensitively and protectively — responses often filter through feeling and instinct; it tends to avoid head-on confrontation and moves sideways, gathering and keeping memories and impressions. This style points toward caring for home, close relationships, and emotional security, yet it can withdraw or become overprotective when feeling vulnerable. Practical guidance: practice clear communication of needs and boundaries so that sensitivity becomes a sustainable strength rather than a limiting retreat.",
            "чувствително и закрилнически — реакциите често минават през емоцията и интуицията; предпочита да заобикаля конфронтацията и да действа отстрани, като събира и пази спомени и усещания. Насочва към грижа за дома, близките и емоционалната безопасност, но може да се затвори или да стане прекалено защитно в отговор на ранимост. Практически съвет: да се упражнява ясна комуникация на нуждите и границите, за да превърне чувствителността в устойчив ресурс."
        ),
        ZodiacSign.LEO to t(
            "warmly and visibly — expression is often generous and dramatic; it wants to be seen and appreciated in the act of creating or leading. This tendency favors creativity, leadership, and joyful giving, but can lean on external validation or come across as demanding when recognition is lacking. Practical suggestion: cultivate an internal sense of worth and channel visibility into inspiring others rather than seeking constant affirmation.",
            "топло и показно — изразът често е щедър и драматичен; има нужда да бъде видян и признат в процеса на себеизразяване. Насочва към творчество, лидeрство и радост от даването, но може да търси външно одобрение или да изглежда взискателно, когато не получава признание. Практически съвет: да култивира вътрешно чувство за стойност и да използва видимостта си за вдъхновение на другите, вместо за потвърждение."
        ),
        ZodiacSign.VIRGO to t(
            "carefully and usefully — it notices what others miss and directs effort toward fixing and refining. Often self-critical, it can be weighed down by perfectionism or excessive analysis. This mode favors efficiency, service, and healthy routines. Practical advice: prioritize tasks and practice accepting “good enough,” turning critique into constructive care rather than self-defeating judgment.",
            "внимателно и ползотворно — забелязва детайли, които другите пропускат, и насочва усилията към поправка и усъвършенстване. Често е най-строг към себе си и може да се задушава от перфекционизъм или прекален анализ. Насочва към ефективност, услуга и здравословни навици. Практически съвет: да приоритизира и да практикува приемане на „достатъчно добро“, като превърне критиката в конструктивна грижа вместо в обезсърчение."
        ),
        ZodiacSign.LIBRA to t(
            "considerately and together — oriented toward balance, reciprocity, and aesthetics; it brings the other into decisions and values harmony. It may lose itself trying to please or avoid conflict, sacrificing personal preferences. This tendency supports ethical, diplomatic relationships and a pursuit of beauty. Practical tip: practice clarity about personal boundaries and seek a balance between empathy and authentic self-expression.",
            "справедливо и в партньорство — ориентира се към равновесие, взаимност и естетика; включва другия в решението и оценява хармонията. Може да се загуби в стремежа да угоди или да избягва конфликти, жертвейки собствените предпочитания. Насочва към етични и дипломатични отношения, както и към стремеж към красота. Практически съвет: да упражнява яснота в личните граници и да търси баланс между съпричастие и собствена автентичност."
        ),
        ZodiacSign.SCORPIO to t(
            "intensely and thoroughly — it doesn’t do things by halves; it guards its inner life and often sees through surface appearances. This style points to transformation, deep intimacy, and psychological clearing, but can run into jealousy, control, or secrecy. Practical guidance: cultivate gradual openness and trust, channel intensity into therapeutic or creative work rather than manipulation or withdrawal.",
            "интензивно и до край — работи дълбоко, без половинчатост; пази своя вътрешен свят и има проницателност, която вижда зад фасадите. Насочва към трансформация, интимност и психическо изчистване, но може да се сблъска с ревност, контрол или тайни. Практически съвет: да се упражнява в откровеност и доверие поетапно, да канализира интензитета в терапевтични или творчески процеси вместо в манипулация или изолация."
        ),
        ZodiacSign.SAGITTARIUS to t(
            "broadly and with faith — it seeks meaning, perspective, and freedom; it needs room and often learns through travel, ideas, and ideals. It favors possibility over fixed security and may overlook details or commitments in pursuit of the horizon. This orientation supports philosophy, adventure, and expansive thinking. Practical tip: pair curiosity with responsibility so freedom doesn’t become aimless scattering.",
            "широко и с вяра — търси смисъл, перспектива и свобода; има нужда от простор и често учи чрез пътуване, идеи и идеали. Предпочита възможността пред постоянната сигурност и може да пренебрегне детайлите или ангажиментите в полза на хоризонта. Насочва към философия, приключение и разширяване на хоризонтите. Практически съвет: да съчетава любопитството с отговорност, така че свободата да не прераства в разпиляност."
        ),
        ZodiacSign.CAPRICORN to t(
            "patiently and with a plan — it builds steadily, values discipline, and honors work that pays off over time. It often shoulders more responsibility than it admits, and can be reserved, prioritizing career or duty over personal needs. This approach favors durable achievement and resource structuring. Practical suggestion: balance ambition with self-care and allow small rest stops while pursuing long-term goals.",
            "търпеливо и с план — гради стъпка по стъпка, оценява дисциплината и работата, която дава резултат във времето. Носи отговорност и често поема повече, отколкото желае да признае; може да бъде резервиран и да поставя кариерата или дълга пред личните нужди. Насочва към устойчиви постижения и структуриране на ресурси. Практически съвет: да балансира амбицията с грижа за себе си и да си позволява малки почивки по пътя към целите."
        ),
        ZodiacSign.AQUARIUS to t(
            "independently and at a distance — it thinks in systems, innovates, and often takes an abstract perspective; it resists being ordered and is guided by principles and collective ideals. It can appear emotionally detached or prioritize ideas over personal ties. This mode supports reform, friendships, and group-minded thinking. Practical advice: couple visionary thinking with personal empathy to make ideas practical and sustainable.",
            "независимо и дистанционно — мисли в системи, иновативно и често с абстрактна перспектива; съпротивлява се на налагани правила и се ръководи от принципи и колективни идеали. Може да изглежда емоционално отдръпнат е или да поставя идеята пред личните връзки. Насочва към реформа, приятелства и групова мисъл. Практически съвет: да свързва визионерството с емпатия на лично ниво, за да направи идеите приложими и устойчиви."
        ),
        ZodiacSign.PISCES to t(
            "Pisces often express a gentle, porous presence that absorbs the emotional tone of a room and can give until depleted. This sign points to vivid imagination, empathy and a spiritual or artistic lean. Practically, it suggests cultivating clear personal boundaries and creative outlets—art, music or caring professions—so sensitivity becomes a resource rather than a drain. Tensions can arise from idealizing others or avoiding practical needs; intentional self-care helps maintain balance.",
            "Риби често са меки, емпатични и с неясни граници — те лесно попиват настроенията около себе си и могат да дават до изтощение. Знакът насочва към богата вътрешна фантазия, духовен чар и склонност към състрадание. Практически, това означава нужда от ясни лични граници и изразни канали: творческа работа, музика или грижа за другите могат да бъдат лек за душата, стига да се намери баланс между даване и възстановяване."
        ),
    )

    fun of(sign: ZodiacSign): Bilingual? = bySign[sign]
}
