package eu.kastroguru.astrodiary.domain.humandesign

/**
 * One text per gate, written for the case where the gate **hangs** — the person carries this half
 * of a circuit and the other half is missing (see [hangingGatesOf]).
 *
 * Each text says what you bring and what you are, mostly unconsciously, looking for in other
 * people. Keyed by gate only: a gate can hang in more than one channel, and the screen prints
 * which channel and which gate is being sought above the text.
 */
object HdHangingGateTexts {

    val seeking: Map<Int, Pair<String, String>> by lazy { first + second }

    private val first: Map<Int, Pair<String, String>> = mapOf(

        1 to ("You bring something that has to come out of you in its own form, and no committee can help you shape it. What you look for is someone who can hear it and say what it is — the ear that turns your expression into something other people can receive. Without that you either keep it to yourself or resent the audience you did find."
            to "Носите нещо, което трябва да излезе от вас в собствената си форма, и никаква комисия не може да ви помогне да го оформите. Търсите някой, който може да го чуе и да каже какво е — ухото, което превръща изразеното от вас в нещо, което другите могат да приемат. Без това или го задържате за себе си, или се сърдите на публиката, която сте намерили."),

        2 to ("You have a direction in you that has no engine attached to it — you know where things should go and cannot make them go there. You look for people who bring the drive, and you are unusually willing to let them do the moving as long as the direction stays yours. The mistake is handing over both at once."
            to "В вас има посока, към която не е закачен двигател — знаете накъде трябва да отидат нещата и не можете да ги накарате да отидат там. Търсите хора, които носят тласъка, и сте необичайно склонни да им оставите движението, докато посоката остава ваша. Грешката е да предадете и двете наведнъж."),

        3 to ("You carry the impulse to start something new out of a mess, and it arrives without a timetable. What you look for is someone who can hold the new thing steady long enough for it to become a form. Left alone, your beginnings pile up unfinished and you conclude you are inconsistent, which is not the problem."
            to "Носите импулса да започнете нещо ново от бъркотия и той идва без разписание. Търсите някой, който може да задържи новото достатъчно стабилно, за да стане форма. Оставени сами, започванията ви се трупат недовършени и заключавате, че сте непоследователни — а проблемът не е този."),

        4 to ("You produce answers, plenty of them, and none of them are proved by the fact that you thought of them. You look for someone who will ask you the real question and then check whether your answer held — that testing is what turns your mental output into something usable. Unasked, you answer everything and are believed for the wrong reasons."
            to "Произвеждате отговори, при това много, и нито един от тях не е доказан от факта, че сте се сетили за него. Търсите някой, който ще ви зададе истинския въпрос и после ще провери дали отговорът ви е издържал — тази проверка превръща умствената ви продукция в нещо използваемо. Непопитани, отговаряте на всичко и ви вярват по грешни причини."),

        5 to ("You need a rhythm and you keep it religiously — the same hour, the same route, the same order — and it is what makes you reliable. What you look for is someone who fits into that rhythm rather than admiring it from outside. Being pulled off your pattern costs you more than it would cost most people."
            to "Имате нужда от ритъм и го спазвате свято — същия час, същия път, същия ред — и точно това ви прави надеждни. Търсите някой, който се вписва в този ритъм, а не му се възхищава отвън. Да ви извадят от модела ви струва повече, отколкото би струвало на повечето хора."),

        6 to ("You have a boundary that decides who comes close and when, and it opens and shuts on its own schedule. You look for someone whose presence makes the opening worth it — intimacy is what completes this in you, and friction is what you get until then. Explaining the closed phases to people saves years of misreading."
            to "Имате граница, която решава кой се приближава и кога, и тя се отваря и затваря по свой график. Търсите някой, чието присъствие прави отварянето да си струва — близостта е онова, което това довършва във вас, а до тогава получавате търкане. Да обясните затворените фази на хората спестява години недоразумения."),

        7 to ("You can see who should be in front and what the plan behind them ought to be, and you are not built to be the one standing there. You look for the person who can carry it publicly while you steer from beside them. Pushed into the front row yourself, you get the position and lose the influence."
            to "Виждате кой трябва да е отпред и какъв трябва да е планът зад него, а не сте направени да сте човекът, който стои там. Търсите онзи, който може да го носи публично, докато вие насочвате отстрани. Бутнати сами на първия ред, получавате позицията и губите влиянието."),

        8 to ("You know what is worth putting in front of people and you are unable to make it yours — the contribution you are drawn to is somebody else's, and your gift is knowing it should be seen. You look for the individual whose expression you can put your weight behind. Trying to be the original one yourself never quite works."
            to "Знаете какво си струва да бъде показано на хората и не можете да го направите ваше — приносът, към който ви тегли, е чужд, а вашата дарба е да знаете, че трябва да се види. Търсите човека, чиято изява можете да подкрепите с тежестта си. Да сте самите вие оригиналният никога не се получава напълно."),

        9 to ("You can narrow down onto one detail and stay there far longer than anyone around you, and on its own it goes nowhere. You look for someone who supplies the thing worth concentrating on, or the pressure that makes you begin. Without it your focus lands on whatever is nearest, which is usually not what mattered."
            to "Можете да се свиете върху един детайл и да останете там много по-дълго от всеки около вас, а само по себе си това не води никъде. Търсите някой, който доставя онова, което си струва да е във фокуса, или натиска, който ви кара да започнете. Без това фокусът ви пада върху най-близкото, което обикновено не е било важното."),

        10 to ("You have a way of being yourself that you cannot easily justify and will not give up. What you look for is the person or the setting that lets that behaviour make sense out loud — expression, direction or instinct arriving from the other side. Around the wrong people the same self-love reads as stubbornness, including to you."
            to "Имате начин да бъдете себе си, който не можете лесно да оправдаете и няма да изоставите. Търсите човека или средата, които правят това поведение да звучи смислено на глас — изява, посока или инстинкт, идващи от другата страна. Сред грешните хора същата обич към себе си се чете като упорство, включително от вас."),

        11 to ("You are full of ideas and none of them are instructions — they are pictures that want telling, not doing. You look for someone who can take one and turn it into an experience or a story that moves. Acting on your own ideas because nobody came for them is the standard way this goes wrong."
            to "Пълни сте с идеи и нито една от тях не е инструкция — те са картини, които искат да бъдат разказани, не изпълнени. Търсите някой, който може да вземе една и да я превърне в преживяване или разказ, който движи. Да действате по своите идеи, защото никой не е дошъл за тях, е стандартният начин това да се обърка."),

        12 to ("You can say a thing beautifully, but only sometimes — the ability comes and goes with the mood, and forcing it produces something stilted. You look for whoever gives you the reason and the moment to speak. Waiting for that feels like caution to you and like moodiness to everyone else."
            to "Можете да кажете нещо красиво, но само понякога — способността идва и си отива с настроението, а насилването произвежда нещо изкуствено. Търсите онзи, който ви дава причината и момента да говорите. Това чакане на вас изглежда като предпазливост, а на всички останали — като променливост."),

        13 to ("People tell you things they have not told anyone, and you keep them. What you look for is the person who can do something with what you have gathered — turn the collected past into a direction or a lesson. Holding it all with nowhere to put it is the weight this gate is known for."
            to "Хората ви казват неща, които не са казвали на никого, и вие ги пазите. Търсите човека, който може да направи нещо с онова, което сте събрали — да превърне събраното минало в посока или в урок. Да го носите цялото, без къде да го сложите, е тежестта, с която този гейт е известен."),

        14 to ("You have resources and the capacity to work that go beyond what your plans require, and you are not the one who decides where they go. You look for someone with a direction worth funding with your energy. Given no direction, this power gets spent on whatever turned up."
            to "Имате ресурси и работоспособност над онова, което плановете ви изискват, и не сте вие онзи, който решава накъде да отидат. Търсите някой с посока, която си струва да бъде финансирана с енергията ви. Без дадена посока тази мощ се изразходва по онова, което се е случило да е наоколо."),

        15 to ("Your rhythm is extremes — you cannot keep a regular pattern and you should not try, because the range is the point and it is what makes you tolerant of other people's oddness. You look for someone who supplies the steady beat you plug into. Judged for inconsistency, you start apologising for the wrong thing."
            to "Вашият ритъм са крайностите — не можете да задържите равномерен модел и не бива да опитвате, защото диапазонът е смисълът и точно той ви прави търпеливи към чуждата странност. Търсите някой, който доставя постоянния такт, в който се включвате. Съдени за непоследователност, започвате да се извинявате за грешното нещо."),

        16 to ("You have enthusiasm for a skill and you jump in before you are ready, which is how you learn. You look for the depth that makes the leap land — someone who knows the craft well enough that your talent has somewhere to go. On your own, you get very good at starting and never quite arrive at mastery."
            to "Имате въодушевление за едно умение и се хвърляте, преди да сте готови — така се учите. Търсите дълбочината, която прави скока да се приземи — някой, който знае занаята достатъчно добре, за да има къде да отиде талантът ви. Сами, ставате много добри в започването и никога не стигате съвсем до майсторство."),

        17 to ("You have opinions and they arrive fully formed, which is not the same as being right. You look for the person who brings the facts and the pattern your opinion should have been built on. Unchecked, this gate produces a lot of confident statements about things nobody measured."
            to "Имате мнения и те идват напълно оформени, което не е същото като да сте прави. Търсите човека, който носи фактите и модела, върху които мнението ви е трябвало да бъде построено. Непроверен, този гейт произвежда много уверени твърдения за неща, които никой не е измерил."),

        18 to ("You see what is wrong with a thing before you see anything else, and the ability is genuinely valuable when someone asked. You look for whoever brings the standard to measure against — otherwise your correction has no reference and lands on people instead of on the work. Being the one who criticises unasked is the whole trap of this gate."
            to "Виждате какво е сбъркано в едно нещо, преди да видите каквото и да е друго, и способността наистина е ценна, когато някой е попитал. Търсите онзи, който носи мярката, спрямо която да се сравнява — иначе поправката ви няма отправна точка и пада върху хората вместо върху работата. Да сте онзи, който критикува непопитан, е целият капан на този гейт."),

        19 to ("You are extremely sensitive to what people need — food, contact, reassurance — and you feel it before it is said. You look for someone who can turn that sensitivity into a principle or a decision, because on its own it just makes you anxious about everyone. Left there, you provide endlessly and are still not sure you have done enough."
            to "Изключително чувствителни сте към онова, от което хората имат нужда — храна, контакт, успокоение — и го усещате, преди да е казано. Търсите някой, който може да превърне тази чувствителност в принцип или в решение, защото сама по себе си тя само ви прави тревожни за всички. Оставена така, вие осигурявате безкрайно и още не сте сигурни, че е било достатъчно."),

        20 to ("You live in the present tense and can say what is true right now, but the saying does not automatically turn into doing. You look for the energy, the knowing or the direction that gives your words something behind them. Without it you become the person who describes the moment perfectly and moves nothing."
            to "Живеете в настоящето и можете да кажете какво е вярно точно сега, но казването не се превръща автоматично в правене. Търсите енергията, знанието или посоката, които дават на думите ви нещо зад тях. Без това ставате човекът, който описва момента съвършено и не помества нищо."),

        21 to ("You need control over your own territory — your money, your time, your way of doing it — and you will fight for it further than is reasonable. What you look for is someone whose values make the control worth exercising for something other than itself. Uncoupled, this becomes bossiness aimed at whatever is nearest."
            to "Имате нужда от контрол над собствената си територия — парите, времето, начина си на работа — и ще се борите за нея по-далеч, отколкото е разумно. Търсите някой, чиито ценности правят контрола да си струва да бъде упражнен за нещо друго освен себе си. Незакачен, това се превръща в командване, насочено към най-близкото."),

        22 to ("You have a social grace that appears and disappears with your mood — open and charming in one phase, entirely unavailable in the next. You look for someone whose words give your openness a shape it can be shared in. Judged as inconsistent, you start performing the open version, which is the one thing that does not work."
            to "Имате социална лекота, която се появява и изчезва с настроението — отворени и очарователни в една фаза, напълно недостъпни в следващата. Търсите някой, чиито думи дават на отвореността ви форма, в която може да бъде споделена. Съдени за непостоянство, започвате да изпълнявате отворената версия — единственото нещо, което не работи."),

        23 to ("You know things in a form nobody else can follow, and when you say them at the wrong moment you are called strange. You look for someone whose framework lets your knowing be understood — the translation is what this gate waits for. Said into the wrong room, the same sentence gets you dismissed rather than heard."
            to "Знаете неща във форма, която никой друг не може да проследи, и когато ги казвате в грешния момент, ви наричат странни. Търсите някой, чиято рамка позволява знанието ви да бъде разбрано — преводът е онова, което този гейт чака. Казано в грешната стая, същото изречение ви носи отхвърляне, а не изслушване."),

        24 to ("The same thought returns to you again and again, and each time it is slightly clearer — this is not rumination, it is how the insight actually finishes. You look for whoever brings the fresh pressure or the question that makes the next pass worth it. Explaining that you need to go round again is worth doing early with anyone close to you."
            to "Една и същата мисъл ви се връща отново и отново и всеки път е малко по-ясна — това не е преживяне, а начинът, по който прозрението наистина се довършва. Търсите онзи, който носи новия натиск или въпроса, който прави следващата обиколка да си струва. Струва си рано да обясните на близките си, че имате нужда да минете отново."),

        25 to ("You have a way of loving that has nothing to do with the person in front of you — it is not personal, and that is its strength and the reason it wounds people. You look for whoever can give it a form: a shock, an initiation, something that makes the universal land somewhere specific. Unlanded, it reads as coldness from a very warm person."
            to "Имате начин да обичате, който няма нищо общо с човека пред вас — не е лично, и това е и силата му, и причината да наранява хората. Търсите онзи, който може да му даде форма: сътресение, посвещаване, нещо, което кара всеобщото да се приземи на конкретно място. Неприземено, звучи като студенина от много топъл човек."),

        26 to ("You can present a thing in the light that makes it work, and where the line runs between selling and lying is your lifelong question. You look for someone whose sense of what should be preserved keeps your telling honest. Without that check, this gate makes a very persuasive person nobody can quite trust."
            to "Можете да представите едно нещо в светлината, в която то работи, и къде минава границата между продаване и лъгане е вашият пожизнен въпрос. Търсите някой, чието усещане за онова, което трябва да се опази, държи разказа ви честен. Без тази проверка този гейт прави много убедителен човек, на когото никой не може напълно да вярва."),

        27 to ("You look after people, and you will do it past the point where it helps them or you. What you look for is someone with the values to say what is actually worth caring for — that is what stops the caring from becoming a job with no end. Uncoupled, you feed everyone and call the exhaustion love."
            to "Грижите се за хората и ще го правите отвъд точката, в която помага на тях или на вас. Търсите някой с ценностите да каже за какво наистина си струва да се полагат грижи — това спира грижата да стане работа без край. Незакачено, храните всички и наричате изтощението обич."),

        28 to ("You go looking for what a life is worth and you find it the hard way, in the struggle rather than in the answer. You look for whoever can tell you which struggles are the good ones — the discernment you do not have on your own. Left alone with this, you take on every difficulty available and call it depth."
            to "Отивате да търсите колко струва един живот и го намирате по трудния начин — в борбата, а не в отговора. Търсите онзи, който може да ви каже кои борби са добрите — различаването, което сами не притежавате. Оставени сами с това, поемате всяка налична трудност и я наричате дълбочина."),

        29 to ("You say yes with your whole body and you say it too often, because the saying itself feels right regardless of what you agreed to. You look for someone who brings the thing worth committing to. Without that, this gate produces a life of honoured commitments that led nowhere in particular."
            to "Казвате „да“ с цялото си тяло и го казвате прекалено често, защото самото казване се усеща правилно, независимо на какво сте се съгласили. Търсите някой, който носи онова, за което си струва да се обвържете. Без това този гейт произвежда живот от удържани ангажименти, които не са водили никъде конкретно."),

        30 to ("You feel desire at a temperature other people do not reach, and it fixes on things you did not choose to want. You look for whoever can put that intensity into a shape — a role, a project, an outlet. Unshaped, it burns through you and you call it passion until it becomes exhaustion."
            to "Усещате желание при температура, до която другите хора не стигат, и то се захваща за неща, които не сте избрали да искате. Търсите онзи, който може да сложи тази интензивност във форма — роля, проект, отдушник. Безформена, тя гори през вас и я наричате страст, докато не стане изтощение."),

        31 to ("You can lead when you are put there, and you cannot appoint yourself — the voice works only when the room has already turned to you. You look for the person or group whose direction and plan your voice is meant to carry. Speaking without that mandate is how a natural leader becomes the one nobody follows."
            to "Можете да водите, когато ви сложат там, и не можете да се назначите сами — гласът работи само когато стаята вече се е обърнала към вас. Търсите човека или групата, чиято посока и план гласът ви трябва да носи. Да говорите без този мандат е начинът един естествен водач да стане онзи, когото никой не следва."),

        32 to ("You can tell what will last and what will not, and the knowing arrives as fear rather than as analysis. You look for someone with the ambition to actually go after the thing you judged worth keeping. Alone with it, you become the person who sees clearly and never moves."
            to "Можете да кажете какво ще издържи и какво не, и знанието идва като страх, а не като анализ. Търсите някой с амбицията наистина да тръгне след онова, което сте отсъдили, че си струва да се пази. Сами с това, ставате човекът, който вижда ясно и никога не се помества.")
    )

    private val second: Map<Int, Pair<String, String>> = mapOf(

        33 to ("You need to withdraw, and what you bring back from the retreat is the story of what happened — you are the one who can tell it afterwards. You look for whoever gathers the material worth retelling. Without that, the privacy becomes hiding and there is nothing to come back with."
            to "Имате нужда да се оттегляте, а онова, което връщате от оттеглянето, е разказът за случилото се — вие сте онзи, който може да го изрече после. Търсите онзи, който събира материала, който си струва да бъде преразказан. Без това уединението става криене и няма с какво да се върнете."),

        34 to ("You have raw power that only works when it is busy — it is not for talking about and not for sharing, and it gets strange when it has nothing to do. You look for the expression, the instinct or the self that gives it a channel out. Idle, this is the gate that makes a capable person restless and hard to be around."
            to "Имате груба мощ, която работи само когато е заета — не е за говорене и не е за споделяне, и става странна, когато няма какво да прави. Търсите изявата, инстинкта или себе-то, които ѝ дават канал навън. Без работа, това е гейтът, който прави способния човек неспокоен и труден за понасяне."),

        35 to ("You have done a great many things and you are already looking at the next one, because the point was the experience and not the result. You look for someone whose feeling gives an experience its weight, so the collection stops being a list. Uncoupled, you arrive at the end with a lot of stories and the sense of having felt none of them."
            to "Правили сте огромно количество неща и вече гледате към следващото, защото смисълът е бил преживяването, а не резултатът. Търсите някой, чието чувство дава тежест на едно преживяване, за да спре колекцията да е списък. Незакачено, стигате до края с много истории и с усещането, че не сте изживели нито една."),

        36 to ("You go into things without knowing how, and the crisis that follows is the mechanism rather than the mistake. You look for whoever can turn the raw experience into progress — the change that makes the upheaval worth having gone through. On your own, the same crisis repeats with different faces."
            to "Влизате в нещата, без да знаете как, а кризата, която следва, е механизмът, а не грешката. Търсите онзи, който може да превърне суровото преживяване в напредък — промяната, която прави преобръщането да си струва. Сами, същата криза се повтаря с различни лица."),

        37 to ("You hold groups together with agreements and warmth, and you need the handshake to be honoured. You look for someone with the will to actually deliver their end — that is what makes a family or a team out of your good intentions. Without it, you keep your side of bargains nobody else remembers making."
            to "Държите групите заедно с договорки и топлина, и имате нужда ръкостискането да бъде удържано. Търсите някой с волята наистина да изпълни своята страна — това прави от добрите ви намерения семейство или екип. Без това удържате своята страна на сделки, които никой друг не помни да е правил."),

        38 to ("You will fight, and you need something worth fighting for or you will fight anything at all. You look for whoever can tell you which battles matter — the discernment that turns stubbornness into purpose. Unaimed, this gate makes a life of opposition with no cause at the centre of it."
            to "Ще се борите и имате нужда от нещо, за което си струва — иначе ще се борите с каквото и да е. Търсите онзи, който може да ви каже кои битки имат значение — различаването, което превръща упорството в цел. Ненасочен, този гейт прави живот в противопоставяне без причина в центъра."),

        39 to ("You provoke people without trying to, and what comes out of them when you do is usually the truth they were sitting on. You look for someone whose spirit can take the provocation and turn it into something — otherwise you are simply the one who upsets the room. Learning that the effect is real, and not imagined by others, changes how you use it."
            to "Провокирате хората, без да се опитвате, и онова, което излиза от тях, обикновено е истината, върху която са седели. Търсите някой, чийто дух може да поеме провокацията и да я превърне в нещо — иначе сте просто онзи, който разстройва стаята. Да разберете, че въздействието е истинско, а не измислено от другите, променя начина, по който го използвате."),

        40 to ("You work and then you need to be completely alone, and the aloneness is not a mood — it is the price of the work. You look for the person whose warmth and agreement make the effort worth making. Without that, you provide for everyone and slowly stop wanting to see any of them."
            to "Работите и после имате нужда да сте напълно сами, а самотата не е настроение — тя е цената на работата. Търсите човека, чиято топлина и договорка правят усилието да си струва. Без това осигурявате на всички и бавно спирате да искате да виждате някого от тях."),

        41 to ("Every experience you have not had yet starts as a fantasy in you, and the pressure to begin is constant and does not care whether anything is available. You look for whoever can recognise the feeling and give it a direction. Unrecognised, this becomes a life of imagined experiences and a growing sense that nothing starts."
            to "Всяко преживяване, което още не сте имали, започва в вас като фантазия, а натискът да започнете е постоянен и не се интересува дали има нещо налично. Търсите онзи, който може да разпознае чувството и да му даде посока. Неразпознато, това става живот от въображаеми преживявания и растящо усещане, че нищо не започва."),

        42 to ("You are the one who can finish a thing properly, all the way through the last unglamorous part, and you cannot easily start one. You look for whoever brings the beginning worth completing. Without it you get pulled into finishing other people's abandoned projects and wonder why your own list never moves."
            to "Вие сте онзи, който може да довърши нещо както трябва, докрай през последната непривлекателна част, и не можете лесно да започнете. Търсите онзи, който носи началото, което си струва да се довърши. Без това ви въвличат в довършване на чужди зарязани проекти и се чудите защо вашият списък не се движи."),

        43 to ("Something arrives in you already known, with no steps you can show, and when you say it out loud at the wrong moment you are called difficult. You look for the person whose language can carry it out — the voice that makes the leap explicable. Unheard often enough, this gate turns a brilliant mind into a silent one."
            to "Нещо пристига в вас вече знайно, без стъпки, които можете да покажете, и когато го изречете в грешния момент, ви наричат труден човек. Търсите човека, чийто език може да го изнесе навън — гласът, който прави скока обясним. Неизслушан достатъчно често, този гейт превръща един блестящ ум в мълчалив."),

        44 to ("You can smell what a person is going to do, because you recognise the pattern from before — this is memory working as instinct, and it is usually right about people. You look for someone who can present what you sense in a way that others will act on. Alone, you know who not to trust and cannot explain why to anyone."
            to "Можете да подушите какво ще направи един човек, защото разпознавате модела от преди — това е памет, която работи като инстинкт, и обикновено е права за хората. Търсите някой, който може да представи усетеното от вас така, че другите да действат по него. Сами, знаете на кого да не вярвате и не можете да обясните защо на никого."),

        45 to ("You have a natural claim over what belongs to your people and how it gets divided, and you are not shy about it. You look for whoever can take control of the resource itself, because you distribute rather than manage. Without that, you preside over something you do not actually hold."
            to "Имате естествено право над онова, което принадлежи на вашите хора, и над това как се разпределя, и не се притеснявате от него. Търсите онзи, който може да поеме контрола над самия ресурс, защото вие разпределяте, а не управлявате. Без това председателствате нещо, което всъщност не държите."),

        46 to ("You are in a body that gets lucky — turning up at the right place without planning it, and doing well at things because you were there. You look for whoever brings the commitment that makes the luck into something. Unpartnered, this gate gives a charmed life with nothing accumulating in it."
            to "Живеете в тяло, на което върви — озовавате се на правилното място, без да сте го планирали, и се справяте с неща, защото сте били там. Търсите онзи, който носи обвързването, което превръща късмета в нещо. Незакачен, този гейт дава щастлив живот, в който нищо не се натрупва."),

        47 to ("You go back over what happened until it makes a picture, and while the picture is unformed the pressure feels like being stuck. You look for the question or the framework that resolves it — the realisation you cannot force. Without it, you are the person who keeps chewing over the past and is told to move on."
            to "Връщате се върху случилото се, докато не се получи картина, а докато картината е неоформена, натискът се усеща като задънена улица. Търсите въпроса или рамката, които го разрешават — осъзнаването, което не можете да принудите. Без това сте човекът, който продължава да преживя миналото, и на когото казват да го подмине."),

        48 to ("You have depth that you cannot demonstrate on request, and the fear that goes with it is a fear of not being adequate when the moment comes. You look for whoever can give your depth an outlet — the skill, the enthusiasm, the platform. Kept in, this becomes a very capable person who never quite delivers."
            to "Имате дълбочина, която не можете да демонстрирате по заявка, а страхът, който върви с нея, е страх, че няма да сте достатъчни, когато дойде моментът. Търсите онзи, който може да даде на дълбочината ви отдушник — умението, въодушевлението, сцената. Задържана вътре, това става много способен човек, който никога не изнася съвсем."),

        49 to ("You know instantly what you will and will not accept, and you can end something on a principle without much warning. You look for someone whose sensitivity to what people need keeps the principle humane. Alone with it, this gate rejects first and understands later."
            to "Знаете мигновено какво ще приемете и какво не, и можете да прекъснете нещо по принцип без много предупреждение. Търсите някой, чиято чувствителност към нуждите на хората държи принципа човешки. Сами с това, този гейт първо отхвърля и после разбира."),

        50 to ("You carry the rules about what has to be looked after, and you feel responsible whether or not anyone appointed you. You look for whoever brings the practical caring that puts your values into action. Without it, you become the guardian of standards nobody is applying."
            to "Носите правилата за онова, за което трябва да се полага грижа, и се чувствате отговорни независимо дали някой ви е назначил. Търсите онзи, който носи практичната грижа, привеждаща ценностите ви в действие. Без това ставате пазител на стандарти, които никой не прилага."),

        51 to ("You go where other people will not, and you get shocked into a new level rather than growing into it gradually. You look for whoever can give the shock a meaning — the love or the purpose that makes the leap more than daring. Uncoupled, you are competitive about things that do not matter."
            to "Отивате там, където другите няма да отидат, и биват изтласквани в ново ниво чрез сътресение, вместо да израствате постепенно. Търсите онзи, който може да даде на сътресението смисъл — обичта или целта, които правят скока повече от смелост. Незакачен, ставате състезателни за неща, които нямат значение."),

        52 to ("You can sit still with enormous energy held in place, and that stillness is what makes concentration possible — for you and for the room. You look for whoever supplies the thing worth applying it to. Without a focus, the same energy becomes tension with nowhere to go."
            to "Можете да седите неподвижно с огромна енергия, задържана на място, и точно тази неподвижност прави съсредоточаването възможно — за вас и за стаята. Търсите онзи, който доставя онова, към което си струва да бъде приложена. Без фокус същата енергия става напрежение без изход."),

        53 to ("You start things — that is the whole of it, and you are not the one who sees them through. You look for whoever can carry the beginning to maturity, and you have to make peace with handing it over. Without that, your life is a long series of openings and the accusation that you never finish anything."
            to "Започвате неща — това е цялото, и не сте онзи, който ги довежда докрай. Търсите онзи, който може да доведе началото до зрялост, и трябва да се примирите с предаването му. Без това животът ви е дълга поредица от начала и обвинението, че никога нищо не довършвате."),

        54 to ("You want to rise, materially and otherwise, and the drive is relentless in a way you cannot switch off. You look for whoever can tell you what is actually worth climbing towards. Unaimed, the ambition attaches to whatever is in front of you and you arrive somewhere you did not want to be."
            to "Искате да се изкачите, материално и не само, и тласъкът е неумолим по начин, който не можете да изключите. Търсите онзи, който може да ви каже към какво наистина си струва да се изкачвате. Ненасочена, амбицията се захваща за онова, което е пред вас, и стигате някъде, където не сте искали да сте."),

        55 to ("Your mood decides how full the world looks, and it swings between abundance and emptiness for no reason you will ever locate. You look for whoever can provoke it into expression, or you sit in the swing alone. This is the gate where learning that the mood is weather, not truth, changes the most."
            to "Настроението ви решава колко пълен изглежда светът и се люлее между изобилие и пустота без причина, която ще откриете някога. Търсите онзи, който може да го провокира до изява — иначе седите в люлката сами. Това е гейтът, при който да разберете, че настроението е време, а не истина, променя най-много."),

        56 to ("You can tell it — the story that makes people feel they were there — and you need something worth telling. You look for whoever brings the ideas your telling can carry. Without them you tell it anyway, and hear yourself embellishing to keep the room."
            to "Можете да го разкажете — историята, която кара хората да усетят, че са били там — и имате нужда от нещо, което си струва да се разкаже. Търсите онзи, който носи идеите, които разказът ви може да понесе. Без тях разказвате въпреки това и се чувате как украсявате, за да задържите стаята."),

        57 to ("You hear things — in a voice, in a room, in the second before something happens — and the knowing is immediate and only offered once. You look for whoever can act on it, speak it, or hold it steady. Unheeded, this becomes the most acute intuition in the room being talked over by people who thought about it instead."
            to "Чувате неща — в един глас, в една стая, в секундата преди нещо да се случи — и знанието е мигновено и се дава само веднъж. Търсите онзи, който може да действа по него, да го изрече или да го задържи стабилно. Незачетено, това става най-острата интуиция в стаята, надговорена от хора, които вместо това са мислили."),

        58 to ("You are glad to be alive in a way that has no cause, and it comes out as the urge to make things better. You look for whoever brings the standard your energy should be improving against. Without it, the same vitality turns into criticising everything within reach."
            to "Радвате се, че сте живи, по начин, който няма причина, и то излиза като порив нещата да станат по-добри. Търсите онзи, който носи мярката, спрямо която енергията ви трябва да подобрява. Без нея същата жизненост се превръща в критикуване на всичко наоколо."),

        59 to ("You can get through anyone's defences — that is what this gate does, and it is not always about sex, though it is always about closeness. You look for the person whose boundary makes the intimacy mean something. Without that, you break through and find you did not want to be inside."
            to "Можете да минете през защитата на всеки — това прави този гейт, и не винаги става дума за секс, макар винаги да става дума за близост. Търсите човека, чиято граница прави близостта да значи нещо. Без това пробивате и откривате, че не сте искали да сте вътре."),

        60 to ("You live with a limit you cannot argue with, and the new thing only comes out of accepting it rather than pushing at it. You look for whoever can take the mutation you produce and order it into something. Unaccepted, the limit becomes the whole story and nothing new arrives."
            to "Живеете с ограничение, с което не можете да спорите, а новото излиза само от приемането му, а не от блъскането в него. Търсите онзи, който може да вземе мутацията, която произвеждате, и да я подреди в нещо. Неприето, ограничението става цялата история и нищо ново не пристига."),

        61 to ("You are under pressure to know why — not how, why — and the pressure does not lift because an answer arrived. You look for whoever can turn the pressure into a thought that finishes. Without that, this is the gate of lying awake with something you cannot even formulate."
            to "Под натиск сте да знаете защо — не как, а защо — и натискът не се вдига, защото е дошъл отговор. Търсите онзи, който може да превърне натиска в мисъл, която приключва. Без това това е гейтът на лежането без сън с нещо, което дори не можете да формулирате."),

        62 to ("You have the names and the details and the order of things, and you are the one who can make a complicated matter sayable. You look for whoever brings the opinion or the pattern worth organising. Without it, you produce impeccable detail about something nobody needed sorted."
            to "Имате имената, детайлите и реда на нещата, и сте онзи, който може да направи един сложен въпрос изразим. Търсите онзи, който носи мнението или модела, които си струва да бъдат подредени. Без това произвеждате безупречен детайл за нещо, което никой не е имал нужда да бъде подредено."),

        63 to ("You doubt, and the doubt is not a weakness — it is the question that asks whether this actually holds up. You look for whoever produces the formula or the answer your doubt is meant to test. Untested, the doubt turns inward and you spend it on yourself and the people close to you."
            to "Съмнявате се, и съмнението не е слабост — то е въпросът дали това наистина издържа. Търсите онзи, който произвежда формулата или отговора, които съмнението ви трябва да провери. Непроверено, съмнението се обръща навътре и го изразходвате върху себе си и близките си."),

        64 to ("Your head is full of images from things that already happened, out of order and unresolved, and the pressure is to make sense of them. You look for whoever can complete the picture — the realisation that turns the noise into a memory you can put down. Without it, you replay and call it thinking."
            to "Главата ви е пълна с образи от вече случили се неща, разбъркани и неразрешени, а натискът е да им намерите смисъл. Търсите онзи, който може да довърши картината — осъзнаването, което превръща шума в спомен, който можете да оставите. Без това превъртате и го наричате мислене.")
    )
}
