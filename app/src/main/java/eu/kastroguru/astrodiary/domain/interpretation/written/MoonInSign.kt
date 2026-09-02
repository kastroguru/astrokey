package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * Hand-written Moon-in-sign texts: what a person needs in order to feel safe, and how they react
 * before they have had time to think. Read second only to the Sun, and usually the placement people
 * recognise fastest in themselves.
 */
object MoonInSign {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val entries: Map<String, Bilingual> = mapOf(
        "moon_aries" to t(
            "You calm down by taking action. Letting a feeling smoulder inside often makes it bigger, so you tend to react quickly and speak plainly, moving on before others have caught up. You need a space where a flare-up is acceptable and won’t be held against you. Constructive outlets — exercise, a direct conversation, or a task you can finish — help. Practice a brief pause before responding to avoid burning bridges and to channel your courage productively.",
            "Успокоявате се, като действате. Когато оставите чувството да ври в себе си, то често става по-интензивно, затова реагирате бързо и директно и обикновено бързо ви минава. Нуждаете се от място, където избликът е позволен и не се държи срещу вас. Полезно е да намерите конструктивен изход — физическа активност, кратък разговор или конкретна задача — и да практикувате малка пауза преди реакция, за да избегнете изгаряне на мостове."
        ),
        "moon_taurus" to t(
            "You soothe yourself through the body and steady routines: food, familiar places, a trusted mug, predictable rhythms. You’re often slow to upset and slow to forgive; comfort for you tends to be physical rather than spoken. When shaken, you may resist change or withdraw. Creating gentle rituals for self-care, allowing small incremental changes, and deliberately choosing steadiness rather than stubbornness can help you stay open while preserving your need for security.",
            "Успокоявате се чрез тялото и рутината: храна, познати места, любимата чаша, стабилна дневна рутина. Често ви е трудно да се разстроите и бавно прощавате; утехата за вас е предимно физическа, не вербална. При твърде много раздрусване може да замръзнете или да се оттеглите. Полезно е да създадете безопасни ритуали за самоподдържане, да отделяте пространство за промяна в малки стъпки и да позволите на надеждността да ви помогне да превърнете устойчивостта в избор, а не в заяждане."
        ),
        "moon_gemini" to t(
            "You process by talking and trying different angles — once a feeling is named, it usually feels lighter, sometimes after several rounds. Silence can feel threatening. You often intellectualize emotion, dissecting it until it becomes a concept rather than a lived sensation. Pairing conversation with journaling, movement, or a small ritual helps you actually feel what you talk about instead of just explaining it. That steadies scattering and deepens connection.",
            "Преработвате преживяванията чрез говорене и смяна на гледни точки — когато нещо бъде назовано, ви става по-леко, понякога след няколко обяснения. Тишината може да ви буди тревога. Склонни сте да интелектуализирате чувствата, да ги дисектирате и да ги размествате от дума в дума, което понякога ви отдалечава от самото им усещане. Полезно е да комбинирате вербализацията с писане, разходка или ритуал, който помага емоцията да бъде изпитана, а не само описана."
        ),
        "moon_cancer" to t(
            "You need to belong — to a place or a person. Caring comes almost automatically: you remember small details, maintain household rhythms, and protect those close to you. When hurt, you’re more likely to withdraw inward than to say what’s wrong. Feeling needed and accepted gives you a sense of safety, but it can also lead you to neglect boundaries. Practicing clear expression of needs and allowing others to care helps you nurture in a balanced, sustainable way.",
            "Имaте нужда да принадлежите — на място или на човек. Грижата ви идва почти автоматично: помните дребни факти, пазите домашния ритъм и пазите хората близо. Когато сте наранени, по-често затваряте в себе си, вместо да кажете какво ви боли. Чувството, че сте нужни и приети, ви дава сигурност, но може да ви кара да пренебрегвате собствените граници. Практикувайте ясно споделяне и приемане на помощ, за да подхранвате и давате здравословно."
        ),
        "moon_leo" to t(
            "You need warmth directed at you personally, not generic approval. With that attention you become generous and radiant; without it you can feel invisible and start seeking notice. A small, sincere gesture matters more to you than a large impersonal compliment. Channel this need into creative expression, leadership, or caring for projects and people, and watch for tendencies to perform or dramatize just to be seen. Authenticity carries more reward than spectacle.",
            "Нуждаете се от топлина, отправена лично към вас, не просто общо одобрение. Когато я получавате, сте щедри и сияещи; без нея се чувствате невидими и може да започнете да търсите внимание. Малък искрен жест или лично признание често достига по-далеч от голямо, безлично възхищение. Канализирайте нуждата от признание в творческо изразяване, лидерство или грижа за деца/проекти, и се пазете от тенденцията да драматизирате, за да получите потвърждение."
        ),
        "moon_virgo" to t(
            "You feel better when things are in order — the list written, the drawer sorted, the issue named. Caring for others often takes the form of practical help; worry becomes your way of attending. You show love by being useful rather than by saying it, and you tend to postpone rest. Learning to honor your need for downtime, set boundaries, and accept that imperfection does not equal lack of care will make your service sustainable. Small restorative rituals help.",
            "По-добре се чувствате, когато нещата са подредени — списъкът е написан, чекмеджето е сортирано, проблемът е назован. Често показвате грижа с практична помощ и полезни действия, а тревогата ви може да ви кара постоянно да поправяте нещо. Ще усетите дисбаланса, когато отлагате собствената си почивка, защото винаги има още една задача. Конструктивният изход е да приемете, че грижата не изисква съвършенство, да поставяте граници и да оставяте време за възстановяване."
        ),
        "moon_libra" to t(
            "Conflict in a room unsettles you, even when it’s not about you. You steady yourself by smoothing misunderstandings and creating harmony — a skill that makes you pleasant company, but it can also leave your needs last. There’s a risk of losing yourself in the pursuit of peace. Practicing calm self-expression and setting boundaries helps you maintain fairness and aesthetic balance in relationships without sacrificing your own priorities.",
            "Разстройват ви напреженията във взаимоотношенията, дори когато нямат директно отношение към вас. Стабилността намирате, като изглаждате недоразумения и създавате хармония — това ви прави приятна компания, но често поставяте нуждите на другите пред собствените. Има риск да изгубите позицията си в стремежа към мир. Практикувайте да изразявате желанията си спокойно и да поставяте граници, като едновременно търсите справедливост и естетично равновесие в отношенията."
        ),
        "moon_scorpio" to t(
            "You feel things intensely and show little of it on the surface. Trust is given slowly, tested quietly, and withdrawn decisively. You seek depth and transformation; intimacy is profound for you but can also bring jealousy or control dynamics. It helps to have at least one person or environment that doesn’t flinch at your intensity, and to use creative or therapeutic outlets as safe channels for powerful emotions. Practicing small acts of vulnerability can make closeness less risky.",
            "Чувствате всичко на пълна сила и показвате малко от това на повърхността. Доверието се дава бавно, проверява се тихо и може да бъде отнето окончателно. Търсите дълбочина и интензитет; интимността за вас е трансформационна, но също така може да носи ревност или контрол. Полезно е да намерите поне един човек или среда, които не се плашат от искреността ви, и да използвате творческата или терапевтичната експресия като безопасен канал за интензивните си емоции."
        ),
        "moon_sagittarius" to t(
            "You do best with room to breathe — physical or intellectual. Freedom, travel, big ideas and a sense of direction calm you more than reassurance. When confined you can grow restless and tend to defuse heavy moments with humor instead of sitting with them. Your optimism and frankness bring energy but can also postpone needed emotional work. Practical suggestions: build projects that allow movement and learning, and create small practices that let feelings be acknowledged rather than pushed aside.",
            "Имате нужда от простор и движение — физическо или интелектуално. Свободата, пътуванията, идеите за бъдещето и чувството за посока ви успокояват повече от успокоителни думи. Когато сте задушени или ограничени, ставате неспокойни и може да се шегувате с тежки моменти, за да не ги преживявате. Честата склонност към оптимизъм и прямота носи ентусиазъм, но и риск да пренесете проблемите на заден план. Практически подход: запазете възможности за учене и движение и намерете начин да давате на емоциите време да се случат."
        ),
        "moon_capricorn" to t(
            "You tend to manage feelings by taking charge and organizing circumstances; emotions are often postponed until things feel under control. You may have been the responsible one early on, so asking for help can feel like a failure rather than an option. Your self-discipline brings stability but can lead to accumulated, unprocessed needs. Constructive steps: schedule regular check‑ins with yourself, allow small acts of vulnerability, and practice accepting support as a functional, not failing, choice.",
            "Емоциите ви се справят чрез действие и организация — поемате задачите и чувствата се отлагат, докато не усетите контрол. Често сте били отговорният човек рано и молбата за помощ може да ви се струва като признание за слабост. Дисциплината ви дава стабилност, но долу може да се натрупват нерешени нужди и тъга. Полезно е да практикувате умишлено време за изразяване, да разрешавате уязвимостта си по малки крачки и да приемате, че опитът да контролирате всичко може да изтощи."
        ),
        "moon_aquarius" to t(
            "You need space to step back and view your feelings objectively, and you’re most at ease when nobody demands emotional availability on their timetable. Closeness scheduled by others tends to cool you rather than warm you; distance can be comforting rather than indifferent. You often express attachment through shared ideals or group belonging, which can be rewarding but may feel impersonal. Practical guidance: set clear boundaries while offering chosen, authentic gestures of warmth to deepen connection without losing autonomy.",
            "Нужно ви е отстъпване към себе си — да гледате чувствата отстрани и да запазите емоционална автономия. Най-спокойни сте, когато не се очаква от вас да отговаряте на нечии емоционални правила. Близостта по чужд график може да ви направи по-хладни, не по-топли; пространството ви утешава, не означава безразличие. Връзките може да проявявате чрез общи каузи и интелектуална връзка, но има риск от дистанциране. Полезно: създайте ясни граници и малки жестове на топлина, които се чувстват автентични."
        ),
        "moon_pisces" to t(
            "You easily absorb the moods around you, so solitude helps you sort out which feelings truly belong to you. Creative outlets, water, sleep and music restore you and provide psychic reset. Your compassion is genuine, but it can make saying no difficult and lead to emotional fatigue. Helpful habits: establish grounding rituals, set gentle but clear boundaries, and use artistic or restful practices to process empathy without losing yourself.",
            "Често попивате настроенията около вас без да искате, затова ви трябва време сам, за да различите кое е ваше. Творчески практики, вода, сън и музика ви възстановяват и помагат да обработите преживяванията. Добротата и съпричастността ви са силни, но и правят казването на „не“ по-трудно, което може да ви изтощава. Практически подход: работете с утвърдени граници и ритуали за възстановяване, учете се да назовавате нуждите си и да си давате почивка."
        ),
    )
}
