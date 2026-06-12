package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Transit-to-natal planet interpretations.
 * Key: "${transitKey}_${natalKey}"  e.g. "uranus_mars"
 * Value: Pair<English, Bulgarian>
 *
 * Structure (two-tier):
 *   general["uranus_mars"]           → shown always (planet-pair level)
 *   specific["uranus_mars_0"]        → shown additionally when aspect = conjunction (0°)
 *   specific["uranus_mars_90"]       → square, etc.
 */
object TransitInterpretations {

    private val en = "en"   // unused sentinel — helps readability of buildMap blocks

    // ── General (planet-pair) interpretations ─────────────────────────────────
    val general: Map<String, Pair<String, String>> = buildMap {

        // helper
        fun p(k: String, en: String, bg: String) = put(k, en to bg)

        // ── TRANSIT SUN ───────────────────────────────────────────────────────
        p("sun_sun",
            "Transit Sun to Natal Sun (Solar Return): Your personal new year begins. Fresh purpose and vitality are available. Set intentions for the year ahead — this brief window carries powerful self-renewal energy.",
            "Транзитно Слънце към Натално Слънце (Слънчево завръщане): Вашата лична нова година започва. Свежа целенасоченост и жизненост са достъпни. Поставете намерения за годината напред — този кратък прозорец носи мощна енергия за самообновяване.")
        p("sun_moon",
            "Transit Sun to Natal Moon: Conscious will and emotional nature come into dialogue. You understand more clearly what you need emotionally versus what you simply want. Relationships with women or mother figures may be highlighted.",
            "Транзитно Слънце към Натална Луна: Съзнателната воля и емоционалната природа влизат в диалог. Разбирате по-ясно от какво наистина се нуждаете емоционално. Отношенията с жени или майчини фигури могат да бъдат подчертани.")
        p("sun_mercury",
            "Transit Sun to Natal Mercury: Mental clarity aligns with your sense of identity. Your thoughts and words carry extra confidence and impact. An excellent time for communication, presentations, and important conversations.",
            "Транзитно Слънце към Натален Меркурий: Менталната яснота се съгласува с вашата идентичност. Мислите и думите ви носят допълнително самочувствие и въздействие. Отлично време за комуникация и важни разговори.")
        p("sun_venus",
            "Transit Sun to Natal Venus: Your sense of self shines through love and beauty. Relationships and creative expression are warmly lit. Social interactions are more magnetic and pleasurable — express appreciation to those you care about.",
            "Транзитно Слънце към Натална Венера: Вашето Аз блести чрез любов и красота. Отношенията и творческото изразяване са топло осветени. Социалните взаимодействия са по-магнетични — изразете признателност към тези, за които се грижите.")
        p("sun_mars",
            "Transit Sun to Natal Mars: Vitality and drive surge — you feel powerful and motivated to act. Channel this heightened energy productively. Watch for irritability or unnecessary confrontations if the energy is not well-directed.",
            "Транзитно Слънце към Натален Марс: Жизнеността и стремежът нарастват — чувствате се мощни и мотивирани да действате. Насочете тази повишена енергия продуктивно. Внимавайте за раздразнителност ако енергията не е добре насочена.")
        p("sun_jupiter",
            "Transit Sun to Natal Jupiter: Optimism and opportunity expand around you. Confidence peaks and doors open more easily. An excellent transit for new beginnings, presenting yourself to the world, and taking inspired risks.",
            "Транзитно Слънце към Натален Юпитер: Оптимизмът и възможностите се разширяват около вас. Самочувствието достига връх и вратите се отварят по-лесно. Отличен транзит за нови начала и поемане на вдъхновени рискове.")
        p("sun_saturn",
            "Transit Sun to Natal Saturn: Reality meets ambition — you are asked to work honestly for what you want. A grounding transit that rewards discipline. If you have been drifting, Saturn gently calls you back to responsibility.",
            "Транзитно Слънце към Натален Сатурн: Реалността среща амбицията — призовани сте да работите честно за онова, което искате. Заземяващ транзит, който възнаграждава дисциплината. Ако сте се отдалечили, Сатурн ви връща към отговорността.")
        p("sun_uranus",
            "Transit Sun to Natal Uranus: A sudden impulse toward authenticity and freedom awakens. Restlessness with routine is natural. Changes that occur now, though possibly disruptive, move you toward a truer expression of self.",
            "Транзитно Слънце към Натален Уран: Внезапен импулс към автентичност и свобода се пробужда. Безпокойството от рутината е естествено. Промените, настъпили сега, ви движат към по-истинско себеизразяване.")
        p("sun_neptune",
            "Transit Sun to Natal Neptune: Spiritual sensitivity is heightened and the boundaries of self become permeable. Intuition and imagination flourish. Avoid making binding decisions while in a fog — creative and spiritual work prospers.",
            "Транзитно Слънце към Натален Нептун: Духовната чувствителност е засилена и границите на Аза стават пропускливи. Интуицията и въображението процъфтяват. Избягвайте важни решения в мъгла — творческата и духовна работа просперират.")
        p("sun_pluto",
            "Transit Sun to Natal Pluto: Deep transformation of identity is underway. Power dynamics surface and old self-concepts are shed. A potent time to reclaim your authentic authority and release who you no longer are.",
            "Транзитно Слънце към Натален Плутон: Дълбока трансформация на идентичността е в ход. Динамиките на властта изплуват и старите самоконцепции се отхвърлят. Мощно време за възвръщане на автентичната ви власт.")
        p("sun_chiron",
            "Transit Sun to Natal Chiron: A tenderness around identity and the right to shine surfaces. Old wounds about visibility and self-worth are touched. This is an invitation for conscious healing of the core self.",
            "Транзитно Слънце към Натален Хирон: Нежност около идентичността и правото да блестите изплува. Стари рани за видимостта и самооценката са докоснати. Покана за съзнателно лечение на основното Аз.")
        p("sun_rahu",
            "Transit Sun to Natal Rahu: Opportunities aligned with your life's growth direction arrive. The universe illuminates the path your soul is meant to travel. Step toward what feels both exciting and unfamiliar.",
            "Транзитно Слънце към Натален Раху: Пристигат възможности, свързани с посоката на растеж в живота ви. Вселената осветява пътя, по който душата ви трябва да върви. Насочете се към онова, което е едновременно вълнуващо и непознато.")
        p("sun_lilith",
            "Transit Sun to Natal Lilith: Wild, suppressed aspects of identity surface for acknowledgment. You may feel drawn to express something usually hidden or taboo. Authenticity over performance is the invitation.",
            "Транзитно Слънце към Натална Лилит: Диви, потиснати аспекти на идентичността изплуват за признание. Може да се чувствате привлечени да изразите нещо обикновено скрито. Поканата е за автентичност вместо изпълнение.")

        // ── TRANSIT MOON ──────────────────────────────────────────────────────
        p("moon_sun",
            "Transit Moon to Natal Sun: A brief wave of emotional confidence aligns feelings with purpose. A good day for self-care and checking in with what you truly want rather than what is expected of you.",
            "Транзитна Луна към Натално Слънце: Кратка вълна на емоционална увереност съгласува чувствата с целта. Добър ден за самогрижа и проверка на онова, което наистина искате.")
        p("moon_moon",
            "Transit Moon to Natal Moon: Your emotional nature speaks to itself. Old habits, instincts, and subconscious patterns rise to the surface. Note what feelings arise — they reveal your authentic emotional needs.",
            "Транзитна Луна към Натална Луна: Емоционалната ви природа разговаря сама със себе си. Стари навици и инстинкти изплуват на повърхността. Обърнете внимание на чувствата — те разкриват автентичните ви емоционални нужди.")
        p("moon_mercury",
            "Transit Moon to Natal Mercury: Thoughts and feelings merge. You communicate more emotionally and intuitively. Dreams may be vivid. An excellent time for journalling, heartfelt conversations, and intuitive writing.",
            "Транзитна Луна към Натален Меркурий: Мисли и чувства се сливат. Комуникирате по-емоционално и интуитивно. Мечтите могат да бъдат живи. Отлично за дневник, искрени разговори и интуитивно писане.")
        p("moon_venus",
            "Transit Moon to Natal Venus: Affection, warmth, and a need for beauty rise naturally. You want comfort, love, and aesthetics around you. A lovely time for relationships, self-nurturing, and creative activities.",
            "Транзитна Луна към Натална Венера: Обичта, топлината и нуждата от красота нарастват естествено. Искате комфорт, любов и естетика около вас. Прекрасно за взаимоотношения и самогрижа.")
        p("moon_mars",
            "Transit Moon to Natal Mars: Emotional energy is charged — passion and irritability can arrive in quick succession. You feel intensely and act on feelings rapidly. Physical energy needs an outlet; use it rather than reacting impulsively.",
            "Транзитна Луна към Натален Марс: Емоционалната енергия е заредена — страст и раздразнение могат да се редуват бързо. Чувствате интензивно и действате по чувства бързо. Физическата енергия се нуждае от изход.")
        p("moon_jupiter",
            "Transit Moon to Natal Jupiter: Emotional optimism and generosity flow easily. You feel expansive and giving. Social interactions are pleasurable and connections feel abundant. A brief but genuinely joyful transit.",
            "Транзитна Луна към Натален Юпитер: Емоционалният оптимизъм и щедростта текат лесно. Чувствате се разширени и даряващи. Социалните взаимодействия са приятни. Кратък, но наистина радостен транзит.")
        p("moon_saturn",
            "Transit Moon to Natal Saturn: Emotions feel heavy or restricted briefly. Old emotional patterns and limitations surface. Use this window to examine what emotional structures need renegotiating — not to indulge the low feeling.",
            "Транзитна Луна към Натален Сатурн: Емоциите се чувстват тежки или ограничени за кратко. Стари емоционални модели и ограничения изплуват. Използвайте този прозорец за преразглеждане на емоционалните структури.")
        p("moon_uranus",
            "Transit Moon to Natal Uranus: Emotional restlessness and a need for something different arrive suddenly. Mood shifts happen quickly. Expect the unexpected in domestic or emotional life — embrace rather than resist the disruption.",
            "Транзитна Луна към Натален Уран: Емоционалното безпокойство и нужда от промяна пристигат внезапно. Настроенията се сменят бързо. Очаквайте неочакваното в домашния или емоционален живот.")
        p("moon_neptune",
            "Transit Moon to Natal Neptune: Emotions become deeply intuitive and spiritually sensitive. The line between your feelings and others' blurs — be aware of absorbing moods. Dreams, art, and spiritual practice are beautifully supported.",
            "Транзитна Луна към Натален Нептун: Емоциите стават дълбоко интуитивни и духовно чувствителни. Границата между вашите чувства и тези на другите се размива. Мечтите, изкуството и духовната практика са прекрасно поддържани.")
        p("moon_pluto",
            "Transit Moon to Natal Pluto: Emotional intensity peaks. Deep feelings and subconscious material rise unbidden. This brief but powerful transit can bring psychological breakthroughs or confrontations with emotional shadow material.",
            "Транзитна Луна към Натален Плутон: Емоционалната интензивност достига връх. Дълбоки чувства и подсъзнателен материал изплуват. Кратък, но мощен транзит, който може да донесе психологически пробиви.")
        p("moon_chiron",
            "Transit Moon to Natal Chiron: A tender moment touches an old emotional wound. Something in the present echoes a past hurt. Sitting with the feeling rather than pushing it away allows gentle healing to begin.",
            "Транзитна Луна към Натален Хирон: Нежен момент докосва стара емоционална рана. Нещо в настоящето отеква в миналото. Оставането с чувството вместо избутването му позволява нежно лечение да започне.")
        p("moon_rahu",
            "Transit Moon to Natal Rahu: Emotionally, you are drawn toward new territory that feels both exciting and unfamiliar. Trust the pull toward growth even if it feels uncomfortable.",
            "Транзитна Луна към Натален Раху: Емоционално се привличате към нова територия, която е едновременно вълнуваща и непозната. Доверете се на привличането към растеж дори ако е неудобно.")
        p("moon_lilith",
            "Transit Moon to Natal Lilith: Raw, instinctual emotional energy surfaces. Emotions you have deemed 'unacceptable' — rage, wildness, intense desire — demand acknowledgment rather than suppression.",
            "Транзитна Луна към Натална Лилит: Сурова, инстинктивна емоционална енергия изплува. Емоции, считани за 'неприемливи' — гняв, дивота, интензивно желание — изискват признание вместо потискане.")

        // ── TRANSIT MERCURY ───────────────────────────────────────────────────
        p("mercury_sun",
            "Transit Mercury to Natal Sun: Mental focus aligns with life purpose. You think and speak with greater confidence about who you are. Excellent for self-expression, sharing your vision, or any communication that represents you.",
            "Транзитен Меркурий към Натално Слънце: Менталният фокус се съгласува с жизнената цел. Мислите и говорите с по-голяма увереност за кои сте. Отлично за себеизразяване и всяка комуникация, която ви представя.")
        p("mercury_moon",
            "Transit Mercury to Natal Moon: Thoughts and emotions are in fruitful conversation. You analyse feelings or speak about emotional needs clearly. Journalling, therapy, and honest emotional conversations are well-supported.",
            "Транзитен Меркурий към Натална Луна: Мислите и емоциите са в плодотворен разговор. Анализирате чувствата или говорите ясно за емоционалните нужди. Воденето на дневник и честните разговори са добре подкрепени.")
        p("mercury_mercury",
            "Transit Mercury to Natal Mercury: Mental agility peaks — your mind is sharp and communicative. An influx of information, messages, and short trips may arrive. Excellent for learning, planning, negotiating, and any mental work.",
            "Транзитен Меркурий към Натален Меркурий: Менталната гъвкавост достига връх — умът е остър и комуникативен. Може да пристигнат информация, съобщения и кратки пътувания. Отлично за учене, планиране и преговори.")
        p("mercury_venus",
            "Transit Mercury to Natal Venus: Thoughts turn naturally to love, beauty, and relationship. You express affection gracefully through words. Love letters, heartfelt compliments, and diplomatic relationship conversations flow naturally.",
            "Транзитен Меркурий към Натална Венера: Мислите се обръщат естествено към любовта, красотата и отношенията. Изразявате обич изящно чрез думи. Любовни писма и дипломатични разговори за взаимоотношения текат естествено.")
        p("mercury_mars",
            "Transit Mercury to Natal Mars: Mind and assertiveness combine — you communicate with directness and force. Excellent for debate, advocacy, and negotiations. Watch for arguments ignited by words delivered too sharply.",
            "Транзитен Меркурий към Натален Марс: Умът и настойчивостта се съчетават — комуникирате с директност и сила. Отлично за дебати и преговори. Внимавайте за спорове, предизвикани от твърде остри думи.")
        p("mercury_jupiter",
            "Transit Mercury to Natal Jupiter: Thinking expands toward the big picture. You communicate with enthusiasm and vision. Education, philosophy, travel planning, and broad-minded conversations are favoured.",
            "Транзитен Меркурий към Натален Юпитер: Мисленето се разширява към голямата картина. Комуникирате с ентусиазъм и визия. Образованието, философията и разговорите с широко мислене са благоприятни.")
        p("mercury_saturn",
            "Transit Mercury to Natal Saturn: Thinking becomes serious, disciplined, and precise. Mental work, planning, and detailed tasks are well-supported. Channel the focus productively — avoid slipping into rumination or worry.",
            "Транзитен Меркурий към Натален Сатурн: Мисленето става сериозно, дисциплинирано и прецизно. Менталната работа, планирането и детайлните задачи са добре поддържани. Насочете фокуса продуктивно — избягвайте да се плъзнете в тревожност.")
        p("mercury_uranus",
            "Transit Mercury to Natal Uranus: Brilliant, unconventional ideas flash into awareness. Expect unexpected news or sudden shifts in perspective. Your communication becomes inventive. Open your mind to ideas that seem initially strange.",
            "Транзитен Меркурий към Натален Уран: Брилянтни, нестандартни идеи проблясват в съзнанието. Очаквайте неочаквани новини или внезапни промени в перспективата. Отворете ума си за идеи, изглеждащи първоначално странни.")
        p("mercury_neptune",
            "Transit Mercury to Natal Neptune: Intuitive thinking and imagination are heightened, but logical clarity may blur. Perfect for creative writing, poetry, and inspired thought. Avoid important decisions or contracts in a mental fog.",
            "Транзитен Меркурий към Натален Нептун: Интуитивното мислене и въображението са засилени, но логическата яснота може да се размие. Перфектно за творческо писане. Избягвайте важни решения или договори в ментална мъгла.")
        p("mercury_pluto",
            "Transit Mercury to Natal Pluto: Thinking goes deep — surface conversations no longer satisfy. You want to understand the hidden truth beneath appearances. Research, psychological insight, and transformative conversations are well-supported.",
            "Транзитен Меркурий към Натален Плутон: Мисленето отива дълбоко — повърхностните разговори вече не удовлетворяват. Искате да разберете скрита истина. Изследванията, психологическото проникване и трансформативните разговори са добре поддържани.")
        p("mercury_chiron",
            "Transit Mercury to Natal Chiron: Communication around old wounds surfaces. Words that once hurt you, or beliefs formed in pain, are ready to be examined. Healing through expression — speaking or writing the unspoken truth — is available.",
            "Транзитен Меркурий към Натален Хирон: Комуникацията около стари рани изплува. Думи, които някога са ви наранявали, са готови за изследване. Лечение чрез изразяване — говорене или писане на неизказаната истина — е достъпно.")
        p("mercury_rahu",
            "Transit Mercury to Natal Rahu: Information and ideas aligned with your growth direction arrive. Messages you receive now may point toward your evolutionary path. Stay receptive to unexpected communications.",
            "Транзитен Меркурий към Натален Раху: Пристигат информация и идеи, свързани с посоката ви на растеж. Съобщенията, получени сега, може да сочат към еволюционния ви път.")
        p("mercury_lilith",
            "Transit Mercury to Natal Lilith: Taboo thoughts and suppressed truths demand expression. You may feel compelled to say what is usually left unsaid. Authentic communication over socially acceptable performance.",
            "Транзитен Меркурий към Натална Лилит: Табуирани мисли и потиснати истини изискват изразяване. Може да се чувствате принудени да кажете онова, което обикновено остава неказано.")

        // ── TRANSIT VENUS ─────────────────────────────────────────────────────
        p("venus_sun",
            "Transit Venus to Natal Sun: Love and beauty illuminate your sense of self. You feel more attractive and magnetic. A naturally pleasant transit that opens social doors and makes the world feel warmer toward you.",
            "Транзитна Венера към Натално Слънце: Любовта и красотата осветяват усещането ви за Аз. Чувствате се по-привлекателни и магнетични. Естествено приятен транзит, отварящ социални врати.")
        p("venus_moon",
            "Transit Venus to Natal Moon: Emotional warmth, comfort, and nurturing love are highlighted. You seek and attract tender connections. Domestic life feels beautiful. Self-care and pleasure activities nourish your emotional wellbeing.",
            "Транзитна Венера към Натална Луна: Подчертани са емоционалната топлота, комфортът и любящата грижа. Търсите и привличате нежни връзки. Домашният живот се чувства красив. Самогрижата подхранва емоционалното ви благополучие.")
        p("venus_mercury",
            "Transit Venus to Natal Mercury: Love and intelligence blend. You communicate with warmth and diplomacy. Romantic messages, heartfelt writing, and negotiations where your charm can shine are beautifully supported.",
            "Транзитна Венера към Натален Меркурий: Любовта и интелигентността се смесват. Комуникирате с топлота и дипломатичност. Романтичните съобщения и преговорите, при които чармът ви може да блести, са прекрасно поддържани.")
        p("venus_venus",
            "Transit Venus to Natal Venus (Venus Return): Your values and capacity for love and pleasure are renewed. You feel especially attractive and in tune with what brings joy. Set intentions around love, money, and creative expression.",
            "Транзитна Венера към Натална Венера (Завръщане на Венера): Вашите ценности и способност за любов и удоволствие се обновяват. Чувствате се особено привлекателни. Поставете намерения около любовта, парите и творческото изразяване.")
        p("venus_mars",
            "Transit Venus to Natal Mars: The dance of desire and beauty — romantic and creative passion surge. Relationships feel exciting and physically charged. Channel this energy into art, romance, and collaborative projects.",
            "Транзитна Венера към Натален Марс: Танцът на желанието и красотата — романтичната и творческа страст нарастват. Взаимоотношенията се чувстват вълнуващи. Насочете тази енергия в изкуство, романтика и съвместни проекти.")
        p("venus_jupiter",
            "Transit Venus to Natal Jupiter: Abundance and great fortune in love and finances. You attract blessings and feel naturally lucky. Excellent for financial decisions, social events, and deepening relationships. A genuinely joyful transit.",
            "Транзитна Венера към Натален Юпитер: Изобилие и голям късмет в любовта и финансите. Привличате благословии. Отличен за финансови решения, социални събития и задълбочаване на отношенията.")
        p("venus_saturn",
            "Transit Venus to Natal Saturn: Love meets responsibility and commitment. Relationships are tested for depth and longevity. Shallow connections may fade while genuine bonds deepen. Honest, mature conversations about the future are supported.",
            "Транзитна Венера към Натален Сатурн: Любовта среща отговорността и ангажимента. Взаимоотношенията се тестват за дълбочина и трайност. Повърхностните връзки може да отслабнат, докато истинските се задълбочат.")
        p("venus_uranus",
            "Transit Venus to Natal Uranus: Love becomes electric and unpredictable. You crave excitement and authenticity in relationships. New attractions may arrive suddenly. Existing relationships benefit from a fresh, unconventional approach.",
            "Транзитна Венера към Натален Уран: Любовта става електрическа и непредсказуема. Жадувате вълнение и автентичност. Нови привличания могат да пристигнат внезапно. Съществуващите отношения се ползват от свеж, нестандартен подход.")
        p("venus_neptune",
            "Transit Venus to Natal Neptune: Romantic idealism peaks — you see others through a loving, rose-tinted lens. Beautiful for art, spiritual love, and compassion. Be mindful of projecting perfection or overlooking practical realities.",
            "Транзитна Венера към Натален Нептун: Романтичният идеализъм достига връх — виждате другите с розови очила. Красиво за изкуство, духовна любов и съпричастност. Внимавайте да не проектирате съвършенство или да пропускате практически реалности.")
        p("venus_pluto",
            "Transit Venus to Natal Pluto: Love deepens to a transformative, intense level. Power dynamics in relationships surface for examination. A desire for deeper, more authentic connection replaces superficial arrangements.",
            "Транзитна Венера към Натален Плутон: Любовта се задълбочава до трансформиращо, интензивно ниво. Динамиките на властта в отношенията изплуват. Желанието за по-дълбока, автентична връзка замества повърхностните договорености.")
        p("venus_chiron",
            "Transit Venus to Natal Chiron: Old wounds around love and worthiness are gently touched. A tender invitation for self-compassion in the places where you have felt unloved or unworthy. Healing through beauty and kindness toward yourself.",
            "Транзитна Венера към Натален Хирон: Стари рани около любовта и достойнството са нежно докоснати. Нежна покана за самосъстрадание там, където сте се чувствали нелюбими. Лечение чрез красота и доброта към себе си.")
        p("venus_rahu",
            "Transit Venus to Natal Rahu: New relationships or creative/financial opportunities aligned with your growth path arrive. A connection that feels both new and destined may emerge. Say yes to what expands you.",
            "Транзитна Венера към Натален Раху: Нови отношения или творчески/финансови възможности, свързани с пътя ви на растеж, пристигат. Може да се появи връзка, чувстваща се едновременно нова и предопределена.")
        p("venus_lilith",
            "Transit Venus to Natal Lilith: Desire for authentic, uncensored love and beauty surfaces. You are drawn to what feels real and perhaps unconventional. Embrace your full spectrum of values and desires rather than the 'acceptable' version.",
            "Транзитна Венера към Натална Лилит: Желанието за автентична, нецензурирана любов и красота изплува. Привлечени сте от онова, което се чувства реално. Прегърнете пълния спектър от ценности и желания.")

        // ── TRANSIT MARS ──────────────────────────────────────────────────────
        p("mars_sun",
            "Transit Mars to Natal Sun: Willpower and vitality are supercharged. Drive, ambition, and physical energy peak. A powerful transit for initiating projects and asserting yourself. Channel the fire wisely to avoid burnout or conflict.",
            "Транзитен Марс към Натално Слънце: Волята и жизнеността са свръхзаредени. Стремежът, амбицията и физическата енергия достигат връх. Мощен транзит за инициативи. Насочете огъня мъдро за да избегнете изгаряне или конфликт.")
        p("mars_moon",
            "Transit Mars to Natal Moon: Emotional energy becomes sharp and reactive. You feel things intensely and act on feelings quickly. Passion and protectiveness are heightened. Good for acting on emotional matters long deferred.",
            "Транзитен Марс към Натална Луна: Емоционалната енергия става остра и реактивна. Чувствате интензивно и действате по чувства бързо. Страстта и защитността са засилени. Добро за действия по дълго отлагани емоционални въпроси.")
        p("mars_mercury",
            "Transit Mars to Natal Mercury: Mind and assertiveness combine — sharp, forceful communication emerges. Excellent for debates, negotiations, and advocacy. Watch for arguments sparked by words delivered too bluntly.",
            "Транзитен Марс към Натален Меркурий: Умът и настойчивостта се съчетават — появява се остра, сила комуникация. Отлично за дебати и преговори. Внимавайте за спорове, предизвикани от твърде директни думи.")
        p("mars_venus",
            "Transit Mars to Natal Venus: Sexual and creative energy surge powerfully. Desire for love, beauty, and pleasure is strong. Relationships feel passionate and physically charged. Channel into art, romance, or creative collaboration.",
            "Транзитен Марс към Натална Венера: Сексуалната и творческата енергия нарастват мощно. Желанието за любов, красота и удоволствие е силно. Взаимоотношенията се чувстват страстни. Насочете в изкуство, романтика или сътрудничество.")
        p("mars_mars",
            "Transit Mars to Natal Mars (Mars Return): The warrior energy resets — a new cycle of courage and drive begins. You feel physically vital, competitive, and bold. An excellent time to begin new projects requiring sustained energy and initiative.",
            "Транзитен Марс към Натален Марс (Завръщане на Марс): Воинската енергия се нулира — започва нов цикъл на смелост и стремеж. Чувствате се физически витален и смел. Отлично за нови проекти, изискващи продължителна енергия.")
        p("mars_jupiter",
            "Transit Mars to Natal Jupiter: Courageous optimism and energised expansion combine. You feel bold enough to pursue your biggest visions. Excellent for ambitious projects and competitive endeavours. Avoid overconfidence leading to recklessness.",
            "Транзитен Марс към Натален Юпитер: Смелият оптимизъм и енергизираното разширяване се съчетават. Чувствате се достатъчно смели да преследвате най-големите си визии. Отлично за амбициозни проекти.")
        p("mars_saturn",
            "Transit Mars to Natal Saturn: Drive meets discipline — productive but sometimes frustrating. You want to move fast but find obstacles or responsibilities slowing you. Sustained, strategic effort yields real results; forcing does not.",
            "Транзитен Марс към Натален Сатурн: Стремежът среща дисциплината — продуктивно, но понякога разочароващо. Искате да се движите бързо, но намирате препятствия. Продължителното, стратегическо усилие дава реални резултати.")
        p("mars_uranus",
            "Transit Mars to Natal Uranus: Electric, volatile energy surges. You feel rebellious, impatient with constraints, and drawn to bold, unexpected action. Breakthroughs are possible — as is recklessness. Exciting and potentially disruptive.",
            "Транзитен Марс към Натален Уран: Електрическа, нестабилна енергия нараства. Чувствате се бунтарски, нетърпеливи с ограниченията. Пробивите са възможни — както и безразсъдността. Вълнуващо и потенциално разрушително.")
        p("mars_neptune",
            "Transit Mars to Natal Neptune: Drive becomes spiritualised or confused. Energy is best channelled into creative, spiritual, or healing work rather than direct force. Inspired action guided by intuition can be powerfully effective.",
            "Транзитен Марс към Натален Нептун: Стремежът се одухотворява или обърква. Енергията е най-добре насочена в творческа, духовна или лечебна работа. Вдъхновеното действие, ръководено от интуицията, може да бъде мощно ефективно.")
        p("mars_pluto",
            "Transit Mars to Natal Pluto: One of the most intense combinations — raw power, confrontation, and transformation. You may feel an overwhelming compulsion to change something, fight for control, or confront hidden truths. Handle with care and intention.",
            "Транзитен Марс към Натален Плутон: Една от най-интензивните комбинации — сурова сила, конфронтация и трансформация. Може да почувствате непреодолимо желание да промените нещо или да се изправите пред скрити истини. Действайте с намерение.")
        p("mars_chiron",
            "Transit Mars to Natal Chiron: Old wounds around courage, assertion, or anger are activated. Perhaps you fear conflict because of past hurt, or old anger resurfaces. Healing comes through acting from authentic feeling rather than old coping patterns.",
            "Транзитен Марс към Натален Хирон: Стари рани около смелостта, настойчивостта или гнева се активират. Лечение идва чрез действие от автентично чувство вместо от стари механизми за справяне.")
        p("mars_rahu",
            "Transit Mars to Natal Rahu: Energised action aligned with your evolutionary path arrives. The courage to move toward unfamiliar but destined territory is available. Act boldly in the direction of your authentic growth calling.",
            "Транзитен Марс към Натален Раху: Пристига енергизирано действие, съответстващо на еволюционния ви път. Смелостта да се движите към непозната, но предопределена територия е достъпна.")
        p("mars_lilith",
            "Transit Mars to Natal Lilith: Raw, primal desire and rebellious action surface powerfully. You may feel driven to act in ways that feel wild or outside social norms. Channel this energy authentically rather than suppressing it.",
            "Транзитен Марс към Натална Лилит: Сурово, първично желание и бунтарско действие изплуват мощно. Може да се чувствате движени да действате по начини, чувстващи се диви. Насочете тази енергия автентично.")

        // ── TRANSIT JUPITER ───────────────────────────────────────────────────
        p("jupiter_sun",
            "Transit Jupiter to Natal Sun: One of the most fortunate transits available. Confidence, vitality, and purpose expand dramatically. New opportunities arrive. Say yes to what aligns with your deepest values — abundance follows.",
            "Транзитен Юпитер към Натално Слънце: Един от най-благоприятните налични транзити. Самочувствието, жизнеността и целта се разширяват драматично. Пристигат нови възможности. Кажете да на онова, което съответства на ценностите ви.")
        p("jupiter_moon",
            "Transit Jupiter to Natal Moon: Emotional generosity, joy, and domestic abundance flow. You feel emotionally optimistic and nurtured. Family life expands or improves. Emotional intelligence grows and relationships deepen warmly.",
            "Транзитен Юпитер към Натална Луна: Емоционалната щедрост, радостта и домашното изобилие текат. Чувствате се емоционално оптимистични и подхранвани. Семейният живот се разширява. Емоционалната интелигентност расте.")
        p("jupiter_mercury",
            "Transit Jupiter to Natal Mercury: Mind expands toward the big picture — inspiring conversations, educational opportunities, and broad visions arrive. Travel, publishing, and philosophy are favoured. Avoid scattering energy across too many ideas.",
            "Транзитен Юпитер към Натален Меркурий: Умът се разширява към голямата картина — пристигат вдъхновяващи разговори и образователни възможности. Пътуванията, публикуването и философията са благоприятни.")
        p("jupiter_venus",
            "Transit Jupiter to Natal Venus: Financial and romantic abundance expands. Love relationships deepen, new love may arrive, and material comfort grows. Creative projects thrive. One of the most pleasant and fortunate of all transits.",
            "Транзитен Юпитер към Натална Венера: Финансовото и романтичното изобилие се разширява. Любовните отношения се задълбочават, нова любов може да пристигне. Творческите проекти процъфтяват. Един от най-приятните транзити.")
        p("jupiter_mars",
            "Transit Jupiter to Natal Mars: Courage and ambition are amplified by faith and luck. You feel bold enough for your biggest visions. Excellent for major initiatives and competitive endeavours. Channel heroism rather than recklessness.",
            "Транзитен Юпитер към Натален Марс: Смелостта и амбицията са усилени от вяра и късмет. Чувствате се достатъчно смели за най-големите си визии. Отлично за основни инициативи. Насочете героизъм вместо безразсъдство.")
        p("jupiter_jupiter",
            "Transit Jupiter to Natal Jupiter (Jupiter Return): A 12-year cycle of growth resets. You are called to expand beyond your previous limits in philosophy, purpose, or travel. A genuinely lucky period that rewards those who act on their vision.",
            "Транзитен Юпитер към Натален Юпитер (Завръщане на Юпитер): 12-годишен цикъл на растеж се нулира. Призовани сте да се разширите отвъд предишните ограничения. Наистина щастлив период, който възнаграждава тези, които действат по визията си.")
        p("jupiter_saturn",
            "Transit Jupiter to Natal Saturn: Expansion meets structure — a powerful combination for building something real and lasting. Optimism and discipline combine. Long-term projects receive a positive boost; hard work now lays lasting foundations.",
            "Транзитен Юпитер към Натален Сатурн: Разширяването среща структурата — мощна комбинация за изграждане на нещо реално и трайно. Оптимизмът и дисциплината се съчетават. Дългосрочните проекти получават позитивен тласък.")
        p("jupiter_uranus",
            "Transit Jupiter to Natal Uranus: Freedom, innovation, and liberating breakthroughs expand dramatically. Unexpected good fortune and radical opportunities arrive. Old limits on what you believe is possible are shattered — embrace the revolutionary expansion.",
            "Транзитен Юпитер към Натален Уран: Свободата, иновацията и освобождаващите пробиви се разширяват драматично. Пристигат неочаквано добра съдба и радикални възможности. Стари граници за възможното са разрушени.")
        p("jupiter_neptune",
            "Transit Jupiter to Natal Neptune: Spiritual growth and inspired creativity peak. Faith, imagination, and compassion expand beautifully. Avoid excess idealism or financial over-extension — ground your vision in practical steps.",
            "Транзитен Юпитер към Натален Нептун: Духовният растеж и вдъхновеното творчество достигат връх. Вярата, въображението и съпричастността се разширяват красиво. Заземете визията си в практически стъпки.")
        p("jupiter_pluto",
            "Transit Jupiter to Natal Pluto: Transformative power on a grand scale arrives. Old structures crumble and something far more powerful is built. Ambition and resilience are supercharged. This transit can mark significant turning points in life.",
            "Транзитен Юпитер към Натален Плутон: Трансформираща сила в голям мащаб пристига. Стари структури се срутват и нещо много по-мощно се изгражда. Амбицията и устойчивостта са свръхзаредени. Може да белязва значителни повратни точки.")
        p("jupiter_chiron",
            "Transit Jupiter to Natal Chiron: Healing expands. What was once a wound becomes a source of wisdom and even purpose. You gain perspective on old pain. Generous, expansive healing is available — and your wound may become your gift.",
            "Транзитен Юпитер към Натален Хирон: Лечението се разширява. Онова, което беше рана, става източник на мъдрост и дори цел. Придобивате перспектива за старата болка. Щедро, разширяващо лечение е достъпно.")
        p("jupiter_rahu",
            "Transit Jupiter to Natal Rahu: Enormous growth and destiny-aligning opportunities arrive. Doors open wide to your evolutionary path. This is a period of significant expansion in the direction your soul is heading.",
            "Транзитен Юпитер към Натален Раху: Пристигат огромен растеж и съдбовно подравняващи се възможности. Вратите се отварят широко към еволюционния ви път. Период на значително разширение в посоката, в която върви душата ви.")
        p("jupiter_lilith",
            "Transit Jupiter to Natal Lilith: Wild, authentic self-expression is invited to expand. The parts of you that were deemed 'too much' are exactly what the world needs. Greater freedom and authenticity in expressing your full self are available.",
            "Транзитен Юпитер към Натална Лилит: Дивото, автентично себеизразяване е поканено да се разшири. Частите от вас, считани за 'прекалено', са точно онова, от което светът се нуждае.")

        // ── TRANSIT SATURN ────────────────────────────────────────────────────
        p("saturn_sun",
            "Transit Saturn to Natal Sun: A fundamental reckoning with identity and life path is underway. You are asked to take full responsibility for who you are becoming. Demanding but ultimately a transit of authentic self-mastery and lasting achievement.",
            "Транзитен Сатурн към Натално Слънце: Основно разчистване на сметките с идентичността и жизнения път е в ход. Призовани сте да поемете пълна отговорност за кого ставате. Взискателно, но в крайна сметка транзит на автентично самовладеене.")
        p("saturn_moon",
            "Transit Saturn to Natal Moon: Emotional heaviness and restriction arise. Old emotional patterns that no longer serve are highlighted. The relationship with home, mother, or emotional security is tested. Restructure emotional life on more solid ground.",
            "Транзитен Сатурн към Натална Луна: Появяват се емоционална тежест и ограничение. Стари емоционални модели, вече неслужещи, са подчертани. Отношенията с дома или емоционалната сигурност се тестват. Преструктурирайте емоционалния живот.")
        p("saturn_mercury",
            "Transit Saturn to Natal Mercury: Thinking becomes serious, structured, and focused. Ideas are tested for practicality and relevance. Thorough planning, academic work, and long-term mental projects are well-supported. Shallow thinking is no longer satisfying.",
            "Транзитен Сатурн към Натален Меркурий: Мисленето става сериозно, структурирано и фокусирано. Идеите се тестват за практичност. Задълбоченото планиране и дългосрочните ментални проекти са добре поддържани.")
        p("saturn_venus",
            "Transit Saturn to Natal Venus: Love and finances are subject to a reality test. You take relationships seriously and demand authenticity. Shallow connections may fade; genuine ones deepen. Financial discipline and long-term planning are called for.",
            "Транзитен Сатурн към Натална Венера: Любовта и финансите са подложени на тест с реалността. Приемате отношенията сериозно. Повърхностните връзки може да отслабнат; истинските се задълбочат. Финансовата дисциплина е призована.")
        p("saturn_mars",
            "Transit Saturn to Natal Mars: Drive meets resistance — you want to move fast but find obstacles slowing you. The teaching is patience and strategy over brute force. Disciplined, sustained action yields real results; forcing does not.",
            "Транзитен Сатурн към Натален Марс: Стремежът среща съпротива. Искате да се движите бързо, но намирате препятствия. Урокът е търпение и стратегия вместо груба сила. Дисциплинираното, продължително усилие дава реални резултати.")
        p("saturn_jupiter",
            "Transit Saturn to Natal Jupiter: Grand vision meets practical reality. Optimism is grounded by discipline — you have both the dream and the ability to build it. Long-term projects begun now carry the combined power of inspiration and structure.",
            "Транзитен Сатурн към Натален Юпитер: Голямата визия среща практическата реалност. Оптимизмът е заземен от дисциплина. Дългосрочните проекти, започнати сега, носят комбинираната сила на вдъхновение и структура.")
        p("saturn_saturn",
            "Transit Saturn to Natal Saturn (Saturn Return at ~29 and ~59): One of the most significant transits of a lifetime. You are called to take full responsibility for your life and step into authentic maturity. Old structures that no longer fit must be released.",
            "Транзитен Сатурн към Натален Сатурн (Завръщане на Сатурн ~29 и ~59 г.): Един от най-значимите транзити в живота. Призовани сте да поемете пълна отговорност и да встъпите в автентична зрялост. Стари структури трябва да бъдат освободени.")
        p("saturn_uranus",
            "Transit Saturn to Natal Uranus: The need for freedom and stability are in productive tension. True liberation is being rebuilt on an authentic foundation rather than mere rebellion. Restructure what restricts while maintaining what is genuinely essential.",
            "Транзитен Сатурн към Натален Уран: Нуждата от свобода и стабилност са в продуктивно напрежение. Истинското освобождение се изгражда отново на автентична основа. Преструктурирайте онова, което ограничава, като запазвате онова, което е съществено.")
        p("saturn_neptune",
            "Transit Saturn to Natal Neptune: Dreams meet reality. Illusions that have been sustaining you are gently dissolved. This clarifying transit invites you to rebuild spiritual life on what is genuinely substantial rather than wishful thinking.",
            "Транзитен Сатурн към Натален Нептун: Мечтите срещат реалността. Илюзии, поддържали ви, се разтварят нежно. Този изясняващ транзит ви кани да преизградите духовния живот върху онова, което е наистина съществено.")
        p("saturn_pluto",
            "Transit Saturn to Natal Pluto: Deep, structural transformation under sustained pressure. Old power dynamics and psychological blocks are dismantled methodically. Slow and demanding, but the result is fundamental, lasting change at the deepest level.",
            "Транзитен Сатурн към Натален Плутон: Дълбока, структурна трансформация под продължително налягане. Стари динамики на власт и психологически блокажи се демонтират методично. Бавно и взискателно, но резултатът е фундаментална, трайна промяна.")
        p("saturn_chiron",
            "Transit Saturn to Natal Chiron: The wound is met with maturity and responsibility. You are ready to stop avoiding the deepest source of pain and begin healing it with discipline and self-compassion. Saturn turns the wound into earned wisdom.",
            "Транзитен Сатурн към Натален Хирон: Раната е посрещната с зрялост и отговорност. Готови сте да спрете да избягвате най-дълбокия источник на болка и да започнете лечение с дисциплина. Сатурн превръща раната в заслужена мъдрост.")
        p("saturn_rahu",
            "Transit Saturn to Natal Rahu: Growth toward your destiny requires disciplined, committed effort. You cannot take shortcuts on your evolutionary path now — only genuine, structured work counts. The rewards are lasting and real.",
            "Транзитен Сатурн към Натален Раху: Растежът към съдбата ви изисква дисциплинирано, ангажирано усилие. Не можете да съкращавате пътя към еволюционния си път сега. Наградите са трайни и реални.")
        p("saturn_lilith",
            "Transit Saturn to Natal Lilith: Wild energy is examined with maturity. You are asked to take responsibility for your shadow self — not to suppress it, but to integrate it with genuine, earned authority over your own darkness.",
            "Транзитен Сатурн към Натална Лилит: Дивата енергия е изследвана с зрялост. Призовани сте да поемете отговорност за сянката си — не да я потиснете, а да я интегрирате с истинска власт над собствената си тъмнина.")

        // ── TRANSIT URANUS ────────────────────────────────────────────────────
        p("uranus_sun",
            "Transit Uranus to Natal Sun: A fundamental awakening of identity — one of the most life-changing transits. Who you thought you were is being disrupted so that who you truly are can emerge. Embrace authenticity, even at the cost of your previous path.",
            "Транзитен Уран към Натално Слънце: Фундаментално пробуждане на идентичността — един от най-животопроменящите транзити. Кой смятахте, че сте, се нарушава, за да може истинският ви Аз да се появи. Прегърнете автентичността дори с цената на предишния ви път.")
        p("uranus_moon",
            "Transit Uranus to Natal Moon: Sudden changes in emotional life, home, or domestic arrangements arrive. Old emotional habits are disrupted in service of emotional freedom. You may feel erratic or brilliantly alive — often both simultaneously.",
            "Транзитен Уран към Натална Луна: Внезапни промени в емоционалния живот, дома или домашните уредби. Стари емоционални навици се нарушават в служба на емоционалната свобода. Може да се чувствате нестабилни или брилянтно живи — често и двете едновременно.")
        p("uranus_mercury",
            "Transit Uranus to Natal Mercury: Your mind receives electric downloads of innovation and insight. Brilliant, unconventional ideas come rapidly. Communication becomes inventive and possibly erratic. Ground and channel this mental voltage creatively.",
            "Транзитен Уран към Натален Меркурий: Умът ви получава електрически изтегляния на иновации и прозрения. Брилянтни, нестандартни идеи идват бързо. Комуникацията става изобретателна. Заземете и насочете това ментално напрежение творчески.")
        p("uranus_venus",
            "Transit Uranus to Natal Venus: Love life and values undergo radical change. Existing relationships must evolve or end to make way for something more authentic. Unconventional attractions arrive. Authenticity replaces convention in what you value.",
            "Транзитен Уран към Натална Венера: Любовният живот и ценностите претърпяват радикална промяна. Съществуващите отношения трябва да еволюират или да приключат. Нестандартни привличания пристигат. Автентичността замества конвенцията в ценностите ви.")
        p("uranus_mars",
            "Transit Uranus to Natal Mars: One of the most electrifying and potentially volatile transits. Drive and assertiveness become unpredictable, brilliant, and sometimes reckless. Channel the lightning-bolt energy into bold innovation rather than impulsive destruction.",
            "Транзитен Уран към Натален Марс: Един от най-електризиращите и потенциално нестабилни транзити. Стремежът и настойчивостта стават непредсказуеми, брилянтни и понякога безразсъдни. Насочете енергията на мълнията в смела иновация вместо в импулсивно разрушение.")
        p("uranus_jupiter",
            "Transit Uranus to Natal Jupiter: Sudden, liberating expansion of opportunity and freedom arrives. Unexpected good fortune and breakthroughs in vision or philosophy emerge. Old limits on what you believe is possible are shattered.",
            "Транзитен Уран към Натален Юпитер: Внезапно, освобождаващо разширяване на възможностите и свободата пристига. Появяват се неочакван късмет и пробиви. Стари граници за онова, което смятате за възможно, са разрушени.")
        p("uranus_saturn",
            "Transit Uranus to Natal Saturn: Established structures of your life are being fundamentally disrupted. What has been built through duty and convention is tested for authentic alignment. Some foundations need to be rebuilt on more genuine ground.",
            "Транзитен Уран към Натален Сатурн: Установените структури на живота ви са фундаментално нарушени. Онова, построено чрез задълженост и конвенция, се тества. Някои основи трябва да бъдат преизградени на по-истинска земя.")
        p("uranus_uranus",
            "Transit Uranus to Natal Uranus (Uranus Opposition ~40 or Uranus Return ~84): A massive invitation to live your most authentic life. Break free from any remaining pretence — the revolutionary truth of who you are demands to be lived fully.",
            "Транзитен Уран към Натален Уран (Опозиция ~40 г. или Завръщане ~84 г.): Огромна покана да живеете най-автентичния си живот. Освободете се от всяко оставащо притворство — революционната истина за кои сте изисква да бъде напълно изживяна.")
        p("uranus_neptune",
            "Transit Uranus to Natal Neptune: Spiritual understanding undergoes a liberating disruption. Old spiritual frameworks that have become limiting are dismantled to reveal a wider, more authentic spiritual reality. Mystical experiences may be sudden.",
            "Транзитен Уран към Натален Нептун: Духовното разбиране претърпява освобождаващо нарушаване. Стари духовни рамки, станали ограничаващи, се демонтират. Мистичните преживявания може да бъдат внезапни.")
        p("uranus_pluto",
            "Transit Uranus to Natal Pluto: Generational forces of revolution and transformation activate in your personal life. Profound collective change reverberates through your individual experience. Power, change, and freedom operate at a tectonic level.",
            "Транзитен Уран към Натален Плутон: Поколенчески сили на революция и трансформация се активират в личния ви живот. Дълбока колективна промяна отеква в индивидуалния ви опит. Властта, промяната и свободата работят на тектонично ниво.")
        p("uranus_chiron",
            "Transit Uranus to Natal Chiron: The wound is disrupted and reawakened in service of liberation. Old coping strategies around your core wound are no longer working — this is precisely the invitation to heal differently and break free.",
            "Транзитен Уран към Натален Хирон: Раната е нарушена и събудена отново в служба на освобождението. Стари механизми за справяне вече не работят — това е точно поканата да се излекувате по различен начин.")
        p("uranus_rahu",
            "Transit Uranus to Natal Rahu: Your evolutionary path is suddenly and dramatically accelerated. What was a gradual movement toward destiny becomes an electric leap. Embrace unexpected opportunities for growth, even if they feel destabilising.",
            "Транзитен Уран към Натален Раху: Еволюционният ви път е внезапно и драматично ускорен. Онова, което беше бавно движение към съдбата, става електрически скок. Прегърнете неочаквани възможности за растеж.")
        p("uranus_lilith",
            "Transit Uranus to Natal Lilith: The wild self is liberated and amplified powerfully. Suppressed desires, instincts, and rebellious impulses surge to the surface. A profound invitation to authentic freedom from self-imposed limitations and social conditioning.",
            "Транзитен Уран към Натална Лилит: Дивото Аз е освободено и мощно усилено. Потиснати желания, инстинкти и бунтарски импулси изплуват на повърхността. Дълбока покана за автентична свобода от самоналоженото ограничение.")

        // ── TRANSIT NEPTUNE ───────────────────────────────────────────────────
        p("neptune_sun",
            "Transit Neptune to Natal Sun: A slow, profound spiritualising of identity over several years. Who you were is gently eroded; a more subtle, compassionate, soul-level sense of self emerges. Confusion and inspiration arrive together.",
            "Транзитен Нептун към Натално Слънце: Бавно, дълбоко одухотворяване на идентичността в продължение на няколко години. Кой бяхте се разтваря нежно; появява се по-фино, съпричастно усещане за Аз на ниво душа. Объркването и вдъхновението пристигат заедно.")
        p("neptune_moon",
            "Transit Neptune to Natal Moon: Emotional sensitivity reaches profound depths. Psychic experiences multiply. The boundaries between your emotions and others' dissolve — you absorb moods. Profound empathy alongside risk of losing yourself emotionally.",
            "Транзитен Нептун към Натална Луна: Емоционалната чувствителност достига дълбоки дълбини. Психичните преживявания се умножават. Границите между вашите емоции и тези на другите се разтварят. Дълбока съпричастност заедно с риск от емоционално самозагубване.")
        p("neptune_mercury",
            "Transit Neptune to Natal Mercury: The mind softens into imagination and spiritual intuition. Logical thinking gives way to inspiration. Beautiful for creative writing, art, and mysticism; challenging for contracts, decisions, and clarity.",
            "Транзитен Нептун към Натален Меркурий: Умът се омеква в въображение и духовна интуиция. Логическото мислене отстъпва на вдъхновението. Красиво за творческо писане и мистицизъм; предизвикателно за договори и яснота.")
        p("neptune_venus",
            "Transit Neptune to Natal Venus: Love becomes mystical, selfless, and sometimes illusory. You yearn for spiritual union. Relationships either deepen spiritually or reveal their illusions. Creative and spiritual art flourish magnificently.",
            "Транзитен Нептун към Натална Венера: Любовта става мистична, безкористна и понякога илюзорна. Копнеете за духовен съюз. Отношенията или се задълбочават духовно, или разкриват своите илюзии. Творческото и духовно изкуство процъфтяват.")
        p("neptune_mars",
            "Transit Neptune to Natal Mars: Drive becomes spiritualised or confused. Ordinary ambition feels hollow; you are drawn toward compassionate, healing, or artistic action. Channel energy into spiritual service rather than forceful striving.",
            "Транзитен Нептун към Натален Марс: Стремежът се одухотворява или обърква. Обикновената амбиция изглежда празна; привлечени сте към съпричастно, лечебно или художествено действие. Насочете енергията в духовна служба.")
        p("neptune_jupiter",
            "Transit Neptune to Natal Jupiter: Spiritual idealism and compassion expand enormously. Your vision of a better world feels vivid. Ground the inspired action in practical reality to avoid over-idealism or financial inflation.",
            "Транзитен Нептун към Натален Юпитер: Духовният идеализъм и съпричастността се разширяват огромно. Визията ви за по-добър свят изглежда жива. Заземете вдъхновеното действие в практическата реалност за да избегнете свръхидеализъм.")
        p("neptune_saturn",
            "Transit Neptune to Natal Saturn: Foundations built on fear or convention are gently eroded. This destabilises but ultimately invites rebuilding on genuinely spiritual values. What dissolves was never solid; what remains is truly yours.",
            "Транзитен Нептун към Натален Сатурн: Основите, построени на страх или конвенция, се разтварят нежно. Стабилизира, но в крайна сметка кани изграждане отново върху истински духовни ценности. Онова, което се разтваря, никога не е било здраво.")
        p("neptune_uranus",
            "Transit Neptune to Natal Uranus: The desire for liberation becomes spiritually infused. The revolution you seek is not merely personal — it is a fundamental dissolution of the ego's need for control. Freedom through surrender becomes the unexpected path.",
            "Транзитен Нептун към Натален Уран: Желанието за освобождение се изпълва с духовно съдържание. Революцията, която търсите, не е само лична — тя е фундаментално разтваряне на нуждата на егото от контрол.")
        p("neptune_neptune",
            "Transit Neptune to Natal Neptune (Neptune Opposition ~40-42): A spiritual midlife. Beliefs about the spiritual dimension of life are tested; dreams and ideals are examined. Deepen genuine faith and release naive idealism or spiritual materialism.",
            "Транзитен Нептун към Натален Нептун (Опозиция ~40-42 г.): Духовна среда на живота. Убежденията за духовното измерение на живота се тестват. Задълбочете истинската вяра и освободете наивния идеализъм.")
        p("neptune_pluto",
            "Transit Neptune to Natal Pluto: The deepest transformations are spiritualised. The dissolution of old power structures opens space for something transcendent. Profound, multigenerational spiritual evolution is at work.",
            "Транзитен Нептун към Натален Плутон: Най-дълбоките трансформации са одухотворени. Разтварянето на стари структури на власт отваря пространство за нещо трансцендентно. Дълбока, многопоколенческа духовна еволюция е в ход.")
        p("neptune_chiron",
            "Transit Neptune to Natal Chiron: The wound is bathed in compassion and spiritual light. Neptune invites unconditional self-compassion. Beware of spiritual bypassing — face the wound through compassionate presence rather than escape.",
            "Транзитен Нептун към Натален Хирон: Раната е потопена в съпричастност и духовна светлина. Нептун кани безусловно самосъстрадание. Внимавайте за духовно заобикаляне — изправете се пред раната чрез съпричастно присъствие.")
        p("neptune_rahu",
            "Transit Neptune to Natal Rahu: Your evolutionary path is infused with spiritual, creative, or compassionate themes. The direction your soul is moving has a mystical dimension. Follow the spiritual pull rather than the merely practical.",
            "Транзитен Нептун към Натален Раху: Еволюционният ви път е изпълнен с духовни, творчески или съпричастни теми. Посоката, в която се движи душата ви, има мистично измерение.")
        p("neptune_lilith",
            "Transit Neptune to Natal Lilith: The shadow self is gently spiritualised. Old patterns of suppression give way to a more compassionate relationship with your own wildness. Healing through spiritual acceptance of all aspects of yourself.",
            "Транзитен Нептун към Натална Лилит: Сянката на Аза е нежно одухотворена. Стари модели на потискане отстъпват на по-съпричастно отношение към собствената ви дивост. Лечение чрез духовно приемане на всички аспекти от себе си.")

        // ── TRANSIT PLUTO ─────────────────────────────────────────────────────
        p("pluto_sun",
            "Transit Pluto to Natal Sun: One of the deepest transits of a lifetime. Your entire identity is being rebuilt from the ground up. Old ego structures that no longer serve are dismantled. You emerge fundamentally, powerfully changed.",
            "Транзитен Плутон към Натално Слънце: Един от най-дълбоките транзити в живота. Цялата ви идентичност се преизгражда от основи. Стари структури на егото, вече неслужещи, се демонтират. Излизате фундаментално, мощно променени.")
        p("pluto_moon",
            "Transit Pluto to Natal Moon: Deep psychological transformation of emotional life and past conditioning. Old emotional patterns are destroyed and rebuilt at a more authentic level. Profoundly healing and deeply demanding over an extended period.",
            "Транзитен Плутон към Натална Луна: Дълбока психологическа трансформация на емоционалния живот и миналото обусловяване. Стари емоционални модели са разрушени и преизградени на по-автентично ниво. Дълбоко лечебно и взискателно.")
        p("pluto_mercury",
            "Transit Pluto to Natal Mercury: Thinking undergoes a psychological depth transformation. Surface-level understanding no longer satisfies — you are compelled to know the truth beneath the truth. Research and transformative communication are the gifts.",
            "Транзитен Плутон към Натален Меркурий: Мисленето претърпява психологическа трансформация в дълбочина. Повърхностното разбиране вече не удовлетворява. Изследванията и трансформативната комуникация са даровете.")
        p("pluto_venus",
            "Transit Pluto to Natal Venus: Love and values are transformed at the deepest level. What you desire shifts fundamentally. Relationships that cannot sustain authentic truth may not survive; those that can are profoundly, lastingly deepened.",
            "Транзитен Плутон към Натална Венера: Любовта и ценностите са трансформирани на най-дълбоко ниво. Онова, което желаете, се променя фундаментално. Отношенията, неудържащи автентична истина, може да не оцелеят; тези, способни на това, са дълбоко задълбочени.")
        p("pluto_mars",
            "Transit Pluto to Natal Mars: Raw power and drive are unleashed at a fundamental level. A relentless compulsion to act, fight, or achieve may arise. Extraordinary feats are possible — as is destructive obsession. Fight for what is truly worth fighting for.",
            "Транзитен Плутон към Натален Марс: Сурова сила и стремеж са освободени на фундаментално ниво. Може да се появи неудържим импулс за действие или постижение. Изключителни подвизи са възможни. Борете се за онова, за което наистина си струва да се борите.")
        p("pluto_jupiter",
            "Transit Pluto to Natal Jupiter: Philosophy, beliefs, and optimism undergo a profound power transformation. False beliefs are ruthlessly dismantled. What emerges is a more powerful, authentic, evidence-based faith in your capacity for transformation.",
            "Транзитен Плутон към Натален Юпитер: Философията, убежденията и оптимизмът претърпяват дълбока трансформация на властта. Фалшивите убеждения са безмилостно демонтирани. Появява се по-мощна, автентична вяра в способността ви за трансформация.")
        p("pluto_saturn",
            "Transit Pluto to Natal Saturn: The deepest structures of life — career, authority, responsibility — undergo profound, inexorable transformation. What was built on false authority or fear is dismantled and rebuilt on authentic power and integrity.",
            "Транзитен Плутон към Натален Сатурн: Най-дълбоките структури на живота — кариера, авторитет, отговорност — претърпяват дълбока трансформация. Онова, построено на фалшив авторитет, се демонтира и преизгражда върху автентична сила.")
        p("pluto_uranus",
            "Transit Pluto to Natal Uranus: Generational forces of revolution and liberation activate personally. The need for genuine freedom at the deepest psychological level becomes overwhelming and unstoppable. Profound, collective transformation is tectonic.",
            "Транзитен Плутон към Натален Уран: Поколенчески сили на революция и освобождение се активират лично. Нуждата от истинска свобода на най-дълбоко психологическо ниво става непреодолима. Дълбоката колективна трансформация е тектонична.")
        p("pluto_neptune",
            "Transit Pluto to Natal Neptune: Spiritual realities undergo deep transformation. What you believed about the spiritual dimension is taken apart and rebuilt at a more powerful, authentic level. The mystic within you becomes a force of transformation.",
            "Транзитен Плутон към Натален Нептун: Духовните реалности претърпяват дълбока трансформация. Онова, вярвано за духовното измерение, е разглобено и преизградено на по-мощно ниво. Мистикът вътре ви се превръща в сила на трансформация.")
        p("pluto_pluto",
            "Transit Pluto to Natal Pluto (Pluto Square ~40): The midlife power activation. You are asked to confront your own power, shadow, and deepest fears. What is false about how you have wielded — or denied — your power must be confronted and transformed.",
            "Транзитен Плутон към Натален Плутон (Квадрат ~40 г.): Активиране на силата в средата на живота. Призовани сте да се изправите пред собствената си сила, сянка и най-дълбоки страхове. Онова, фалшиво в начина, по който сте упражнявали — или отричали — властта си, трябва да бъде трансформирано.")
        p("pluto_chiron",
            "Transit Pluto to Natal Chiron: The deepest wound is activated for profound healing transformation. This is not gentle healing — it is surgery. Old wounds unconsciously driving your life are brought into full awareness for complete healing and integration.",
            "Транзитен Плутон към Натален Хирон: Най-дълбоката рана е активирана за дълбока лечебна трансформация. Това не е нежно лечение — то е операция. Стари рани, несъзнателно движещи живота ви, са доведени до пълно осъзнаване.")
        p("pluto_rahu",
            "Transit Pluto to Natal Rahu: Your evolutionary path is accelerated through profound transformation. Old patterns blocking your growth are ruthlessly dismantled. You are powerfully propelled toward your soul's authentic destiny.",
            "Транзитен Плутон към Натален Раху: Еволюционният ви път е ускорен чрез дълбока трансформация. Стари модели, блокиращи растежа ви, са безмилостно демонтирани. Мощно сте тласкани към автентичната съдба на душата ви.")
        p("pluto_lilith",
            "Transit Pluto to Natal Lilith: The shadow self and deepest taboos are powerfully activated for complete integration. What has been most suppressed throughout this lifetime surfaces for a profound reckoning and the full truth of who you are.",
            "Транзитен Плутон към Натална Лилит: Сянката и най-дълбоките табута са мощно активирани за пълна интеграция. Онова, най-потиснато в живота ви, изплува за дълбоко разчистване и пълна истина за кои сте.")

        // ── TRANSIT CHIRON ────────────────────────────────────────────────────
        p("chiron_sun",
            "Transit Chiron to Natal Sun: The core wound around identity and the right to be seen surfaces for healing. Where do you feel fundamentally flawed or invisible? This transit invites conscious compassion for the core self.",
            "Транзитен Хирон към Натално Слънце: Основната рана около идентичността и правото да бъдете видени изплува за лечение. Лечение идва чрез съзнателна съпричастност към основното Аз.")
        p("chiron_moon",
            "Transit Chiron to Natal Moon: Old emotional wounds — often from early life or the relationship with the mother — surface tenderly. An invitation for deep emotional healing through acknowledgment and self-compassion rather than avoidance.",
            "Транзитен Хирон към Натална Луна: Стари емоционални рани — често от ранния живот — изплуват нежно. Покана за дълбоко емоционално лечение чрез признание и самосъстрадание.")
        p("chiron_mercury",
            "Transit Chiron to Natal Mercury: Wounds around communication and being heard are activated. Perhaps old messages that your voice didn't matter surface. Healing comes through authentic self-expression and speaking your truth gently.",
            "Транзитен Хирон към Натален Меркурий: Рани около комуникацията и правото да бъдете чути се активират. Лечение идва чрез автентично себеизразяване и нежно говорене на истината ви.")
        p("chiron_venus",
            "Transit Chiron to Natal Venus: Old wounds around love and worthiness of being loved come forward. A tender invitation for self-compassion in the places where you have felt unworthy of love or beauty. Healing through kindness toward yourself.",
            "Транзитен Хирон към Натална Венера: Стари рани около любовта и достойнството да бъдете обичани изплуват. Нежна покана за самосъстрадание там, където сте се чувствали недостойни за любов.")
        p("chiron_mars",
            "Transit Chiron to Natal Mars: Wounds around courage, assertion, or the right to act are activated. Perhaps you fear conflict because of past hurt, or old anger resurfaces. Healing comes through acting from your authentic self rather than old defensive patterns.",
            "Транзитен Хирон към Натален Марс: Рани около смелостта и правото да действате се активират. Лечение идва чрез действие от автентичното ви Аз вместо от стари защитни модели.")
        p("chiron_jupiter",
            "Transit Chiron to Natal Jupiter: Wounds around faith, expansion, and believing in your potential surface. Where did you stop trusting in your capacity to grow and thrive? This transit opens a door to reclaiming genuine, earned optimism.",
            "Транзитен Хирон към Натален Юпитер: Рани около вярата и убеждението в потенциала ви изплуват. Лечение идва чрез придобиване на истинска, заслужена увереност в способността ви да растете и процъфтявате.")
        p("chiron_saturn",
            "Transit Chiron to Natal Saturn: Wounds around authority, discipline, and deserving success surface. Old messages that your achievements were not enough can be examined and released. Turn the wound into earned wisdom about authentic authority.",
            "Транзитен Хирон към Натален Сатурн: Рани около авторитета и заслуженето на успех изплуват. Стари послания, че постиженията ви не са достатъчни, могат да бъдат изследвани. Превърнете раната в заслужена мъдрост за автентичен авторитет.")
        p("chiron_uranus",
            "Transit Chiron to Natal Uranus: Wounds around freedom and the right to be different surface for healing. Where did you learn it was dangerous to be unconventional? Healing comes through claiming your authentic originality without apology.",
            "Транзитен Хирон към Натален Уран: Рани около свободата и правото да бъдете различни изплуват. Лечение идва чрез заявяване на автентичната ви оригиналност без извинение.")
        p("chiron_neptune",
            "Transit Chiron to Natal Neptune: Wounds around spirituality, boundaries, or the right to dream come forward. Perhaps you were told your spiritual experiences were invalid. This transit invites compassion for the dreamer and seeker in you.",
            "Транзитен Хирон към Натален Нептун: Рани около духовността и правото да мечтаете изплуват. Лечение идва чрез съпричастност към мечтателя и търсача в себе си.")
        p("chiron_pluto",
            "Transit Chiron to Natal Pluto: Wounds around power, transformation, and survival at the deepest level surface. Old traumas around powerlessness are activated. Profound healing through facing the deepest material with compassionate courage.",
            "Транзитен Хирон към Натален Плутон: Рани около властта и оцеляването на най-дълбоко ниво изплуват. Стари травми около безсилието се активират. Дълбоко лечение чрез изправяне пред най-дълбокия материал.")
        p("chiron_chiron",
            "Transit Chiron to Natal Chiron (Chiron Return ~50): The great healing milestone. You are called to fully claim your wound as your wisdom, your deepest vulnerability as your greatest gift. The wounded healer archetype activates completely.",
            "Транзитен Хирон към Натален Хирон (Завръщане на Хирон ~50 г.): Великият лечебен milestone. Призовани сте напълно да заявите раната си като ваша мъдрост, вашата дълбока уязвимост като ваш най-голям дар.")
        p("chiron_rahu",
            "Transit Chiron to Natal Rahu: Your wounds are the doorway to your evolutionary gifts. The healing journey is the growth path. What you have struggled with most deeply is exactly what your soul came to transform and share.",
            "Транзитен Хирон към Натален Раху: Раните ви са вратата към еволюционните ви дарове. Лечебното пътуване е пътят на растеж. Онова, с което сте се борили най-дълбоко, е точно онова, за чието трансформиране душата ви е дошла.")
        p("chiron_lilith",
            "Transit Chiron to Natal Lilith: Wounds around the wild self and authentic desire surface for healing. Where were you shamed for your instincts? Healing comes through compassionate acknowledgment of the full, untamed truth of who you are.",
            "Транзитен Хирон към Натална Лилит: Рани около дивото Аз изплуват за лечение. Лечение идва чрез съпричастно признание на пълната, необуздана истина за кои сте.")

        // ── TRANSIT RAHU (NORTH NODE) ─────────────────────────────────────────
        p("rahu_sun",
            "Transit Rahu to Natal Sun: New opportunities for growth through authentic self-expression arrive. The universe is pushing you toward unfamiliar but destined territory around identity. Embrace what excites and expands you.",
            "Транзитен Раху към Натално Слънце: Пристигат нови възможности за растеж чрез автентично себеизразяване. Вселената ви тласка към непозната, но предопределена територия около идентичността.")
        p("rahu_moon",
            "Transit Rahu to Natal Moon: Your emotional life is redirected toward new, growth-oriented territory. Old emotional comfort zones are dismantled so more authentic emotional experiences can be embraced. Trust the unfamiliar emotional pull.",
            "Транзитен Раху към Натална Луна: Емоционалният ви живот е пренасочен към нова, растежно ориентирана територия. Стари емоционални зони на комфорт се демонтират. Доверете се на непознатото емоционално привличане.")
        p("rahu_mercury",
            "Transit Rahu to Natal Mercury: New ideas and modes of thinking that align with your evolutionary path arrive. Information appears pointing toward your growth direction. Remain receptive to new ways of thinking and communicating.",
            "Транзитен Раху към Натален Меркурий: Пристигат нови идеи и начини на мислене, свързани с еволюционния ви път. Информация, сочеща към посоката ви на растеж, се появява.")
        p("rahu_venus",
            "Transit Rahu to Natal Venus: New relationships and values aligned with your soul's direction arrive. A new connection — or a new chapter in an existing one — that feels both destined and growth-expanding is possible.",
            "Транзитен Раху към Натална Венера: Пристигат нови отношения и ценности, свързани с посоката на душата ви. Нова връзка, чувстваща се едновременно предопределена и разширяваща растежа, е възможна.")
        p("rahu_mars",
            "Transit Rahu to Natal Mars: Courageous action toward unfamiliar but destined territory is activated. New drive in the direction of growth arises. Act boldly toward what feels simultaneously exciting and new.",
            "Транзитен Раху към Натален Марс: Смелото действие към непозната, но предопределена територия е активирано. Нова енергия в посоката на растеж се появява. Действайте смело към онова, което е едновременно вълнуващо и ново.")
        p("rahu_jupiter",
            "Transit Rahu to Natal Jupiter: Enormous growth and destiny-aligning opportunities become available. This is one of the most auspicious of all configurations — large opportunities perfectly aligned with your life direction arrive.",
            "Транзитен Раху към Натален Юпитер: Достъпни стават огромен растеж и съдбовно подравняващи се възможности. Една от най-благоприятните конфигурации — пристигат големи възможности, идеално съответстващи на жизнената ви посока.")
        p("rahu_saturn",
            "Transit Rahu to Natal Saturn: Growth in the direction of your destiny requires disciplined structure and commitment. Build something real and lasting in the direction you know you need to go — shortcuts don't count now.",
            "Транзитен Раху към Натален Сатурн: Растежът към съдбата ви изисква дисциплинирана структура и ангажираност. Изградете нещо реално в посоката, в която знаете, че трябва да тръгнете.")
        p("rahu_uranus",
            "Transit Rahu to Natal Uranus: Liberating breakthroughs toward your evolutionary path arrive. The changes coming are both destined and revolutionary. Embrace the disruption — it is pointing directly toward your authentic growth.",
            "Транзитен Раху към Натален Уран: Освобождаващи пробиви към еволюционния ви път пристигат. Промените, идващи, са едновременно предопределени и революционни. Прегърнете нарушаването.")
        p("rahu_neptune",
            "Transit Rahu to Natal Neptune: Your evolutionary path is infused with spiritual or creative themes. The territory your soul is moving into has a mystical or artistic dimension. Follow the spiritual pull over the merely practical.",
            "Транзитен Раху към Натален Нептун: Еволюционният ви път е изпълнен с духовни или творчески теми. Следвайте духовното привличане пред просто практичното.")
        p("rahu_pluto",
            "Transit Rahu to Natal Pluto: Your evolutionary path leads through profound transformation. Old patterns blocking your soul's direction are being demolished. You are being powerfully and inexorably propelled toward your authentic destiny.",
            "Транзитен Раху към Натален Плутон: Еволюционният ви път минава през дълбока трансформация. Стари модели, блокиращи посоката на душата ви, се разрушават. Мощно и неумолимо сте тласкани към автентичната си съдба.")
        p("rahu_chiron",
            "Transit Rahu to Natal Chiron: Your wounds are directly connected to the gifts your soul came to offer. The healing journey is the evolutionary path. Growing requires integrating your deepest vulnerabilities as your greatest gifts.",
            "Транзитен Раху към Натален Хирон: Раните ви са пряко свързани с даровете, за чието предлагане е дошла душата ви. Лечебното пътуване е еволюционният път.")
        p("rahu_rahu",
            "Transit Rahu to Natal Rahu (Rahu Return ~18 years): A major reset of your growth path. The evolutionary direction is renewed and amplified. New chapters in your soul's journey begin. Step boldly toward unfamiliar territory.",
            "Транзитен Раху към Натален Раху (Завръщане ~18 г.): Основно нулиране на пътя ви на растеж. Еволюционната посока е обновена и усилена. Нови глави в пътуването на душата ви започват.")
        p("rahu_lilith",
            "Transit Rahu to Natal Lilith: Your evolutionary path leads directly through your shadow and authentic wildness. Growth requires claiming the aspects of yourself most suppressed. Your wildness is the gift your soul came to offer.",
            "Транзитен Раху към Натална Лилит: Еволюционният ви път минава директно през сянката ви и автентичната дивост. Растежът изисква заявяване на най-потиснатите аспекти от себе си.")

        // ── TRANSIT LILITH ────────────────────────────────────────────────────
        p("lilith_sun",
            "Transit Lilith to Natal Sun: Suppressed aspects of identity surface for acknowledgment. Authentic power requires integrating rather than suppressing these darker elements. Who you truly are — all of it — deserves to exist.",
            "Транзитна Лилит към Натално Слънце: Потиснати аспекти на идентичността изплуват за признание. Автентичната сила изисква интегриране вместо потискане на тези по-тъмни елементи.")
        p("lilith_moon",
            "Transit Lilith to Natal Moon: Raw, instinctual emotions deemed 'unacceptable' demand expression. Authentic emotional health requires making room for the full spectrum — including rage, lust, and wild grief.",
            "Транзитна Лилит към Натална Луна: Сурови, инстинктивни емоции, считани за 'неприемливи', изискват изразяване. Автентичното емоционално здраве изисква пространство за пълния спектър.")
        p("lilith_mercury",
            "Transit Lilith to Natal Mercury: Taboo thoughts and suppressed truths demand expression. You feel compelled to say what is usually left unsaid. Authentic communication over socially acceptable performance is the invitation.",
            "Транзитна Лилит към Натален Меркурий: Табуирани мисли и потиснати истини изискват изразяване. Чувствате се принудени да кажете онова, обикновено неказано. Поканата е за автентична комуникация.")
        p("lilith_venus",
            "Transit Lilith to Natal Venus: Wild desire and unconventional beauty surge. You are drawn to what feels real and perhaps forbidden or outside the norm. Embrace the full authentic spectrum of what you value and desire.",
            "Транзитна Лилит към Натална Венера: Дивото желание и нестандартната красота нарастват. Привлечени сте от онова, което се чувства реално и може би забранено. Прегърнете пълния автентичен спектър на ценностите и желанията ви.")
        p("lilith_mars",
            "Transit Lilith to Natal Mars: Primal, rebellious action surfaces powerfully. You feel driven to act in ways that feel wild or outside social norms. Channel this energy authentically — it carries a raw, liberating power.",
            "Транзитна Лилит към Натален Марс: Първично, бунтарско действие изплува мощно. Чувствате се движени да действате по начини, чувстващи се диви. Насочете тази енергия автентично — тя носи сурова, освобождаваща сила.")
        p("lilith_jupiter",
            "Transit Lilith to Natal Jupiter: Wild, authentic expansion is invited. The parts of you deemed 'too much' are exactly what needs expressing. Greater freedom and authenticity in philosophy, faith, and self-expression are available.",
            "Транзитна Лилит към Натален Юпитер: Дивото, автентично разширение е поканено. Частите от вас, считани за 'прекалено', са точно онова, нуждаещо се от изразяване.")
        p("lilith_saturn",
            "Transit Lilith to Natal Saturn: Shadow material around authority, rules, and discipline surfaces. Where do you over-rebel or over-comply? Integration of authentic authority — neither suppressing wildness nor being ruled by it — is invited.",
            "Транзитна Лилит към Натален Сатурн: Материал на сянката около авторитета и дисциплината изплува. Поканата е за интеграция на автентичен авторитет — нито потискане на дивостта, нито управление от нея.")
        p("lilith_uranus",
            "Transit Lilith to Natal Uranus: Wild freedom and revolutionary shadow energy combine explosively. You may feel driven to radical, instinctual liberation. Channel this into genuine self-liberation rather than reactive destruction.",
            "Транзитна Лилит към Натален Уран: Дивата свобода и революционната енергия на сянката се съединяват взривоопасно. Насочете това в истинско самоосвобождение вместо в реактивно разрушение.")
        p("lilith_neptune",
            "Transit Lilith to Natal Neptune: The mystical and the wild merge. Hidden spiritual desires or taboo spiritual experiences surface. Healing through radical acceptance of all aspects of yourself — including the darkest and most instinctual.",
            "Транзитна Лилит към Натален Нептун: Мистичното и дивото се сливат. Скрити духовни желания или табуирани духовни преживявания изплуват. Лечение чрез радикално приемане на всички аспекти от себе си.")
        p("lilith_pluto",
            "Transit Lilith to Natal Pluto: The deepest shadow and the most transformative power combine. What has been most forbidden is surfacing for complete integration. The power you have most feared in yourself is the power that can transform you.",
            "Транзитна Лилит към Натален Плутон: Най-дълбоката сянка и най-трансформиращата сила се съединяват. Онова, което е било най-забранено, изплува за пълна интеграция. Силата, от която сте се страхували най-много, е тази, която може да ви трансформира.")
        p("lilith_chiron",
            "Transit Lilith to Natal Chiron: Wild self and wound meet. Shame around authentic desires has become a wound — healing becomes possible through radical self-acceptance. Claim the wild truth of who you are without apology.",
            "Транзитна Лилит към Натален Хирон: Дивото Аз и раната се срещат. Срамът около автентичните желания е станал рана — лечение е възможно чрез радикално самоприемане.")
        p("lilith_rahu",
            "Transit Lilith to Natal Rahu: Your evolutionary path leads through your shadow. Growth requires claiming the aspects of yourself most suppressed or shamed. Your wildness and authenticity are the gifts your soul came to offer.",
            "Транзитна Лилит към Натален Раху: Еволюционният ви път минава през сянката ви. Растежът изисква заявяване на аспектите от себе си, най-потиснати или засрамени.")
        p("lilith_lilith",
            "Transit Lilith to Natal Lilith (Lilith Return): The wild self demands full acknowledgment. Suppressed rage, desire, and authenticity that have been building demand release and integration. Claim the full, uncensored truth of who you are.",
            "Транзитна Лилит към Натална Лилит (Завръщане на Лилит): Дивото Аз изисква пълно признание. Потиснат гняв, желание и автентичност, натрупвани с времето, изискват освобождаване и интеграция.")
    }

    // ── Specific aspect interpretations (placeholder — extend per aspect type) ─
    val specific: Map<String, Pair<String, String>> = emptyMap()
    // Future keys: "uranus_mars_0" (conjunction), "uranus_mars_90" (square), etc.

    fun getGeneral(transitKey: String, natalKey: String): Pair<String, String>? =
        general["${transitKey}_${natalKey}"]

    fun getSpecific(transitKey: String, natalKey: String, aspectDeg: Int): Pair<String, String>? =
        specific["${transitKey}_${natalKey}_${aspectDeg}"]
}
