package eu.kastroguru.astrodiary.domain.interpretation.written

import eu.kastroguru.astrodiary.domain.interpretation.Bilingual

/**
 * The first half of a chain reading: what a body is doing in the house it stands in, written as one
 * or two plain sentences. Paired with [RulerHouseKey] this turns all 1,872 chain readings into
 * written text — 156 lines here plus twelve there, instead of 1,872 separate paragraphs.
 *
 * Key: planet key + house, e.g. "sun_10". Filled body by body; anything not yet written falls back
 * to the composed version, so nothing is ever blank.
 */
object PlanetInHouseLines {

    private fun t(en: String, bg: String) = Bilingual(en, bg)

    val entries: Map<String, Bilingual> = mapOf(
        "sun_1" to t(
            "You are the one who sets the tone, and people feel you arrive. You are most alive when you do not have to make yourself smaller to fit in.",
            "Вие сте човекът, който задава тона, и хората ви усещат отдалеч. Най-жив сте, когато не ви се налага да се смалявате, за да се вместите."
        ),
        "sun_2" to t(
            "Your sense of purpose runs through what you have built and what you own. Money here is not vanity — it is your measure of whether life is working.",
            "Смисълът ви минава през това какво сте изградили и какво имате. Парите тук не са суета, а мярка дали животът ви работи."
        ),
        "sun_3" to t(
            "You come alive in conversation, reading and explaining. The small daily traffic and the people nearby give you the material you make yourself out of.",
            "Оживявате в разговори, четене и обясняване. Дребното ежедневие и хората наоколо ви дават материала, от който правите себе си."
        ),
        "sun_4" to t(
            "Home is your centre. Whatever you achieve outside, you judge it by how things stand indoors.",
            "Центърът ви е домът. Каквото и да постигнете навън, преценявате го по това как стоят нещата у вас."
        ),
        "sun_5" to t(
            "You become yourself through play, making things and love. With no joy in your life, everything else starts to feel pointless.",
            "Ставате себе си през игра, творене и любов. Няма ли радост в живота ви, всичко останало започва да ви се струва безсмислено."
        ),
        "sun_6" to t(
            "Your purpose is in the work and in a day that holds together. What you are proud of is not the title but the fact that something runs because you keep it running.",
            "Смисълът ви е в работата и в подреден ден. Гордеете се не с титлата, а с това, че нещо работи, защото вие го поддържате."
        ),
        "sun_7" to t(
            "You see yourself through the other person. Partnership shows you who you are — and with nobody there, you are short of a mirror.",
            "Виждате себе си през другия. Партньорството ви показва кой сте — а когато няма с кого, ви липсва огледало."
        ),
        "sun_8" to t(
            "You are pulled towards the deep and the uncomfortable: other people's money, secrets, loss, change. Shallow things bore you quickly.",
            "Тегли ви към дълбокото и неудобното: чужди пари, тайни, загуба, промяна. Плитките неща бързо ви отегчават."
        ),
        "sun_9" to t(
            "You need to widen out in order to be yourself: travel, study, something to believe in. Shut into a small circle, you begin to fade.",
            "За да сте себе си, трябва да се разширявате: пътуване, учене, нещо, в което да вярвате. Затворите ли се в тесен кръг, започвате да гаснете."
        ),
        "sun_10" to t(
            "Your place is out in the open, in front of people. Work and reputation are how you exist — which is both your strength and your exposure.",
            "Мястото ви е навън, пред хората. Работата и името са начинът, по който съществувате — и това е едновременно силата и слабото ви място."
        ),
        "sun_11" to t(
            "You are yourself among your own people — friends, groups, a shared cause. Something achieved alone gives you only half the pleasure.",
            "Себе си сте сред своите хора — приятели, групи, обща кауза. Постигнатото сами ви радва наполовина."
        ),
        "sun_12" to t(
            "You work quietly, out of sight. Being on show tires you, and the things that matter most happen to you when nobody is watching.",
            "Работите на тихо, извън погледа. Публичността ви изморява, а най-важното ви се случва, когато никой не гледа."
        ),

        // ── Moon: what you need in order to be all right ──────────────────────
        "moon_1" to t(
            "Your mood is visible before you say anything, and people react to it. You need to be yourself in front of others rather than play a part.",
            "Настроението ви се вижда, преди да сте казали нещо, и хората реагират на него. Имате нужда да сте себе си пред другите, а не да играете роля."
        ),
        "moon_2" to t(
            "You settle when there is something solid: money in the account, food in the house, familiar things around you. Insecurity keeps you tense even when everything else is fine.",
            "Успокоявате се, когато има нещо стабилно: пари в сметката, храна в къщата, познати неща около вас. Несигурността ви държи напрегнати дори когато всичко друго е наред."
        ),
        "moon_3" to t(
            "You need to talk about what is bothering you, and soon. Keeping quiet weighs on you more than the problem itself.",
            "Имате нужда да говорите за онова, което ви тежи, и то скоро. Мълчанието ви тежи повече от самия проблем."
        ),
        "moon_4" to t(
            "Home is literally your shelter. When things are calm there you are calm; when they are not, nothing outside makes up for it.",
            "Домът ви е буквално убежище. Когато там е спокойно, и вие сте спокойни; когато не е, нищо навън не го компенсира."
        ),
        "moon_5" to t(
            "You need joy and some attention in order to be well. Play and making things are not a luxury for you — they are how you recover.",
            "Имате нужда от радост и от малко внимание, за да сте добре. Играта и творенето не са ви лукс, а начинът, по който се възстановявате."
        ),
        "moon_6" to t(
            "You calm down by putting things in order: a list, a routine, something finished. Chaos in small matters upsets you more than you admit.",
            "Успокоявате се, като въведете ред: списък, режим, нещо довършено. Хаосът в дребното ви разстройва повече, отколкото признавате."
        ),
        "moon_7" to t(
            "You need someone beside you. On your own you are restless, and closeness steadies you more than any achievement does.",
            "Имате нужда от някого до себе си. Сами сте неспокойни, а близостта ви стабилизира повече от каквото и да е постижение."
        ),
        "moon_8" to t(
            "You attach deeply and do not let go easily. What you need is real closeness, not a pleasant surface.",
            "Привързвате се дълбоко и не пускате лесно. Имате нужда от истинска близост, а не от приятна повърхност."
        ),
        "moon_9" to t(
            "You settle by getting out of the frame: a trip, a new subject, different surroundings. Closed spaces and closed circles wear you down.",
            "Успокоявате се, като излезете от рамката: път, нова тема, друга среда. Затвореното пространство и затвореният кръг ви изтощават."
        ),
        "moon_10" to t(
            "You need to be respected for what you do. Your private life and your work mix, and recognition works on you like comfort.",
            "Имате нужда да ви уважават за онова, което правите. Личното и работата ви се смесват, а признанието ви действа като утеха."
        ),
        "moon_11" to t(
            "Your people are your comfort. Among friends you recover, and without a circle you feel oddly unmoored.",
            "Своите хора са ви утехата. Сред приятели се възстановявате, а без общност се чувствате някак незакрепени."
        ),
        "moon_12" to t(
            "You need quiet and time with nobody in it. You carry other people's moods, and being alone is how you work out which of it was yours.",
            "Имате нужда от тишина и от време без никого. Носите чуждите настроения и точно насаме разбирате кое от тях е било ваше."
        ),

        // ── Mercury: how you think and how you say it ─────────────────────────
        "mercury_1" to t(
            "You think out loud and talk about yourself before you notice you are doing it. People know you by the way you speak.",
            "Мислите на глас и говорите за себе си, преди да сте се усетили. Хората ви познават по начина, по който говорите."
        ),
        "mercury_2" to t(
            "Your mind works practically: what it costs, whether it pays off, whether it will last. Talk with nothing in it loses you quickly.",
            "Умът ви работи практично: колко струва, изплаща ли се, ще остане ли. Разговор без съдържание бързо ви губи."
        ),
        "mercury_3" to t(
            "You think fast and in several directions, and learning comes easily. You need something new to find out every day.",
            "Мислите бързо и в няколко посоки, а ученето ви идва лесно. Имате нужда всеки ден да има какво ново да разберете."
        ),
        "mercury_4" to t(
            "Your thinking is tied to home and to the past. Your decisions run through what things were like where you grew up, whether you admit it or not.",
            "Мисленето ви е свързано с дома и с миналото. Решенията ви минават през това как е било там, където сте израснали, независимо дали го признавате."
        ),
        "mercury_5" to t(
            "Your mind is playful and likes to invent. Learning goes well while it is entertaining and stalls the moment it becomes an obligation.",
            "Умът ви е игрив и обича да измисля. Ученето ви върви, докато е забавно, и спира в момента, в който стане задължение."
        ),
        "mercury_6" to t(
            "Your mind is a working tool: details, lists, what is missing. You spot the mistake before anyone else has started reading.",
            "Умът ви е работен инструмент: детайли, списъци, какво липсва. Забелязвате грешката, преди другите да са започнали да четат."
        ),
        "mercury_7" to t(
            "You think by talking to another person. Your ideas come clear when there is someone to argue back.",
            "Мислите, като говорите с друг човек. Идеите ви се избистрят, когато има кой да ви възрази."
        ),
        "mercury_8" to t(
            "Your mind digs. The version meant for public consumption does not interest you — what is underneath it does.",
            "Умът ви копае. Версията за пред хората не ви интересува; интересува ви какво стои под нея."
        ),
        "mercury_9" to t(
            "You think broadly and care what it all means. Fine detail bores you, while the big picture keeps you awake.",
            "Мислите нашироко и ви е важно какво значи всичко това. Дребният детайл ви отегчава, а голямата картина ви държи."
        ),
        "mercury_10" to t(
            "You think in terms of results and of how it will sound outside. Your words carry weight because you do not spend them on nothing.",
            "Мислите за резултат и за това как ще звучи навън. Думите ви носят тежест, защото не ги харчите напразно."
        ),
        "mercury_11" to t(
            "Your ideas come from the people around you and from shared causes. You think in the future tense and in the plural.",
            "Идеите ви идват от хората около вас и от общите каузи. Мислите в бъдеще време и в „ние“."
        ),
        "mercury_12" to t(
            "Your mind works below the surface and often knows before you can explain it. You need quiet to hear what you actually think.",
            "Умът ви работи под повърхността и често знае, преди да можете да го обясните. Имате нужда от тишина, за да чуете какво всъщност мислите."
        ),

        // ── Venus: what you want to be close to, and what you find beautiful ──
        "venus_1" to t(
            "You are met with warmth more often than you notice, and it makes doors open earlier than they should. You want to be liked, and you usually are.",
            "Посрещат ви с топлина по-често, отколкото забелязвате, и това ви отваря врати по-рано, отколкото е редно. Искате да ви харесват — и обикновено става."
        ),
        "venus_2" to t(
            "You enjoy owning nice things and you are willing to pay for quality. Comfort is not shallow for you: it is the proof that the effort was worth it.",
            "Обичате да притежавате хубави неща и сте склонни да платите за качество. Комфортът не ви е плитък: той ви е доказателството, че усилието е било оправдано."
        ),
        "venus_3" to t(
            "You charm people with words and you smooth over difficulties by talking. Pleasant conversation is a real need for you, not a pastime.",
            "Печелите хората с думи и изглаждате трудното чрез говорене. Приятният разговор ви е истинска нужда, а не запълване на времето."
        ),
        "venus_4" to t(
            "You want a beautiful home and peace in it. Making the place pleasant is your way of showing love, and quarrels indoors cost you more than most.",
            "Искате красив дом и мир в него. Да направите мястото приятно, е вашият начин да покажете обич, а скандалите вкъщи ви струват повече, отколкото на другите."
        ),
        "venus_5" to t(
            "You fall for people easily and you show affection without hesitating. Romance and creating things are where you are most yourself.",
            "Влюбвате се лесно и показвате привързаност без да се колебаете. В романтиката и в творенето сте най-много себе си."
        ),
        "venus_6" to t(
            "You show love by doing things for people rather than by saying so. You need your work to be pleasant, or you stop enjoying it quickly.",
            "Показвате обич, като правите неща за хората, а не като го изричате. Имате нужда работата ви да е приятна, иначе бързо спирате да я обичате."
        ),
        "venus_7" to t(
            "Partnership matters to you more than almost anything, and you are good at it. The risk is agreeing too easily just to keep the peace.",
            "Партньорството ви е по-важно от почти всичко и ви се получава. Рискът е да се съгласявате прекалено лесно, само за да не се стига до конфликт."
        ),
        "venus_8" to t(
            "You do not love lightly: you want all of it or none. Money and closeness get tangled together for you, so the two are worth keeping honest.",
            "Не обичате леко: искате всичко или нищо. Парите и близостта ви се преплитат, затова си струва да ги държите чисти и двете."
        ),
        "venus_9" to t(
            "You are drawn to what comes from further away — other places, other languages, other ways of living. Sameness cools your interest fast.",
            "Тегли ви към онова, което идва отдалеч — други места, други езици, друг начин на живот. Еднообразието бързо ви охлажда."
        ),
        "venus_10" to t(
            "You gain by being liked in public, and you know how to present things well. Your name and your charm work together, which takes you further than effort alone.",
            "Печелите от това, че ви харесват пред хората, и умеете да представяте нещата добре. Името и обаянието ви работят заедно и ви носят по-далеч от самото усилие."
        ),
        "venus_11" to t(
            "Your friendships are close to love, and often start there. You are generous with your circle and expect the same warmth back.",
            "Приятелствата ви са близо до любовта и често започват точно оттам. Щедри сте към своя кръг и очаквате същата топлина обратно."
        ),
        "venus_12" to t(
            "You love quietly and often unspoken. Something in you would rather keep the feeling private than test it in daylight.",
            "Обичате тихо и често неизказано. Нещо във вас предпочита да запази чувството скрито, вместо да го изпита на светло."
        ),

        // ── Mars: how you go after what you want ──────────────────────────────
        "mars_1" to t(
            "You go straight at things and people can see it coming. Nobody has to guess whether you are annoyed.",
            "Тръгвате направо и хората го виждат отдалеч. Никой не се налага да предполага дали сте недоволни."
        ),
        "mars_2" to t(
            "You fight for what is yours: money, property, what you have earned. Losing ground materially makes you far angrier than being insulted.",
            "Борите се за своето: пари, имот, изработеното. Материалната загуба ви разгневява много повече от обидата."
        ),
        "mars_3" to t(
            "You argue well and you argue often. Your fights are in words, and you leave them behind faster than the other person does.",
            "Спорите добре и спорите често. Битките ви са с думи, а ги забравяте по-бързо от отсрещния."
        ),
        "mars_4" to t(
            "Your temper comes out at home, where you feel safe enough to show it. You will defend your family without a second thought.",
            "Нервът ви излиза вкъщи, там, където се чувствате достатъчно сигурни, за да го покажете. Ще защитите семейството си без да се замислите."
        ),
        "mars_5" to t(
            "You chase what you want openly, and the chase itself is half the pleasure. You take risks other people talk themselves out of.",
            "Гоните желаното открито и самото гонене ви е половината удоволствие. Поемате рискове, от които други се отказват с приказки."
        ),
        "mars_6" to t(
            "Your energy goes into work, and a lot of it. You are efficient when there is a task and irritable when there is not.",
            "Енергията ви отива в работата и то в големи количества. Ефективни сте, когато има задача, и раздразнителни, когато няма."
        ),
        "mars_7" to t(
            "Your drive shows up in relationships: strong attraction, and sharp friction. You need someone who can hold their ground with you.",
            "Устремът ви излиза във връзките: силно привличане и остро триене. Имате нужда от човек, който може да ви удържи."
        ),
        "mars_8" to t(
            "You go all in and you do not forget. Money that belongs to two people, and things left unsaid, are where your fights come from.",
            "Влизате докрай и не забравяте. Парите, които са на двама, и неизказаното са мястото, откъдето идват битките ви."
        ),
        "mars_9" to t(
            "You fight for what you believe and you say it plainly. Being told what to think produces the opposite of obedience in you.",
            "Борите се за онова, в което вярвате, и го казвате направо. Когато ви кажат какво да мислите, се получава обратното на послушание."
        ),
        "mars_10" to t(
            "You are ambitious and it shows. You are willing to work harder than the people around you, and you want it noticed.",
            "Амбициозни сте и това се вижда. Готови сте да работите повече от хората около вас — и искате да се забележи."
        ),
        "mars_11" to t(
            "You put your energy behind the group and get things moving in it. You also collide with friends who will not pull their weight.",
            "Влагате енергията си в групата и я задвижвате. Сблъсквате се и с приятели, които не си носят частта."
        ),
        "mars_12" to t(
            "Your anger goes inwards before it comes out, and often it does not come out at all. Working quietly and unwatched suits you better than a fight in the open.",
            "Гневът ви влиза навътре, преди да излезе, а често изобщо не излиза. Работата на тихо и без публика ви подхожда повече от открита битка."
        ),

        // ── Jupiter: where life opens up ──────────────────────────────────────
        "jupiter_1" to t(
            "People give you the benefit of the doubt, and chances turn up because of how you come across. The risk is taking on more than one person can carry.",
            "Хората ви дават предимството на съмнението, а възможностите идват заради това как излизате пред тях. Рискът е да поемете повече, отколкото един човек може да носи."
        ),
        "jupiter_2" to t(
            "Money finds its way to you, though it leaves as easily. You are generous by instinct and rarely poor for long.",
            "Парите ви намират, макар и да си отиват също толкова лесно. Щедри сте по инстинкт и рядко сте бедни за дълго."
        ),
        "jupiter_3" to t(
            "Your luck runs through conversations and contacts: someone says the right thing at the right time. Learning is easy for you and you enjoy explaining.",
            "Късметът ви минава през разговори и познанства: някой казва точното нещо в точния момент. Ученето ви е леко и обичате да обяснявате."
        ),
        "jupiter_4" to t(
            "Home is where your good fortune is stored — family, property, a place that grows over the years. You need room indoors, in the literal sense.",
            "Домът е мястото, където се събира късметът ви — семейство, имот, място, което расте през годините. Имате нужда от простор вкъщи, буквално."
        ),
        "jupiter_5" to t(
            "Children, creating and taking chances bring you more than caution ever has. You are lucky when you play and flat when you do not.",
            "Децата, творенето и рискът ви носят повече, отколкото предпазливостта някога е носила. Имате късмет, когато играете, и линеете, когато не."
        ),
        "jupiter_6" to t(
            "Your growth comes through work done properly, day after day. You are the one who makes a job bigger than it was described.",
            "Растежът ви идва през работа, свършена както трябва, ден след ден. Вие сте човекът, който прави работата по-голяма, отколкото е била описана."
        ),
        "jupiter_7" to t(
            "The people you tie yourself to bring you opportunity. Partnership works out well for you more often than not, and you are worth trusting in it.",
            "Хората, с които се обвързвате, ви носят възможности. Партньорството ви се получава по-често, отколкото не, и в него ви се доверяват заслужено."
        ),
        "jupiter_8" to t(
            "Other people's money works in your favour: support, inheritance, backing when you need it. You are also unafraid of subjects most people avoid.",
            "Чуждите пари работят във ваша полза: подкрепа, наследство, гръб, когато ви трябва. Освен това не се боите от теми, които повечето хора избягват."
        ),
        "jupiter_9" to t(
            "You are at your best abroad, in study, or wherever the frame is wider. Staying in one place too long makes you smaller than you are.",
            "В най-добрата си форма сте в чужбина, в ученето или там, където рамката е по-широка. Прекалено дългото стоене на едно място ви смалява."
        ),
        "jupiter_10" to t(
            "Your career grows on its own once you start, and people put you in charge sooner than you expect. Reputation is your real capital.",
            "Кариерата ви расте почти сама, щом започнете, а хората ви слагат отговорен по-рано, отколкото очаквате. Името ви е истинският ви капитал."
        ),
        "jupiter_11" to t(
            "Your circle carries you: friends open doors and causes give you scale. You are the person others are glad to have in the group.",
            "Кръгът ви ви носи: приятелите отварят врати, а каузите ви дават мащаб. Вие сте човекът, когото другите се радват да имат в групата."
        ),
        "jupiter_12" to t(
            "Your luck arrives quietly and often unseen — help you did not ask for, a door that was open all along. Faith works for you better than strategy.",
            "Късметът ви идва тихо и често незабелязано — помощ, за която не сте молили, врата, която е била отворена през цялото време. Вярата ви работи по-добре от стратегията."
        ),

        // ── Saturn: where you have to earn it ─────────────────────────────────
        "saturn_1" to t(
            "You were made to carry yourself carefully, and it shows: people take you seriously and rarely see you relax. Confidence is something you build rather than something you had.",
            "Устроени сте да се държите внимателно и това си личи: хората ви приемат насериозно и рядко ви виждат отпуснати. Увереността ви е нещо построено, а не даденост."
        ),
        "saturn_2" to t(
            "Money has to be worked for, and you know exactly what things cost. What you build this way tends to hold, unlike what comes easily.",
            "Парите ви идват с труд и знаете точно колко струват нещата. Изграденото по този начин обикновено се задържа, за разлика от лесно дошлото."
        ),
        "saturn_3" to t(
            "You speak carefully and dislike saying what you have not checked. Learning came harder than for others, and stayed longer because of it.",
            "Говорите внимателно и не ви се казва онова, което не сте проверили. Ученето ви е било по-трудно от това на другите — и точно затова се е задържало."
        ),
        "saturn_4" to t(
            "Home carried weight early: responsibility, or a parent who was hard work. You build your own house deliberately, because you know what a shaky one costs.",
            "Домът ви е тежал отрано: отговорност или родител, който е бил трудна работа. Своя дом строите съзнателно, защото знаете колко струва нестабилният."
        ),
        "saturn_5" to t(
            "Play does not come naturally to you, and neither does letting go. Once you do commit to creating something, you finish it — which most people do not.",
            "Играта не ви идва естествено, нито пускането на нещата. Но щом веднъж се хванете да създадете нещо, го довършвате — което повечето хора не правят."
        ),
        "saturn_6" to t(
            "You work hard and you rest badly. Your health responds directly to your routine, which makes the dull daily habits more important for you than for anyone else.",
            "Работите много и почивате зле. Здравето ви реагира директно на режима, което прави скучните ежедневни навици по-важни за вас, отколкото за когото и да е друг."
        ),
        "saturn_7" to t(
            "Commitment is serious business for you, and you either avoid it or you honour it fully. The relationships that last are the ones you had to work at.",
            "Обвързването ви е сериозна работа и или го избягвате, или го носите докрай. Връзките, които остават, са тези, за които ви се е наложило да се потрудите."
        ),
        "saturn_8" to t(
            "Shared money, debts and endings are where life has made you grow up. You handle other people's crises better than most, because you do not look away.",
            "Общите пари, дълговете и краищата са мястото, където животът ви е накарал да пораснете. Справяте се с чуждите кризи по-добре от повечето, защото не извръщате поглед."
        ),
        "saturn_9" to t(
            "You do not take a belief on somebody's word — you test it first. What you end up believing, you can defend, and you teach it well.",
            "Не приемате вяра по чужда дума — първо я проверявате. Онова, в което накрая вярвате, можете да го защитите и го преподавате добре."
        ),
        "saturn_10" to t(
            "You are built for the long climb, and recognition arrives later than for your peers but stays. Authority suits you once you stop apologising for wanting it.",
            "Устроени сте за дълго изкачване и признанието ви идва по-късно от това на връстниците, но остава. Властта ви подхожда, щом спрете да се извинявате, че я искате."
        ),
        "saturn_11" to t(
            "You have few friends and they are the real thing. Groups tire you, but the people you keep, you keep for decades.",
            "Приятелите ви са малко и са истински. Групите ви изморяват, но хората, които запазвате, ги запазвате с десетилетия."
        ),
        "saturn_12" to t(
            "You carry something you do not talk about, and you carry it alone longer than you should. Solitude is both your relief and the thing that costs you.",
            "Носите нещо, за което не говорите, и го носите сами по-дълго, отколкото е нужно. Самотата ви е едновременно облекчение и онова, което ви струва скъпо."
        ),

        // ── Uranus: what refuses to stay the same ─────────────────────────────
        "uranus_1" to t(
            "You are hard to place and people notice it within a minute. You will not be told how to be, and you would rather be odd than convenient.",
            "Трудни сте за категоризиране и хората го усещат в първата минута. Няма да ви кажат как да бъдете, и предпочитате да сте странни, отколкото удобни."
        ),
        "uranus_2" to t(
            "Your money comes and goes in jumps rather than in a steady line. You are willing to earn in ways other people consider unreliable.",
            "Парите ви идват и си отиват на скокове, а не по равна линия. Готови сте да печелите по начини, които други смятат за несигурни."
        ),
        "uranus_3" to t(
            "You think faster than you can explain, and your conclusions arrive whole. Routine conversation bores you within minutes.",
            "Мислите по-бързо, отколкото можете да обясните, и заключенията ви идват наведнъж. Рутинният разговор ви отегчава в рамките на минути."
        ),
        "uranus_4" to t(
            "Home was never settled — moves, changes, a family that did things its own way. You need a place you can rearrange whenever you like.",
            "Домът ви никога не е бил установен — местения, промени, семейство, което е правило нещата по свой начин. Имате нужда от място, което можете да преподреждате когато си поискате."
        ),
        "uranus_5" to t(
            "What you create does not look like what came before, and that is the point. Your romances start suddenly and rarely follow the usual script.",
            "Онова, което създавате, не изглежда като предишното — и точно това е смисълът. Романтиките ви започват внезапно и рядко следват обичайния сценарий."
        ),
        "uranus_6" to t(
            "A fixed routine wears you down faster than hard work does. You need freedom in how the job gets done, even when the job itself is ordinary.",
            "Твърдият режим ви изтощава по-бързо от тежката работа. Имате нужда от свобода в начина, по който се върши работата, дори когато самата работа е обикновена."
        ),
        "uranus_7" to t(
            "You need space inside a relationship and you leave when you do not get it. The partnerships that work for you are the unconventional ones.",
            "Имате нужда от простор вътре във връзката и си тръгвате, когато го няма. Партньорствата, които ви се получават, са нестандартните."
        ),
        "uranus_8" to t(
            "Change arrives in your life through money and through other people, and it arrives suddenly. You recover from upheaval faster than most.",
            "Промяната идва в живота ви през парите и през другите хора, и идва внезапно. Възстановявате се от сривове по-бързо от повечето."
        ),
        "uranus_9" to t(
            "Your beliefs are your own and you changed them at least once completely. You are drawn to whatever the established view has not accounted for.",
            "Убежденията ви са ваши и поне веднъж сте ги сменили изцяло. Тегли ви към онова, което утвърденото мнение не е взело предвид."
        ),
        "uranus_10" to t(
            "Your career does not follow a straight line and you would not want it to. You are useful exactly where the old way has stopped working.",
            "Кариерата ви не следва права линия и вие не бихте искали да следва. Полезни сте точно там, където старият начин е спрял да работи."
        ),
        "uranus_11" to t(
            "Your friends are an unlikely mixture and that suits you. You are drawn to groups that want to change something rather than maintain it.",
            "Приятелите ви са невероятна смесица и точно това ви подхожда. Тегли ви към групи, които искат да променят нещо, а не да го поддържат."
        ),
        "uranus_12" to t(
            "Your independence lives out of sight, and you break your own patterns privately before anyone sees. Something in you does not belong to your surroundings at all.",
            "Независимостта ви живее извън погледа и чупите собствените си шаблони насаме, преди някой да е видял. Нещо във вас изобщо не принадлежи на средата ви."
        ),

        // ── Neptune: where the edges blur ─────────────────────────────────────
        "neptune_1" to t(
            "People see in you what they want to see, and you let them. You take on the mood of the room without deciding to.",
            "Хората виждат у вас онова, което им се иска, а вие им позволявате. Поемате настроението на стаята, без да сте решили."
        ),
        "neptune_2" to t(
            "Money is vague for you: it arrives and disappears without a clear account of how. Being precise about it is a skill you have to learn deliberately.",
            "Парите са ви размити: идват и изчезват без ясна сметка как. Точността с тях е умение, което трябва да усвоите съзнателно."
        ),
        "neptune_3" to t(
            "You hear what is not being said, and you speak in images rather than in exact words. Facts are less real to you than atmosphere.",
            "Чувате неизреченото и говорите с образи, а не с точни думи. Фактите ви са по-малко реални от атмосферата."
        ),
        "neptune_4" to t(
            "There is something unclear in your family story — an absence, a secret, a version that does not add up. Home is both your longing and your uncertainty.",
            "В семейната ви история има нещо неясно — отсъствие, тайна, версия, която не се връзва. Домът ви е едновременно тъга и несигурност."
        ),
        "neptune_5" to t(
            "You create from somewhere you cannot fully explain, and you fall in love with the picture before the person. The gift is real; so is the disappointment.",
            "Създавате от място, което не можете напълно да обясните, и се влюбвате в представата, преди в човека. Дарбата е истинска — истинско е и разочарованието."
        ),
        "neptune_6" to t(
            "Your body reports your state of mind before you do. Work that means nothing to you makes you ill faster than work that is simply hard.",
            "Тялото ви докладва състоянието ви, преди вие да сте го осъзнали. Работа, която не значи нищо за вас, ви разболява по-бързо от работа, която просто е тежка."
        ),
        "neptune_7" to t(
            "You see the best in a partner and sometimes only that. When you learn to look at who is actually there, your capacity for devotion becomes a strength.",
            "Виждате най-доброто в партньора, а понякога само него. Щом се научите да гледате кой стои реално отпред, способността ви за преданост става сила."
        ),
        "neptune_8" to t(
            "You give more than you can account for, and you do not ask enough questions about shared money. Your instincts about people, however, are unusually accurate.",
            "Давате повече, отколкото можете да отчетете, и не задавате достатъчно въпроси за общите пари. Но усетът ви за хора е необичайно точен."
        ),
        "neptune_9" to t(
            "You need something to believe in more than you need it to be provable. Faith carries you where argument would not.",
            "Имате нужда от нещо, в което да вярвате, повече, отколкото да е доказуемо. Вярата ви носи там, където доводът не би стигнал."
        ),
        "neptune_10" to t(
            "Your public direction is not a straight line and forcing one rarely works. People see an image of you that is not quite what you are.",
            "Посоката ви пред света не е права линия и насилването ѝ рядко помага. Хората виждат ваш образ, който не е съвсем това, което сте."
        ),
        "neptune_11" to t(
            "You are drawn to a cause bigger than yourself and you give it more than it returns. The right circle lifts you; the wrong one drains you quietly.",
            "Тегли ви към кауза, по-голяма от вас, и ѝ давате повече, отколкото ви връща. Правилният кръг ви повдига, грешният ви източва тихо."
        ),
        "neptune_12" to t(
            "You are at home in what cannot be seen: solitude, sleep, imagination, prayer. Boundaries are the whole task of your life, not a detail of it.",
            "У дома сте в онова, което не се вижда: самота, сън, въображение, молитва. Границите са цялата задача на живота ви, а не подробност от него."
        ),

        // ── Pluto: what goes deep and does not let go ─────────────────────────
        "pluto_1" to t(
            "Your presence is felt whether you speak or not, and people rarely stay neutral about you. You have been remade at least once and it left a mark.",
            "Присъствието ви се усеща независимо дали говорите, и хората рядко остават неутрални към вас. Преизградени сте поне веднъж и това е оставило следа."
        ),
        "pluto_2" to t(
            "Money is about power for you, not comfort. You have known having and not having, and both changed you.",
            "Парите за вас са въпрос на власт, не на удобство. Познавате и имането, и неимането — и двете са ви променили."
        ),
        "pluto_3" to t(
            "Your words carry more force than you intend, and you rarely forget a conversation. When you decide to know something, you find out everything.",
            "Думите ви носят повече сила, отколкото влагате, и рядко забравяте разговор. Щом решите да знаете нещо, разбирате всичко."
        ),
        "pluto_4" to t(
            "Something in your family had power over you and it took years to get out from under it. What you build at home, you build on purpose.",
            "Нещо в семейството ви е имало власт над вас и е отнело години да излезете изпод нея. Онова, което изграждате у дома, го изграждате нарочно."
        ),
        "pluto_5" to t(
            "You love intensely and create the same way — nothing casual, nothing halfway. Children and passion change your life more thoroughly than your plans do.",
            "Обичате интензивно и създавате по същия начин — нищо между другото, нищо наполовина. Децата и страстта променят живота ви по-основно от плановете ви."
        ),
        "pluto_6" to t(
            "You work to the point where it becomes control, and your body pays the bill. What you do daily either rebuilds you or slowly wears you out.",
            "Работите до степен, в която работата става контрол, а тялото ви плаща сметката. Онова, което правите ежедневно, или ви преизгражда, или ви износва бавно."
        ),
        "pluto_7" to t(
            "Your relationships are the place where power gets negotiated, sometimes without a word. The ones that last are the ones where neither of you is trying to win.",
            "Връзките ви са мястото, където се договаря властта, понякога без нито една дума. Остават тези, в които никой от двамата не се опитва да победи."
        ),
        "pluto_8" to t(
            "You are at home in what other people avoid: loss, money that belongs to two, the things nobody says. You survive what would flatten someone else.",
            "У дома сте в онова, което другите избягват: загуба, пари на двама, нещата, които никой не изрича. Преживявате онова, което би сломило някой друг."
        ),
        "pluto_9" to t(
            "You do not hold a belief lightly, and when one collapses you rebuild the whole structure. What you know, you know from having gone through it.",
            "Не държите вяра между другото, а когато една рухне, преизграждате цялата постройка. Онова, което знаете, го знаете, защото сте минали през него."
        ),
        "pluto_10" to t(
            "You are drawn to positions with real weight, and you handle them better than most. Your career has at least one complete change of direction in it.",
            "Тегли ви към позиции с истинска тежест и се справяте с тях по-добре от повечето. В кариерата ви има поне една пълна смяна на посоката."
        ),
        "pluto_11" to t(
            "You end up influential in groups without setting out to be. Friendships either go deep or they end.",
            "Оказвате се влиятелни в групи, без да сте се стремили. Приятелствата ви или влизат дълбоко, или свършват."
        ),
        "pluto_12" to t(
            "What drives you hardest is the part you see least. Left unexamined it runs you; looked at squarely it becomes the source of your strength.",
            "Онова, което ви движи най-силно, е частта, която виждате най-малко. Оставена непрегледана, тя ви управлява; погледната честно, става източникът на силата ви."
        ),

        // ── Chiron: the old wound, and where you can help ─────────────────────
        "chiron_1" to t(
            "You never quite settled the question of how you come across, and you feel it in the first minute with new people. That same sensitivity makes you read others accurately.",
            "Въпросът как излизате пред хората така и не се е затворил и го усещате в първата минута с нови хора. Същата чувствителност ви кара да четете другите точно."
        ),
        "chiron_2" to t(
            "Your sense of your own worth got knocked early, often around money. You are the person who can tell someone else, credibly, that they are worth more than they think.",
            "Усещането ви за собствена стойност е било разклатено отрано, често около пари. Вие сте човекът, който може убедително да каже на друг, че струва повече, отколкото си мисли."
        ),
        "chiron_3" to t(
            "Something about speaking or being heard hurt early — a stammer, a sibling, a class that laughed. You now explain things to people better than those who never struggled.",
            "Нещо около говоренето или изслушването е наранило рано — заекване, брат или сестра, клас, който се е смял. Сега обяснявате на хората по-добре от онези, които никога не са се затруднявали."
        ),
        "chiron_4" to t(
            "Home is where the old injury lives, and you have spent years making a better one. You know exactly what a family needs, because you noticed what yours lacked.",
            "Домът е мястото, където живее старата рана, и сте прекарали години в правене на по-добър. Знаете точно от какво има нужда едно семейство, защото сте забелязали какво е липсвало на вашето."
        ),
        "chiron_5" to t(
            "You were told, somewhere along the way, that what you made was not good enough. Creating anyway is the whole medicine, and you are unusually good with children because of it.",
            "Някъде по пътя са ви казали, че направеното от вас не е достатъчно добро. Да създавате въпреки това е цялото лекарство — и точно затова сте необичайно добри с деца."
        ),
        "chiron_6" to t(
            "Your body or your daily work has been the place you learned patience the hard way. You help other people with theirs without needing it explained.",
            "Тялото или ежедневната работа са мястото, където сте научили търпението по трудния начин. Помагате на другите с тяхното, без да им се налага да обясняват."
        ),
        "chiron_7" to t(
            "Being close to someone has cost you, and you carry a caution about it. You are the one friends come to when their relationship is falling apart.",
            "Близостта с някого ви е струвала и носите предпазливост към нея. Вие сте човекът, при когото приятелите идват, когато връзката им се разпада."
        ),
        "chiron_8" to t(
            "Loss, or someone else's power over you, marked you early. You are the person who can sit with someone in the worst of it without flinching.",
            "Загуба или чужда власт над вас са ви белязали отрано. Вие сте човекът, който може да седи с някого в най-лошото, без да трепне."
        ),
        "chiron_9" to t(
            "A belief you were given turned out to be false, and rebuilding it took years. What you teach now, you teach without pretending to certainty you do not have.",
            "Вяра, която са ви дали, се е оказала невярна, а преизграждането ѝ е отнело години. Онова, което преподавате сега, го преподавате без да се преструвате на сигурни."
        ),
        "chiron_10" to t(
            "Recognition, or the lack of it, is the sore spot: too much was expected, or nothing was. You are careful with other people's ambition because you know how it can bruise.",
            "Признанието или липсата му е болното място: искали са прекалено много или нищо. Внимателни сте с амбицията на другите, защото знаете как може да натърти."
        ),
        "chiron_11" to t(
            "You were the one left out of the group at some point, and it stayed with you. Now you notice immediately who is standing on the edge of the room.",
            "Някога вие сте били изключеният от групата и това е останало с вас. Сега забелязвате веднага кой стои в края на стаята."
        ),
        "chiron_12" to t(
            "You carry something old that you have never fully put into words. Helping others with exactly that is what turns it from a weight into a gift.",
            "Носите нещо старо, което никога не сте изрекли докрай. Помагането на другите точно с него го превръща от тежест в дарба."
        ),

        // ── Rahu (North Node): the unfamiliar direction you are pulled towards ─
        "rahu_1" to t(
            "Your growth is in stepping forward as yourself instead of waiting to be invited. It feels immodest and it is exactly the right move.",
            "Растежът ви е в това да излезете напред като себе си, вместо да чакате покана. Усеща се нескромно и е точно правилният ход."
        ),
        "rahu_2" to t(
            "You are meant to build something of your own — money, skill, a base that is yours. Relying on other people's resources keeps you where you were.",
            "Предназначени сте да построите нещо свое — пари, умение, база, която е ваша. Разчитането на чуждите ресурси ви държи там, където сте били."
        ),
        "rahu_3" to t(
            "Your way forward is through asking, learning and talking to people nearby. The big philosophy can wait; the phone call cannot.",
            "Пътят ви напред минава през питане, учене и говорене с хората наоколо. Голямата философия може да чака, телефонното обаждане — не."
        ),
        "rahu_4" to t(
            "You are being pulled towards putting down roots and tending what is private. The career will hold while you build a place to come back to.",
            "Тегли ви към това да пуснете корени и да се погрижите за личното. Кариерата ще изчака, докато си построите място, в което да се връщате."
        ),
        "rahu_5" to t(
            "Your growth is in doing what you actually enjoy and putting your name on it. Waiting for the group to approve is the old habit.",
            "Растежът ви е в това да правите онова, което наистина ви е приятно, и да се подпишете под него. Чакането групата да одобри е старият навик."
        ),
        "rahu_6" to t(
            "You get where you are going through routine, service and unglamorous work. The vision was never the problem; the daily follow-through is.",
            "Стигате там, където отивате, през режим, служене и работа без блясък. Визията никога не е била проблемът — довеждането ѝ докрай всеки ден е."
        ),
        "rahu_7" to t(
            "Your development runs through other people: partnership, compromise, letting someone matter. Doing it all yourself is the comfortable mistake.",
            "Развитието ви минава през другите: партньорство, компромис, това да позволите някой да е важен. Да го правите всичко сами е удобната грешка."
        ),
        "rahu_8" to t(
            "You are pulled towards depth, shared resources and things that cannot be controlled. Holding on to your own comfort is what keeps you stuck.",
            "Тегли ви към дълбочина, общи ресурси и неща, които не се контролират. Държенето за собственото удобство е онова, което ви държи на място."
        ),
        "rahu_9" to t(
            "Your road is out and away: study, travel, a bigger frame than the one you were handed. The familiar detail is not where your answer is.",
            "Пътят ви е навън и надалеч: учене, пътуване, по-широка рамка от подадената ви. Отговорът ви не е в познатата подробност."
        ),
        "rahu_10" to t(
            "You are meant to stand where you can be seen and be responsible for it. Staying comfortably in the background is the pattern to break.",
            "Предназначени сте да застанете там, където ви виждат, и да отговаряте за това. Оставането удобно на заден план е шаблонът за чупене."
        ),
        "rahu_11" to t(
            "Your growth comes through people who want the same thing you do, and through causes larger than your own name. Personal spotlight is the old comfort.",
            "Растежът ви идва през хора, които искат същото като вас, и през каузи, по-големи от собственото ви име. Личният прожектор е старото удобство."
        ),
        "rahu_12" to t(
            "You are being drawn towards letting go, quiet and things that cannot be measured. The instinct to organise everything is the thing to loosen.",
            "Тегли ви към пускане, тишина и неща, които не се измерват. Инстинктът да организирате всичко е онова, което трябва да отпуснете."
        ),

        // ── Lilith: what you were told to hide ────────────────────────────────
        "lilith_1" to t(
            "You were told, one way or another, to tone yourself down. Whenever you do, something goes flat; whenever you do not, people react strongly.",
            "По един или друг начин са ви казали да се смекчите. Когато го правите, нещо угасва; когато не го правите, хората реагират силно."
        ),
        "lilith_2" to t(
            "You refuse to be valued on someone else's terms, particularly around money. That refusal has cost you and also kept you honest.",
            "Отказвате да ви оценяват по чужди условия, особено около пари. Този отказ ви е струвал — и същевременно ви е държал честни."
        ),
        "lilith_3" to t(
            "You say the thing others leave unsaid, and it does not always go down well. Your directness is the most useful thing about you in a room full of polite talk.",
            "Казвате онова, което другите оставят неизречено, и не винаги се приема добре. Прямотата ви е най-полезното у вас в стая, пълна с учтиви приказки."
        ),
        "lilith_4" to t(
            "There is a role at home you were expected to play and would not. The break cost you something, and you would do it again.",
            "Вкъщи е имало роля, която са очаквали да играете, а вие не сте. Скъсването ви е струвало нещо — и бихте го направили пак."
        ),
        "lilith_5" to t(
            "What you want is not the tidy version, and pretending otherwise makes you resentful. Owned openly, it is the source of everything you create.",
            "Онова, което искате, не е подредената версия, а преструвката ви прави озлобени. Приемете ли го открито, то е източникът на всичко, което създавате."
        ),
        "lilith_6" to t(
            "You will not be a good soldier in a routine that disrespects you, and your body says so before you do. Work that allows you to be difficult is the work that keeps you well.",
            "Няма да сте добрият служител в режим, който не ви уважава, и тялото ви го казва преди вас. Работата, която ви позволява да сте неудобни, е работата, която ви държи здрави."
        ),
        "lilith_7" to t(
            "You do not soften yourself to keep a relationship, and you have lost some because of it. The ones that remain deal with you as you are.",
            "Не се смекчавате, за да задържите връзка, и сте загубили някои заради това. Останалите се справят с вас такива, каквито сте."
        ),
        "lilith_8" to t(
            "Power, sex and money are where you refuse to be managed. This is your strongest ground and the place where you have been most misjudged.",
            "Властта, сексът и парите са мястото, където отказвате да ви управляват. Това е най-силната ви територия и мястото, където най-често са ви преценявали грешно."
        ),
        "lilith_9" to t(
            "You will not take a belief because it is expected of you, and you say so out loud. That makes you unwelcome in some rooms and indispensable in others.",
            "Няма да приемете вяра само защото се очаква от вас, и го казвате на глас. Това ви прави нежелани в едни стаи и незаменими в други."
        ),
        "lilith_10" to t(
            "You do not play the game the way the profession wants it played. It has held you back in places and made your name in others.",
            "Не играете играта така, както професията иска да се играе. На места това ви е спъвало, на други ви е направило име."
        ),
        "lilith_11" to t(
            "You will not go along with the group to stay in it. That is uncomfortable and it is also why the group needs you.",
            "Няма да се съгласявате с групата само за да останете в нея. Това е неудобно — и е точно причината групата да има нужда от вас."
        ),
        "lilith_12" to t(
            "What you were told to hide, you hid — even from yourself. Bringing it into daylight is not a confession; it is where your force comes back.",
            "Онова, което са ви казали да криете, го скрихте — дори от себе си. Изваждането му на светло не е признание, а мястото, откъдето силата ви се връща."
        ),
    )

    fun of(planetKey: String, house: Int): Bilingual? = entries["${planetKey}_$house"]
}
