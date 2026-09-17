package eu.kastroguru.astrodiary.domain.humandesign

/**
 * Text for the four arrows — see HdVariables.kt for how each value is derived.
 *
 * Determination and Environment are written as **experiments to run**, not as prescriptions:
 * nothing here is medical advice, and the section header says so. Motivation and Perspective are
 * written as the thing that moves you and the thing you notice, each with the shape it takes when
 * you are not in your own design.
 */
object HdVariableTexts {

    /** Section intros — the "what is this" line above each arrow. */
    val intro: Map<String, Pair<String, String>> = mapOf(
        "determination" to (
            "How your body takes things in. This is the one part of the chart that is worth trying rather than believing: run it as an experiment for a few weeks and keep what changes something. It is not dietary or medical advice, and it replaces nothing a doctor tells you."
                to "Как тялото ви приема нещата. Това е частта от картата, която си струва да се пробва, вместо да се вярва: направете от нея експеримент за няколко седмици и запазете онова, което променя нещо. Не е диетичен или медицински съвет и не замества нищо, което лекар ви казва."),
        "environment" to (
            "The kind of space in which you work and rest well. Take it both literally — rooms, light, ceilings, where the door is — and loosely, as the shape of a situation. Of everything in Human Design this is the cheapest to test: change the room and see."
                to "Видът пространство, в което работите и почивате добре. Приемайте го и буквално — стаи, светлина, тавани, къде е вратата — и по-широко, като формата на една ситуация. От всичко в хюман дизайна това е най-евтиното за проверка: сменете стаята и вижте."),
        "motivation" to (
            "What actually sets you in motion, underneath the reasons you give. When you are living your own design it works quietly and you barely notice it; when you are not, it turns into its loud version and starts running your decisions."
                to "Онова, което наистина ви задвижва, под причините, които изтъквате. Когато живеете своя дизайн, работи тихо и почти не го забелязвате; когато не — превръща се в шумната си версия и започва да управлява решенията ви."),
        "perspective" to (
            "What you see first in any situation. It is not an opinion but a lens — the thing your attention lands on before you have thought about it — and it is the most useful thing you have to offer other people, provided you do not mistake it for the whole picture."
                to "Онова, което виждате първо във всяка ситуация. Не е мнение, а леща — нещото, върху което вниманието ви пада, преди да сте мислили — и е най-полезното, което имате да дадете на другите, стига да не го приемате за цялата картина.")
    )

    // ── Determination (12) ────────────────────────────────────────────────────
    val determination: Map<HdDetermination, Pair<String, String>> = mapOf(

        HdDetermination.APPETITE_CONSECUTIVE to (
            "The experiment: one thing at a time, in order. Try meals built from a single food rather than a plate with five things on it, and eat when the appetite actually shows up instead of when the clock says so. Mixed plates and grazing tend to leave you heavy and vaguely unwell without an obvious culprit. The same pattern usually holds for information — one subject until it is finished, then the next."
                to "Експериментът: едно нещо в даден момент, подред. Пробвайте хранене от една храна, вместо чиния с пет неща, и яжте, когато апетитът наистина се появи, а не когато часовникът каже. Смесените чинии и постоянното похапване обикновено ви оставят тежки и неопределено неразположени, без явен виновник. Същият модел обикновено важи и за информацията — една тема, докато приключи, после следващата."),

        HdDetermination.APPETITE_ALTERNATING to (
            "The experiment: alternate. Eat, stop before it is finished, come back to it later; or let one day be light and the next fuller. Your appetite is not meant to be steady, and forcing three equal meals a day onto it is what makes food feel like a chore. Try leaving something on the plate on purpose and see whether the afternoon goes better."
                to "Експериментът: редувайте. Яжте, спрете, преди да е свършено, върнете се по-късно; или нека един ден е лек, а следващият по-пълен. Апетитът ви не е предвиден да е постоянен и точно натрапването на три равни хранения на ден прави храненето задължение. Пробвайте да оставите нещо в чинията умишлено и вижте дали следобедът минава по-добре."),

        HdDetermination.TASTE_OPEN to (
            "The experiment: let taste choose, in the moment, and let it change. Deciding in advance what you will eat tends to go wrong for you — by the time the food is in front of you the appetite has moved. Shop and cook for options rather than for a plan, and treat a sudden strong preference as information rather than as a lack of discipline."
                to "Експериментът: оставете вкусът да избира, в момента, и оставете го да се променя. Да решавате предварително какво ще ядете обикновено се обръща срещу вас — докато храната стигне пред вас, апетитът се е преместил. Пазарувайте и гответе за възможности, а не за план, и приемайте внезапното силно предпочитание като информация, а не като липса на дисциплина."),

        HdDetermination.TASTE_CLOSED to (
            "The experiment: a small repertoire, well made. You are not built to be adventurous with food, and being talked into tasting everything at the table is a real cost rather than politeness. Find the handful of things that agree with you, prepare them the way you like, and repeat them without apologising for it. Novelty belongs in the rest of your life, not on the plate."
                to "Експериментът: малък репертоар, направен добре. Не сте направени да сте авантюристи с храната, и когато ви убеждават да опитате всичко на масата, това е реална цена, а не учтивост. Намерете няколкото неща, които ви понасят, приготвяйте ги както ги искате и ги повтаряйте, без да се извинявате. Новото място е в останалата част от живота ви, не в чинията."),

        HdDetermination.THIRST_NERVOUS to (
            "The experiment: drink with the food, and drink more often than you think you need to. This body seems to want liquid as part of taking anything in — soups, sauces, a glass at hand — and it gets restless and scattered when it goes dry. If you find yourself unable to settle in the afternoon, try water before you try a break."
                to "Експериментът: пийте с храната и пийте по-често, отколкото си мислите, че е нужно. Това тяло сякаш иска течност като част от приемането на всичко — супи, сосове, чаша под ръка — и става неспокойно и разпиляно, когато остане сухо. Ако не можете да се съберете следобед, пробвайте вода, преди да пробвате пауза."),

        HdDetermination.THIRST_CALM to (
            "The experiment: separate drinking from eating. Try water well before or well after a meal rather than during it, and notice whether the meal sits differently. Constant sipping through the day is likely to be a habit picked up from other people rather than a need of yours. Quiet and unhurried is the condition under which this body takes things in properly."
                to "Експериментът: разделете пиенето от храненето. Пробвайте вода доста преди или доста след хранене, а не по време на него, и забележете дали храната лежи различно. Постоянното сръбване през деня най-вероятно е навик, взет от други хора, а не ваша нужда. Тихо и без бързане е условието, при което това тяло приема нещата както трябва."),

        HdDetermination.TOUCH_COLD to (
            "The experiment: cooler than everyone else finds comfortable. Try raw and cold food, a cold rinse at the end of the shower, a cooler room to sleep in, fewer layers than the person next to you is wearing. Overheating makes this body dull and heavy, and the fix is usually temperature rather than rest. Take being the one who opens the window seriously."
                to "Експериментът: по-хладно, отколкото е удобно на всички останали. Пробвайте сурова и студена храна, студено изплакване в края на душа, по-хладна стая за сън, по-малко слоеве от човека до вас. Прегряването прави това тяло тъпо и тежко, а поправката обикновено е температура, не почивка. Приемете сериозно това, че сте човекът, който отваря прозореца."),

        HdDetermination.TOUCH_HOT to (
            "The experiment: warm, cooked, and warm again. Try soups and stews over salads, a hot drink with the meal, a warm room, and see whether things digest more easily and the mood steadies. Cold food and cold rooms tend to make this body contract and go quiet in a way that gets mistaken for a bad temper. Being the one who wants the heater on is not fussiness."
                to "Експериментът: топло, сготвено и после отново топло. Пробвайте супи и яхнии вместо салати, топла напитка с храненето, топла стая, и вижте дали нещата се смилат по-лесно и настроението се уравновесява. Студената храна и студените стаи карат това тяло да се свива и да замлъква по начин, който бъркат с лошо настроение. Това, че сте човекът, който иска отоплението, не е капризност."),

        HdDetermination.SOUND_HIGH to (
            "The experiment: eat and work where there is life going on. Sound seems to be part of how this body takes things in, so a busy kitchen, music, conversation at the table, a café instead of an empty flat. Total silence tends to make you uneasy rather than calm. If concentration keeps failing in a quiet room, try a noisier one before you try harder."
                to "Експериментът: яжте и работете там, където има живот наоколо. Звукът сякаш е част от начина, по който това тяло приема нещата — оживена кухня, музика, разговор на масата, кафене вместо празен апартамент. Пълната тишина по-скоро ви прави неспокойни, отколкото спокойни. Ако концентрацията все се разпада в тиха стая, пробвайте по-шумна, преди да се насилвате повече."),

        HdDetermination.SOUND_LOW to (
            "The experiment: quiet while you take anything in. Try eating without the television, without conversation, without your phone — and notice how much of what you called a stomach problem was a noise problem. The same goes for reading and for anything you need to absorb properly. Asking for silence at the table is a legitimate request, not rudeness."
                to "Експериментът: тишина, докато приемате каквото и да е. Пробвайте да ядете без телевизор, без разговор, без телефон — и забележете колко от онова, което сте наричали проблем със стомаха, е било проблем с шума. Същото важи за четенето и за всичко, което трябва да поемете както трябва. Да поискате тишина на масата е законна молба, не грубост."),

        HdDetermination.LIGHT_DIRECT to (
            "The experiment: bright, and preferably from above or from a window. Try eating and working in properly lit places, get outside early in the day, and treat dim rooms as something that quietly drains you rather than as cosiness. When you feel flat for no reason, check the light before you check the food."
                to "Експериментът: ярко, и по възможност отгоре или от прозорец. Пробвайте да се храните и да работите на добре осветени места, излизайте навън рано през деня и приемайте полутъмните стаи като нещо, което тихо ви източва, а не като уют. Когато сте отпаднали без причина, проверете светлината, преди да проверявате храната."),

        HdDetermination.LIGHT_INDIRECT to (
            "The experiment: light that arrives sideways rather than at you. Try lamps instead of ceiling lights, shade instead of full sun, a seat with the window beside you rather than in front of you. Harsh direct light seems to put this body on edge, and a lot of what passes for tiredness at the end of the day is that. Dimmer than everyone else prefers is likely to be right for you."
                to "Експериментът: светлина, която идва отстрани, а не в лицето. Пробвайте лампи вместо осветление от тавана, сянка вместо пълно слънце, място с прозорец до вас, а не пред вас. Резката пряка светлина сякаш държи това тяло напрегнато, и голяма част от онова, което минава за умора в края на деня, е точно това. По-затъмнено, отколкото предпочитат другите, вероятно е правилно за вас."))

    // ── Environment (12) ──────────────────────────────────────────────────────
    val environment: Map<HdEnvironment, Pair<String, String>> = mapOf(

        HdEnvironment.CAVES_SELECTIVE to (
            "Enclosed spaces with one way in that you control. A small room, a door you can shut, a corner with your back to the wall — and, in the wider sense, situations where you decide who gets in. Big open-plan spaces with people moving behind you will wear you down without your noticing. Choose the smaller office."
                to "Затворени пространства с един вход, който вие контролирате. Малка стая, врата, която можете да затворите, ъгъл с гърба към стената — и в по-широк смисъл ситуации, в които вие решавате кой влиза. Големите отворени пространства с хора, които минават зад вас, ще ви изтощават, без да забележите. Избирайте по-малкия кабинет."),

        HdEnvironment.CAVES_WET to (
            "Enclosed, and with some warmth and moisture in the air. Basements that are not damp but not dry either, rooms with plants, a bathroom you like, a kitchen with something on the stove. Dry, air-conditioned boxes are the wrong shell for you even when they are otherwise ideal. Being tucked in somewhere warm is a working condition, not indulgence."
                to "Затворено, и с известна топлина и влага във въздуха. Полуподземни стаи, които не са мокри, но не са и сухи, помещения с растения, баня, която харесвате, кухня с нещо на печката. Сухите климатизирани кутии са грешната черупка за вас, дори когато иначе са идеални. Да сте сгушени на топло е работно условие, не глезотия."),

        HdEnvironment.MARKETS_INTERNAL to (
            "In the middle of the traffic, not beside it. Crossroads, entrances, the room people pass through, the café at the busy corner — you work best inside the flow, where things and people keep arriving. Quiet suburbs and back rooms make you stale. If a job goes badly, look at whether you have been put somewhere nothing passes."
                to "В средата на движението, а не до него. Кръстовища, входове, стаята, през която хората минават, кафенето на оживения ъгъл — работите най-добре вътре в потока, където нещата и хората продължават да пристигат. Тихите квартали и задните помещения ви застояват. Ако работата не върви, проверете дали не са ви сложили там, където нищо не минава."),

        HdEnvironment.MARKETS_EXTERNAL to (
            "On the edge of the traffic, watching it. A table by the window of the busy place, an office above the street, the seat at the end of the row — near enough to see everything, far enough not to be in it. Being pushed into the middle of the crowd exhausts you; being cut off from it makes you restless. The edge is the working position."
                to "На ръба на движението, гледайки го. Маса до прозореца на оживеното място, кабинет над улицата, мястото в края на реда — достатъчно близо, за да виждате всичко, достатъчно далеч, за да не сте вътре. Да ви бутнат в средата на тълпата ви изтощава; да сте откъснати от нея ви прави неспокойни. Ръбът е работната позиция."),

        HdEnvironment.KITCHENS_WET to (
            "Places where something is being made and the making is visible and messy. A kitchen, a studio with the work spread out, a workshop mid-job, a lab. You come alive where a process is under way rather than where results are displayed. Tidy showrooms and finished spaces leave you with nothing to do."
                to "Места, където нещо се прави и правенето се вижда и е разхвърляно. Кухня, ателие с разстелена работа, работилница по средата на задача, лаборатория. Оживявате там, където има процес в движение, а не там, където се излагат резултати. Подредените витрини и завършените пространства ви оставят без работа."),

        HdEnvironment.KITCHENS_DRY to (
            "The same making, but clean and dry: a bench, a desk with tools laid out, a workshop where everything has a place. You transform things too, but you need the process contained rather than spilling. Damp, cluttered, improvised spaces disturb you more than they should. Give yourself the well-organised workroom and the output changes."
                to "Същото правене, но чисто и сухо: тезгях, бюро с подредени инструменти, работилница, в която всичко има място. И вие преобразувате нещата, но имате нужда процесът да е овладян, а не разлян. Влажните, натъпкани, импровизирани пространства ви смущават повече, отколкото би трябвало. Дайте си добре подредената работна стая и резултатът се променя."),

        HdEnvironment.MOUNTAINS_ACTIVE to (
            "Above things, and doing something from up there. High floors, a view, a raised desk, a place you had to climb to — and in the wider sense, positions with an overview from which you act. Being down in the pit with no line of sight is what makes work feel pointless to you. Ask for the room upstairs."
                to "Над нещата, и вършейки нещо оттам. Високи етажи, изглед, повдигнато бюро, място, до което е трябвало да се изкачите — и в по-широк смисъл позиции с обща гледка, от която действате. Да сте в ямата без видимост е онова, което прави работата безсмислена за вас. Поискайте стаята горе."),

        HdEnvironment.MOUNTAINS_PASSIVE to (
            "Above things, watching. The same height and view, but you are not meant to be running what you see — your value is the vantage point itself and what you notice from it. Being dragged down into the operation costs you the thing you are good for. Keep the high seat and keep the distance."
                to "Над нещата, наблюдавайки. Същата височина и изглед, но не ви е отредено да управлявате онова, което виждате — стойността ви е самата гледна точка и онова, което забелязвате от нея. Да ви завлекат надолу в работата ви отнема точно това, за което сте добри. Пазете високото място и пазете разстоянието."),

        HdEnvironment.VALLEYS_NARROW to (
            "Between two things, in the narrow passage. Corridors, doorways, the seat between two departments, the role that connects one side to another — and figuratively, any position where everything has to come through you. Wide open spaces scatter you; a defined channel focuses you. Look for the bottleneck and stand in it."
                to "Между две неща, в тесния проход. Коридори, входове, мястото между два отдела, ролята, която свързва едната страна с другата — и преносно, всяка позиция, през която всичко трябва да мине. Широко отворените пространства ви разпиляват; определеният канал ви фокусира. Търсете тясното място и застанете в него."),

        HdEnvironment.VALLEYS_WIDE to (
            "Still between things, but with room. A low, broad space with the hills visible on either side, a large room with clear edges, a role that sits between two worlds without being squeezed by either. Tight quarters make you claustrophobic and unproductive; a boundless space gives you nothing to orient by. Wide, with edges in sight."
                to "Пак между нещата, но с място. Ниско, широко пространство с хълмове, видими от двете страни, голяма стая с ясни граници, роля между два свята, без да сте притиснати от нито един. Тесните помещения ви правят клаустрофобични и непродуктивни; безкрайното пространство не ви дава по какво да се ориентирате. Широко, с видими краища."),

        HdEnvironment.SHORES_NATURAL to (
            "The line where one thing ends and another begins, made by nature rather than by people. Coasts, riverbanks, the edge of the woods, the last house before the fields. You settle at boundaries, and you do your best thinking while looking at one. Deep inside a city or deep inside the countryside are both wrong for you — the edge between them is right."
                to "Линията, където едно нещо свършва и друго започва, направена от природата, а не от хората. Брегове, речни корита, ръбът на гората, последната къща преди полето. Установявате се на границите и мислите най-добре, докато гледате някоя. И дълбоко в града, и дълбоко в провинцията са грешни за вас — ръбът между тях е правилният."),

        HdEnvironment.SHORES_ARTIFICIAL to (
            "The same edge, but built. A balcony, a terrace, a top-floor window, a bridge, the last row of buildings before something opens up. You need a made boundary to stand on and look out from — and in the wider sense, a clearly defined position at the border of two systems. A flat in the middle of a block with no outlook slowly flattens you."
                to "Същият ръб, но построен. Балкон, тераса, прозорец на последния етаж, мост, последният ред сгради, преди нещо да се отвори. Имате нужда от направена граница, на която да стоите и от която да гледате навън — и в по-широк смисъл, от ясно определена позиция на границата на две системи. Апартамент в средата на блок без изглед бавно ви сплесква."))

    // ── Motivation (6) ────────────────────────────────────────────────────────
    val motivation: Map<HdMotivation, Pair<String, String>> = mapOf(

        HdMotivation.FEAR to (
            "You are moved by what needs to be watched. This is not cowardice — it is an eye for what could go wrong, and it makes you the person who checks the exits, reads the contract, and asks the question nobody wanted asked. Off-course it turns into suspicion of everything and everyone, and you start protecting against things that were never coming. The difference is whether the fear is pointed at something specific or has become the weather you live in."
                to "Движи ви онова, което трябва да се наблюдава. Това не е малодушие — то е око за онова, което може да се обърка, и ви прави човекът, който проверява изходите, чете договора и задава въпроса, който никой не е искал зададен. Извън курса се превръща в подозрение към всичко и всички, и започвате да се пазите от неща, които никога не са идвали. Разликата е дали страхът е насочен към нещо конкретно, или се е превърнал във времето, в което живеете."),

        HdMotivation.HOPE to (
            "You are moved by what could still turn out well. You carry an expectation that things will open up, and other people borrow it from you — which is genuinely useful, because someone has to keep the door open. Off-course it becomes waiting: hoping instead of acting, and staying in situations long past the point where anything is going to change. Hope is a motor, not a plan."
                to "Движи ви онова, което още може да излезе добре. Носите очакване, че нещата ще се отворят, и другите го заемат от вас — което наистина е полезно, защото някой трябва да държи вратата отворена. Извън курса се превръща в чакане: надяване вместо действие и оставане в ситуации далеч след момента, в който нещо ще се промени. Надеждата е двигател, не план."),

        HdMotivation.DESIRE to (
            "You are moved by wanting things to become something else — yourself included. There is a reforming streak in you: you see what a person, a job or a place could be and you push towards it. Off-course that becomes managing other people's lives for them, and calling it help. Kept honest, it is the drive that actually changes things, provided the thing being changed asked for it."
                to "Движи ви желанието нещата да станат нещо друго — включително вие самите. В вас има преобразуваща жилка: виждате какъв може да стане един човек, една работа или едно място, и бутате натам. Извън курса това се превръща в управляване на живота на другите вместо тях и наричане на това помощ. Държано честно, е тласъкът, който наистина променя нещата — стига променяното да го е поискало."),

        HdMotivation.NEED to (
            "You are moved by what is needed now, by you or by whoever is in front of you. It makes you practical and quick to see the gap, and people rely on you for exactly that. Off-course the needs multiply until nothing is ever enough, and you find yourself acquiring, arranging and providing far beyond anything anyone asked for. The question worth asking is whose need it is and whether it is still a need."
                to "Движи ви онова, което е нужно сега — на вас или на човека пред вас. Това ви прави практични и бързи да видите липсата, и хората разчитат на вас точно за това. Извън курса нуждите се множат, докато нищо не стига, и се улавяте да набавяте, подреждате и осигурявате далеч над всичко, което някой е поискал. Въпросът, който си струва, е чия е нуждата и още ли е нужда."),

        HdMotivation.GUILT to (
            "You are moved by putting things right. You notice the imbalance in a situation — the person who was treated badly, the promise that was dropped — and you feel it as something to be corrected. Off-course it splits into carrying blame that is not yours and handing blame to others, both of which are exhausting and neither of which repairs anything. The clean version is repair without a verdict."
                to "Движи ви оправянето на нещата. Забелязвате несправедливото в една ситуация — човекът, с когото са се отнесли зле, обещанието, което е било изпуснато — и го усещате като нещо, което трябва да се поправи. Извън курса се разделя на носене на вина, която не е ваша, и раздаване на вина на другите; и двете изтощават и нито едно не поправя нищо. Чистата версия е поправка без присъда."),

        HdMotivation.INNOCENCE to (
            "You are moved by nothing in particular, and that is the point. You act without an agenda, meet things as they arrive, and are often the least strategic person in the room — which is exactly why people trust you. Off-course you start being clever: adding a reason, working out the angle, calculating what would be smart. It never suits you, and other people can feel the moment it starts."
                to "Не ви движи нищо конкретно, и точно там е смисълът. Действате без сметка, посрещате нещата, както идват, и често сте най-нестратегичният човек в стаята — точно затова хората ви вярват. Извън курса започвате да сте хитри: добавяте причина, изчислявате ъгъла, преценявате какво би било умно. Никога не ви отива, а другите усещат момента, в който започва."))

    // ── Perspective (6) ───────────────────────────────────────────────────────
    val perspective: Map<HdPerspective, Pair<String, String>> = mapOf(

        HdPerspective.SURVIVAL to (
            "You see what keeps a thing alive. In any plan your attention goes straight to whether it can last — the money, the energy, the weak point that will give first — while everyone else is still admiring the idea. That makes you unpopular early and right late. The trap is mistaking your own alarm for a universal verdict; not every risk is a threat."
                to "Виждате онова, което държи едно нещо живо. Във всеки план вниманието ви отива право към това дали може да издържи — парите, енергията, слабото място, което ще поддаде първо — докато всички останали още се възхищават на идеята. Това ви прави непопулярни рано и прави прави късно. Капанът е да приемете собствената си аларма за всеобща присъда; не всеки риск е заплаха."),

        HdPerspective.POSSIBILITY to (
            "You see what a thing could become. Where others see a room, a job or a person as they are, you see the version that does not exist yet, and describing it is one of the more useful things you do for people. The trap is preferring the possible version so much that you stop dealing with the actual one."
                to "Виждате в какво може да се превърне едно нещо. Там, където другите виждат стая, работа или човек такива, какви са, вие виждате версията, която още не съществува, и да я опишете е едно от по-полезните неща, които правите за хората. Капанът е да предпочитате възможната версия толкова много, че да спрете да се занимавате с действителната."),

        HdPerspective.POWER to (
            "You see where the power actually sits. Who decides, who only appears to decide, what the real currency is in this room — you read that before the conversation has finished, and you rarely mistake a title for authority. Used cleanly it makes you very hard to manipulate. The trap is reading everything as a power move, including the things that were just somebody being tired."
                to "Виждате къде наистина стои силата. Кой решава, кой само изглежда, че решава, каква е истинската валута в тази стая — прочитате го, преди разговорът да е свършил, и рядко бъркате титлата с власт. Използвано чисто, ви прави много трудни за манипулиране. Капанът е да четете всичко като ход за власт, включително онова, което е било просто уморен човек."),

        HdPerspective.WANTING to (
            "You see what people want. Not what they say — what they are actually reaching for, often before they know it themselves, which makes you good at anything that involves anticipating a person. The trap is arranging your own life around other people's wanting until you cannot say what you want yourself."
                to "Виждате какво искат хората. Не онова, което казват — а онова, към което наистина се протягат, често преди самите те да го знаят, което ви прави добри във всичко, свързано с предусещане на човек. Капанът е да подредите собствения си живот около чуждото искане, докато вече не можете да кажете какво искате вие."),

        HdPerspective.PROBABILITY to (
            "You see what is likely. Your attention sorts situations into what usually happens and what almost never does, and you are the person worth asking before a decision is made. The trap is that a probability is not a certainty and people hear it as one — including you, when the unlikely thing is the one you were hoping for."
                to "Виждате онова, което е вероятно. Вниманието ви сортира ситуациите на онова, което обикновено се случва, и онова, което почти никога — и сте човекът, когото си струва да попитат, преди да се вземе решение. Капанът е, че вероятността не е сигурност, а хората я чуват като такава — включително вие, когато малко вероятното е онова, на което сте се надявали."),

        HdPerspective.PERSONAL to (
            "You see through what has happened to you. Your own experience is the instrument, which makes what you say concrete and believable in a way that theory never is. The trap is the obvious one: your life is one sample. When you catch yourself starting a sentence with what happened to you, it is worth asking whether this person's situation is actually the same."
                to "Виждате през онова, което ви се е случило. Собственият ви опит е инструментът, което прави казаното от вас конкретно и достоверно по начин, по който теорията никога не е. Капанът е очевидният: животът ви е една извадка. Когато се хванете да започвате изречение с онова, което ви се е случило, си струва да попитате дали случаят на този човек наистина е същият."))
}
