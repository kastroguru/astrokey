package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * Hand-written Sun-in-sign texts — the placement a newcomer reads first, so it is written properly
 * rather than composed. Keys match NatalInterpretations: planet key + sign, lower case.
 */
object SunInSign {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val entries: Map<String, Bilingual> = mapOf(
        "sun_aries" to t(
            "You come alive when you are the one who starts things. Waiting for permission drains you, while a challenge wakes you up — you would rather act and correct course than deliberate and stay put. The cost is a trail of beginnings: finishing is the part you have to choose deliberately.",
            "Оживявате, когато вие сте този, който започва. Чакането на разрешение ви изцежда, а предизвикателството ви буди — предпочитате да действате и да коригирате, отколкото да обмисляте и да останете на място. Цената е следа от начала: довършването е частта, която трябва да избирате съзнателно."
        ),
        "sun_taurus" to t(
            "You become yourself through what you build and keep. You need to see something real for your effort — a home, a skill, money in the account — and you get there by being steady rather than quick. Pushed to change before you are ready, you dig in, and that stubbornness is both your strength and the wall you occasionally hit.",
            "Ставате себе си през онова, което изграждате и запазвате. Имате нужда да видите нещо реално от усилието си — дом, умение, пари в сметката — и стигате дотам с постоянство, не с бързина. Ако ви натискат да се промените, преди да сте готови, се вкопавате, и това упорство е едновременно силата ви и стената, в която понякога се удряте."
        ),
        "sun_gemini" to t(
            "You are at your best with something new to find out and someone to tell it to. Curiosity is not a hobby for you, it is how you stay alive, and you often understand your own opinion only once you have said it out loud. The trap is skimming: many beginnings, few things known all the way through.",
            "В най-добрата си форма сте, когато има какво ново да разберете и на кого да го разкажете. Любопитството не е хоби, а начинът, по който сте живи, и често разбирате собственото си мнение само след като го изречете. Капанът е повърхностността: много начала, малко неща, узнати докрай."
        ),
        "sun_cancer" to t(
            "You are yourself where you feel safe, and you build that safety for other people too. You read a room before you enter it, remember how things felt years later, and protect what is yours without announcing it. When you feel unsafe you withdraw rather than fight, which others sometimes mistake for indifference.",
            "Себе си сте там, където се чувствате в безопасност, и изграждате тази безопасност и за другите. Прочитате стаята, преди да влезете, помните как са се усещали нещата години по-късно и пазите своето, без да го обявявате. Когато не сте в безопасност, се оттегляте, вместо да се борите, и другите понякога го приемат за безразличие."
        ),
        "sun_leo" to t(
            "You need your effort to be seen — not applause for its own sake, but the sense that what you give reaches someone. Given that, you are generous, warm and hard to discourage. Denied it, you either perform harder or go quiet, and the second is the more expensive of the two.",
            "Имате нужда усилието ви да бъде видяно — не аплаузи за самите аплаузи, а усещането, че онова, което давате, стига до някого. Когато го има, сте щедри, топли и трудни за отчайване. Когато липсва, или се престаравате, или замлъквате, и второто ви струва по-скъпо."
        ),
        "sun_virgo" to t(
            "You come into your own by making things work. You see the flaw everyone else steps over, and fixing it is genuinely satisfying rather than a chore. The same eye turns on you, which is why you can do excellent work and still feel behind — the standard you are measuring against is your own.",
            "Ставате себе си, като карате нещата да работят. Виждате дефекта, който всички останали прескачат, и поправянето му наистина ви удовлетворява, а не ви тежи. Същото око се обръща и към вас — затова можете да свършите отлична работа и пак да се чувствате изостанали: мярката, с която се мерите, е ваша собствена."
        ),
        "sun_libra" to t(
            "You find yourself in relation to other people. You weigh how a decision lands on someone else almost before you weigh your own preference, which makes you fair and pleasant company — and occasionally leaves you unsure what you actually wanted. Saying the unpopular thing is the skill worth practising.",
            "Намирате себе си в отношение с другите. Претегляте как решението ще падне на някой друг почти преди собственото си предпочитание, което ви прави справедливи и приятни — и понякога ви оставя несигурни какво всъщност сте искали. Умението, което си струва да упражнявате, е да кажете непопулярното."
        ),
        "sun_scorpio" to t(
            "You do not do things halfway. You want to know what is really going on beneath the polite version, you keep your own counsel, and you attach deeply to few people rather than lightly to many. Loyalty runs the same depth as suspicion, and learning when to let go is the work of a lifetime.",
            "Не правите нещата наполовина. Искате да знаете какво наистина става под учтивата версия, пазите своето за себе си и се привързвате дълбоко към малко хора, вместо леко към много. Верността ви е толкова дълбока, колкото и подозрението, а да се научите кога да пускате е работа за цял живот."
        ),
        "sun_sagittarius" to t(
            "You need room and a reason. Meaning matters more to you than security, so you will trade comfort for a bigger view — travel, study, a belief worth having. Boxed in, you become restless and blunt, and the honesty that usually serves you starts arriving without the tact.",
            "Имате нужда от простор и от смисъл. Смисълът ви е по-важен от сигурността, затова ще размените удобството за по-широк изглед — пътуване, учене, вяра, която си струва. Затворени в кутия, ставате неспокойни и резки, а честността, която обикновено ви служи, започва да идва без такта."
        ),
        "sun_capricorn" to t(
            "You are built for the long version. You would rather earn something slowly and keep it than be handed it, you take responsibility earlier than most, and you are usually the one people rely on. The risk is measuring yourself only by what you have achieved, and carrying far more than you ever admit.",
            "Направени сте за дългата версия. Предпочитате да заслужите нещо бавно и да го запазите, отколкото да ви го подарят, поемате отговорност по-рано от повечето и обикновено вие сте онзи, на когото хората разчитат. Рискът е да се мерите единствено с постигнатото и да носите много повече, отколкото признавате."
        ),
        "sun_aquarius" to t(
            "You need to think for yourself, and you notice the pattern rather than the individual case. Being told what to believe produces the opposite result, and you are often the one in the room asking why it is done this way at all. The distance that gives you clarity can also keep people further away than you meant.",
            "Имате нужда да мислите сами и забелязвате модела, а не отделния случай. Когато ви кажат в какво да вярвате, получавате обратния резултат, и често вие сте човекът, който пита защо изобщо се прави така. Разстоянието, което ви дава яснота, може и да държи хората по-далеч, отколкото сте искали."
        ),
        "sun_pisces" to t(
            "You take in more than you can explain. Moods, music, other people's states — they reach you directly, which makes you compassionate and imaginative, and also easy to flood. Your work is boundaries: knowing which feeling in the room is actually yours, and where your help stops being help.",
            "Поемате повече, отколкото можете да обясните. Настроения, музика, състоянията на другите — стигат до вас направо, което ви прави съпричастни и въображаеми, но и лесни за наводняване. Вашата работа са границите: да знаете кое чувство в стаята е наистина ваше и къде помощта ви спира да е помощ."
        ),
    )
}
