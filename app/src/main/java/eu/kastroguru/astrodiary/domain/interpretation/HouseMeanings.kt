package eu.kastroguru.astrodiary.domain.interpretation

/**
 * The twelve houses as areas of life — where something happens, as opposed to what it is
 * ([PlanetMeanings]) or how it behaves ([SignMeanings]). Keyed 1..12.
 */
object HouseMeanings {

    val byHouse: Map<Int, Bilingual> = mapOf(
        1 to t(
            "The first house describes how you present yourself and the first impression you make: appearance, manner, and initial responses. It reflects self-expression, initiative, and how you begin new cycles. This area often relates to bodily presence and personal identity — the style you adopt and the confidence you project. Practically, cultivate authentic presence through small, consistent actions, experiment with how you express yourself, and develop embodied habits that support healthier self-image and decisive beginnings.",
            "Първият дом описва начина, по който се показвате пред другите и първото впечатление, което оставяте — външен стил, тон на поведението и първите ви реакции. Тази област отразява начин на самоизразяване, инициативност и начина, по който предприемате нови начала. Може да акцентира върху тялото и здравето, както и върху самосъзнанието. Практически, работете върху искреното присъствие, малките жестове на увереност и експериментите със себеизява, които ви помагат да се чувствате по-цялостни и видими."
        ),
        2 to t(
            "The second house points to personal resources, possessions, and the values that shape your sense of security — income, skills, things you own, and your feeling of self-worth. It often reflects how financial matters mirror internal values. Practically, this house encourages building reliable income streams, mindful budgeting, developing skills that increase earning potential, and separating inner worth from material accumulation. The work here is practical stewardship: cultivate what you value and let money serve those priorities rather than define them.",
            "Вторият дом насочва към личните ресурси, стойността, която придавате на нещата, и начините, по които създавате сигурност — приходи, умения, притежания и усещане за собствена стойност. Тази сфера често оглежда отношението ви към парите като отражение на вътрешни ценности. Практически, фокусът може да бъде върху изграждане на доходи, управление на харчовете, развиване на умения, които носят стабилност, и укрепване на самочувствието извън материалните вещи."
        ),
        3 to t(
            "The third house governs everyday communication, the immediate environment, short trips, and learning style. It includes siblings, neighbors, local contacts, and the channels through which you exchange information — speaking, writing, commuting, and quick journeys. This area encourages curiosity, clarity, and adaptability but can also bring impatience or superficial chatter. Practically, cultivate attentive listening, organize your ideas before sharing, and use local or digital networks for learning and collaboration. Short-term plans, errands, and skill-based learning often play out here.",
            "Третият дом управлява ежедневната комуникация, близкото обкръжение, кратките пътувания и учебния стил. Тук се проявяват братя и сестри, съседите, колеги по квартални проекти и начинът, по който обменяте информация — говорене, писане, пътувания на кратки разстояния и социални медии. Тази област насърчава любопитство, яснота и адаптивност; предизвикателствата могат да включват прибързано говорене или повърхностни впечатления. Практически, работете върху слушането, структурирането на идеите и използването на близките мрежи за учене и обмен."
        ),
        4 to t(
            "home, family roots, and emotional security",
            "домът, семейните корени и емоционалната сигурност"
        ),
        5 to t(
            "play, creativity, romance, and personal self‑expression",
            "игра, творчество, романтика и лично самоизразяване"
        ),
        6 to t(
            "daily work, health, and everyday routines",
            "ежедневна работа, здраве и рутинни грижи"
        ),
        7 to t(
            "close relationships and partnerships",
            "близки връзки и партньорства"
        ),
        8 to t(
            "The eighth house often points to what’s shared and what’s hidden: other people’s money (debts, loans, inheritances), deep emotional bonds, and the losses or crises that transform you. It’s a territory of psychological depth, taboos, power dynamics, and intimate dependence — where endings lead to rebirth. Issues of control versus surrender, secrecy, and joint resources tend to surface here. Practically, this area invites clear agreements about money and boundaries, honest work with fear and desire, and using therapy or ritual for healing and regeneration.",
            "Домът на споделеното и на скритото често насочва към теми като чужди пари (включително дългове, заеми и наследства), интимните връзки, силните емоционални привързаности и кризите, които променят живота ви. Тази област говори за трансформацията, за това кое умира и кое се ражда отново — и за начините, по които сте уязвими или зависими. Може да включва табута, психологическа дълбочина и силови динамики в отношенията. Практически: изследвайте границите и споделянето, разглеждайте финансовите договори внимателно и използвайте терапия или ритуали за интеграция и възстановяване."
        ),
        9 to t(
            "The ninth house relates to broadening horizons: travel, higher education, philosophy, belief systems, and the search for a larger framework of meaning. It brings teachers, law, foreign cultures, and experiences that expand how you understand the world. Expect long journeys, study, publishing, or spiritual quests to feature here. The risk is settling into rigid dogma; the opportunity is to test beliefs against real experience. Practically, this house encourages travel, honest study, teaching or sharing learned insights, and staying open to new paradigms that reshape your worldview.",
            "Домът на разширяването и търсенето насочва вниманието към пътувания, висше образование, философия, вяра и смисъл. Тук се появяват учителите, законът, чуждите култури и опитите, които разширяват перспективата ви. Може да означава дълги пътувания, формално или неформално обучение, публикуване, духовни практики и търсене на “по-голямата картина”. Предизвикателствата включват догматизъм или повърхностно разбиране; полезно е да се упражнява критично мислене, културна чувствителност и готовност да промените вярванията си чрез опит."
        ),
        10 to t(
            "The tenth house concerns your public standing: career, reputation, authority you answer to, and the authority you become. It governs long-term goals, visible achievements, and the role you play in society. Career shifts, responsibility, and leadership ambitions often show here, along with how you want to be recognized. Tensions can arise between public image and private needs. Practically, this house invites clarity about professional ethics, steady work toward goals, mentoring or managing responsibilities, and shaping a reputation that aligns with your deeper values.",
            "Домът на публичния образ и кариерата показва как се позиционирате пред света — репутация, професионални амбиции, отговорности и авторитетните фигури около вас. Тази област говори за дългосрочна цел, публични постижения и начина, по който искате да бъдете видяни и оценени. Може да насочва към предприемачество, служба, лидерство или ролята, която наследявате. Предизвикателствата включват размиване между професионалния и личния живот; полезно е да изграждате етика, последователност и да обмислиш какво наследство желаеш да оставиш."
        ),
        11 to t(
            "The eleventh house covers friends, groups, and the hopes and projects that point toward your future. It governs networks, collective goals, causes, and the communities where you find practical support and shared vision. This area can bring collaboration, technological or progressive initiatives, and social capital — but also groupthink or loss of individuality if you bend too much to the collective. Practically, cultivate authentic friendships, choose groups that reflect your values, and use networking as a resource for long-term projects and meaningful social change.",
            "Домът на приятелите и надеждите насочва към социалните мрежи, групите и каузите, към които се присъединявате, както и към визията за бъдещето, която носите. Тук се срещат съмишленици, колективни проекти, технологични и иновативни идеи и връзки, които подпомагат вашите дългосрочни цели. Може да носи подкрепа и възможности, но и риск от групово мислене или компромис с индивидуалността. Практически: инвестирайте в автентични отношения, прегледайте дали групите отразяват ценностите ви и използвайте мрежата за обща реализация на цели."
        ),
        12 to t(
            "The 12th house points to what is hidden, solitary, and often unconscious. It covers solitude, secrets, shadow patterns, spiritual practice, institutions (hospitals, retreats), and processes of closure or atonement. Themes here may surface through dreams, subtle sensitivities, or circumstances that push you inward and toward private work. The house can indicate places where you withdraw to heal, or where boundaries blur. It does not mean something is missing, but rather a field for inner integration: with conscious attention, 12th-house material often becomes a quiet resource or deeper wisdom.",
            "Дванадесетият дом насочва към онова, което е скрито, тихо и често несъзнавано. Тук попадат самотата, тайните, подсъзнателните модели, духовните практики, институционалните опити (болници, затвори, отшелничество) и процесите на изкупление или завършване. Темите на този дом може да се проявят чрез сънища, чувствителност към колективното поле или чрез ситуации, които ни подтикват към оттегляне и вътрешна работа. Това не означава липса, а канал за пречистване и интеграция: когато се работи съзнателно, слабостите тук често се превръщат във скрит ресурс или духовна зрялост."
        ),
    )

    /**
     * Three-or-four-word labels, for sentences that have to mention a house twice without turning
     * into a paragraph — e.g. "money and what you own" instead of the full description.
     */
    val shortByHouse: Map<Int, Bilingual> = mapOf(
        1 to t("the way you come across", "начина, по който се показвате"),
        2 to t("money and what you own", "парите и притежанията"),
        3 to t("everyday talk and the people close by", "ежедневното говорене и близките наоколо"),
        4 to t("home and family", "дома и семейството"),
        5 to t("play, creating and romance", "играта, създаването и романтиката"),
        6 to t("daily work and health", "ежедневната работа и здравето"),
        7 to t("close relationships", "близките връзки"),
        8 to t("what is shared and what is hidden", "споделеното и скритото"),
        9 to t("the wider world and what you believe", "широкия свят и убежденията"),
        10 to t("your standing in public", "мястото ви пред света"),
        11 to t("friends and what you hope for", "приятелите и надеждите"),
        12 to t("solitude and what stays out of sight", "самотата и невидимото"),
    )

    fun of(house: Int): Bilingual? = byHouse[house]

    fun shortOf(house: Int): Bilingual? = shortByHouse[house]
}
