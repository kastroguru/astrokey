package eu.kastroguru.astrodiary.domain.interpretation

/**
 * The twelve houses as areas of life — where something happens, as opposed to what it is
 * ([PlanetMeanings]) or how it behaves ([SignMeanings]). Keyed 1..12.
 */
object HouseMeanings {

    val byHouse: Map<Int, Bilingual> = mapOf(
        1 to t(
            "how you come across and how you start things — the face people meet first, and your own body",
            "как излизате пред хората и как започвате нещата — лицето, което се среща първо, и собственото ви тяло"
        ),
        2 to t(
            "what you own and what you are worth to yourself — money you earn, things you keep, your own resources",
            "какво притежавате и колко струвате в собствените си очи — парите, които печелите, нещата, които пазите, вашите ресурси"
        ),
        3 to t(
            "everyday talking and moving about — brothers and sisters, neighbours, short trips, learning by doing",
            "ежедневното говорене и движение — братя и сестри, съседи, кратки пътувания, учене в движение"
        ),
        4 to t(
            "home and where you come from — family, the place you retreat to, and what you inherited without choosing",
            "домът и откъде идвате — семейство, мястото, в което се оттегляте, и онова, което сте наследили без да избирате"
        ),
        5 to t(
            "play, creating and romance — children, what you make for the joy of it, and taking a chance",
            "игра, създаване и романтика — деца, онова, което правите за удоволствие, и рискът да опитате"
        ),
        6 to t(
            "daily work and the body's upkeep — routine, health, the job as it is actually lived, and being useful",
            "ежедневната работа и поддържането на тялото — рутина, здраве, работата такава, каквато се живее, и да си полезен"
        ),
        7 to t(
            "one-to-one relationships — partners in love and in business, and the traits you meet in other people rather than in yourself",
            "връзките един на един — партньори в любовта и в работата, и чертите, които срещате в другите, а не в себе си"
        ),
        8 to t(
            "what is shared and what is hidden — other people's money, deep attachments, loss, and what changes you for good",
            "онова, което е споделено, и онова, което е скрито — чуждите пари, дълбоките привързаности, загубата и това, което ви променя завинаги"
        ),
        9 to t(
            "the wider world and what you make of it — travel, study, belief, and the search for a bigger frame",
            "по-широкият свят и какво разбирате от него — пътуване, учене, вяра и търсенето на по-голяма рамка"
        ),
        10 to t(
            "your standing in public — career, reputation, the authority you answer to and the authority you become",
            "мястото ви пред света — кариера, репутация, властта, на която отговаряте, и властта, в която се превръщате"
        ),
        11 to t(
            "the people you choose and what you hope for — friends, groups, causes, and the future you want",
            "хората, които избирате, и онова, на което се надявате — приятели, групи, каузи и бъдещето, което искате"
        ),
        12 to t(
            "what happens out of sight — solitude, what you keep from yourself, endings, and where the boundary between you and everything else thins out",
            "онова, което става извън погледа — самотата, нещата, които криете от себе си, краищата, и мястото, където границата между вас и всичко останало изтънява"
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
