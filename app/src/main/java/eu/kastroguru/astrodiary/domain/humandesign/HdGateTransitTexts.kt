package eu.kastroguru.astrodiary.domain.humandesign

/**
 * The daily layer: what a gate puts in the air while a planet is crossing it, plus the three
 * things that gate can do to *your* chart (see [HdTransitEffect]).
 *
 * Kept to two sentences per gate on purpose — this is a list a person reads in the morning, not a
 * reading. The effect text above it is what makes it personal.
 */
object HdGateTransitTexts {

    val effect: Map<HdTransitEffect, Pair<String, String>> = mapOf(
        HdTransitEffect.COMPLETES to (
            "This one closes a circuit that is normally open in you: for as long as it lasts you can do something you usually cannot, and it tends to feel like relief or like unusual capability. Notice what you reach for while it is here — it is not permanent, and committing as though it were is the standard mistake."
                to "Този затваря верига, която обикновено е отворена в вас: докато е тук, можете нещо, което обичайно не можете, и това се усеща като облекчение или като необичайна способност. Забележете към какво посягате, докато е тук. Не е постоянно, а обичайната грешка е да се обвържете, сякаш ще остане."),
        HdTransitEffect.AMPLIFIES to (
            "You already carry this gate, so today it is simply louder. Familiar rather than new — the same thing you always do, with the volume up, which is worth knowing before you conclude that you are being unusually intense."
                to "Вече носите този гейт, така че днес той е просто по-силен. По-скоро познато, отколкото ново — същото, което правите винаги, със увеличена сила, което е добре да знаете, преди да заключите, че сте необичайно интензивни."),
        HdTransitEffect.OPEN to (
            "This lands where you have nothing of your own, so it arrives from outside — as other people's pressure, or as a mood you will probably mistake for yours. The useful move is to check, later, whether it left when the day did."
                to "Този пада там, където нямате нищо свое, затова идва отвън — като натиск от други хора или като настроение, което вероятно ще припишете на себе си. Полезното е да проверите после дали си е тръгнало заедно с деня.")
    )

    val gate: Map<Int, Pair<String, String>> by lazy { first + second }

    private val first: Map<Int, Pair<String, String>> = mapOf(
        1 to ("Something wants to come out in its own form today, and it will not survive being edited by committee. Make the thing, then decide who gets to see it."
            to "Днес нещо иска да излезе в собствената си форма и няма да преживее редактиране от комисия. Направете го, после решавайте кой го вижда."),
        2 to ("A sense of where things should be going, without any engine to get them there. Say the direction out loud; someone else supplies the movement."
            to "Усещане накъде трябва да отиват нещата, без двигател, който да ги закара. Изречете посоката на глас; движението идва от друг."),
        3 to ("New beginnings pushing out of a mess, at their own pace and not yours. If it will not start, it is not late — it is not ready."
            to "Нови начала, които се измъкват от бъркотия, със свое темпо, не с вашето. Ако не тръгва, не е закъсняло — не е готово."),
        4 to ("Answers arrive quickly and cheaply today, and none of them are proved by arriving. Hold them as guesses until something tests one."
            to "Днес отговорите идват бързо и лесно, и нито един не е доказан от това, че е дошъл. Дръжте ги като предположения, докато нещо не провери някой от тях."),
        5 to ("A day that rewards the routine and punishes the improvisation. Keep the hour, the order and the habit; the results are in the repetition."
            to "Ден, който възнаграждава рутината и наказва импровизацията. Пазете часа, реда и навика; резултатът е в повторението."),
        6 to ("Boundaries are in play — who gets close and who does not, and friction where that is unclear. Conflict today is usually about proximity, not about the topic."
            to "Границите са в игра — кой се приближава и кой не, и търкане там, където това е неясно. Днешният конфликт обикновено е за близостта, не за темата."),
        7 to ("Attention on who should be leading and what the plan behind them is. The useful seat is beside the front, not in it."
            to "Вниманието е върху това кой трябва да води и какъв е планът зад него. Полезното място е до предната редица, не в нея."),
        8 to ("A pull to put something forward and be seen doing it — and it works better when what you put forward is somebody else's. Contribute; do not perform."
            to "Тегли ви да изнесете нещо и да бъдете видени, докато го правите — и работи по-добре, когато изнесеното е чуждо. Дайте принос; не изнасяйте представление."),
        9 to ("Narrow focus is available and it is unusually deep. Pick the one detail worth it, because whatever you point this at will absorb the whole day."
            to "Наличен е тесен фокус и е необичайно дълбок. Изберете единия детайл, който си струва, защото онова, към което го насочите, ще погълне целия ден."),
        10 to ("Being yourself is the theme, including the parts you cannot justify. Behaving to be acceptable costs more than usual today."
            to "Темата е да бъдете себе си, включително частите, които не можете да оправдаете. Да се държите така, че да сте приемливи, днес струва повече от обичайното."),
        11 to ("Ideas and pictures, plenty of them, and none of them are instructions. Tell them to someone; do not start executing them."
            to "Идеи и картини, много, и нито една не е инструкция. Разкажете ги на някого; не започвайте да ги изпълнявате."),
        12 to ("Words either land beautifully or refuse to come at all, depending on the mood. If it will not come out well, wait — this one cannot be forced."
            to "Думите или падат красиво, или отказват да излязат, според настроението. Ако не излиза добре, изчакайте — това не се насилва."),
        13 to ("People will tell you things today. Listen and keep it; what you do with it belongs to a later day."
            to "Днес хората ще ви казват неща. Слушайте и го запазете; какво ще правите с него е за друг ден."),
        14 to ("Resources and working power are available beyond what the plan needs. Watch where it goes, because it will go somewhere."
            to "Налични са ресурси и работоспособност над онова, което планът изисква. Гледайте накъде отиват, защото ще отидат някъде."),
        15 to ("The rhythm is extreme today and will not settle into a pattern. Extend the range rather than fighting for regularity."
            to "Ритъмът днес е краен и няма да се подреди в модел. Разширете диапазона, вместо да се борите за равномерност."),
        16 to ("Enthusiasm to leap into a skill before being ready. Leap, and find the person who actually knows how."
            to "Въодушевление да се хвърлите в едно умение, преди да сте готови. Хвърлете се и намерете човека, който наистина знае как."),
        17 to ("Opinions arrive fully formed and sound like conclusions. Ask what they were built on before you say them out loud."
            to "Мненията идват напълно оформени и звучат като заключения. Попитайте върху какво са построени, преди да ги изречете."),
        18 to ("You will see what is wrong with things today, accurately and unhelpfully. Only say it where someone asked."
            to "Днес ще виждате какво е сбъркано в нещата — точно и безполезно. Казвайте го само там, където някой е попитал."),
        19 to ("Needs are loud — food, contact, reassurance, yours and everyone's. Ask directly instead of arranging the room until someone notices."
            to "Нуждите са силни — храна, контакт, успокоение, ваши и на всички. Поискайте директно, вместо да подреждате стаята, докато някой не забележи."),
        20 to ("Sharp presence and the ability to say exactly what is true now. Saying it is the whole action; it does not have to become a plan."
            to "Остро присъствие и способност да кажете точно какво е вярно сега. Казването е цялото действие; не е нужно да става план."),
        21 to ("A grip on your own territory — money, time, method — tightening beyond what the situation needs. Control what is actually yours and leave the rest."
            to "Хватка върху собствената територия — пари, време, метод — която се затяга над нуждата на ситуацията. Контролирайте онова, което наистина е ваше, и оставете останалото."),
        22 to ("Social openness that comes and goes with the mood; charming now, unavailable in an hour. Do the people-facing thing while the door is open."
            to "Социална отвореност, която идва и си отива с настроението — очарователни сега, недостъпни след час. Направете нещото с хора, докато вратата е отворена."),
        23 to ("Something you know is hard to put into words others will accept. Timing decides whether you are heard or called strange."
            to "Нещо, което знаете, трудно се слага в думи, които другите ще приемат. Моментът решава дали ще ви чуят, или ще ви нарекат странни."),
        24 to ("The same thought comes round again, slightly clearer. Let it come; this is how it finishes, not a sign of being stuck."
            to "Същата мисъл минава отново, малко по-ясна. Оставете я да дойде; така приключва, а не е знак, че сте заклещени."),
        25 to ("An impersonal warmth that is not directed at anyone in particular. Beautiful in the open, wounding if someone takes it personally."
            to "Безлична топлина, която не е насочена към никого конкретно. Красива на открито, наранява, ако някой я приеме лично."),
        26 to ("A talent for presenting things in the light that makes them work. The line between selling and stretching moves easily today."
            to "Дарба да представяте нещата в светлината, в която работят. Границата между продаване и разтягане днес се мести лесно."),
        27 to ("A pull to look after people, past the point where it helps. Ask whether what you are feeding is actually hungry."
            to "Тегли ви да се грижите за хората отвъд точката, в която помага. Попитайте дали онова, което храните, наистина е гладно."),
        28 to ("A day that goes looking for what a life is worth, and finds it in the difficulty rather than in the answer. Choose which struggle; you cannot skip having one."
            to "Ден, който търси колко струва един живот и го намира в трудността, а не в отговора. Изберете коя борба; да нямате никаква не е опция."),
        29 to ("Yes comes out of the body easily today and commits you further than you meant. Check what you agreed to before the day ends."
            to "Днес „да“ излиза от тялото лесно и ви обвързва по-далеч, отколкото сте искали. Проверете на какво сте се съгласили, преди денят да свърши."),
        30 to ("Desire runs hot and attaches to things you did not choose to want. Feel it fully and do not sign anything."
            to "Желанието гори и се захваща за неща, които не сте избрали да искате. Изживейте го напълно и не подписвайте нищо."),
        31 to ("A voice that can lead, if the room has already turned towards you. Unasked, the same words come out as self-appointment."
            to "Глас, който може да води, ако стаята вече се е обърнала към вас. Непопитан, същите думи излизат като самоназначаване."),
        32 to ("A clear read on what will last and what will not, arriving as unease rather than as analysis. Trust the read; do not act on the fear."
            to "Ясно разчитане на онова, което ще издържи и което не, идващо като притеснение, а не като анализ. Доверете се на разчитането; не действайте по страха.")
    )

    private val second: Map<Int, Pair<String, String>> = mapOf(
        33 to ("A need to withdraw, and something worth retelling once you come back. Take the retreat; the story is the reason it is allowed."
            to "Нужда да се оттеглите и нещо, което си струва да бъде разказано, след като се върнете. Вземете си оттеглянето; разказът е причината да е позволено."),
        34 to ("Raw power that only behaves when it is busy. Give it work before it finds its own."
            to "Груба мощ, която се държи прилично само когато е заета. Дайте ѝ работа, преди да си намери сама."),
        35 to ("An appetite for the next experience, and impatience with the one in progress. Finish the feeling before you collect another."
            to "Апетит за следващото преживяване и нетърпение към онова, което е в ход. Довършете чувството, преди да съберете ново."),
        36 to ("Going into something without knowing how, and the turbulence that follows. The mess is the mechanism, not the error."
            to "Влизане в нещо, без да знаете как, и турбуленцията, която следва. Бъркотията е механизмът, не грешката."),
        37 to ("Warmth, agreements and the wish to be part of something. Make the handshake explicit; today's assumptions become tomorrow's grievance."
            to "Топлина, договорки и желание да сте част от нещо. Направете ръкостискането ясно; днешните предположения стават утрешната обида."),
        38 to ("The urge to fight, whether or not there is anything worth fighting. Pick the cause or the cause picks you."
            to "Порив да се борите, независимо дали има за какво. Изберете причината, или причината избира вас."),
        39 to ("Provocation in the air; something you say will pull a real reaction out of someone. Useful with consent, cruel without it."
            to "Провокация във въздуха; нещо, което кажете, ще извади истинска реакция от някого. Полезно със съгласие, жестоко без него."),
        40 to ("Work, then the need to be entirely alone. Take the aloneness without explaining it as a mood."
            to "Работа, после нужда да сте напълно сами. Вземете си самотата, без да я обяснявате като настроение."),
        41 to ("The pressure to begin a new experience, with nothing available to begin. Let the fantasy be a fantasy for now."
            to "Натиск да започнете ново преживяване, без нищо налично за започване. Оставете фантазията да си е фантазия за сега."),
        42 to ("Energy for finishing, including the last unrewarding part. Close something today rather than opening one."
            to "Енергия за довършване, включително последната неблагодарна част. Затворете нещо днес, вместо да отваряте."),
        43 to ("A knowing that arrives without steps and cannot be explained on demand. Say it only where the language exists to receive it."
            to "Знание, което пристига без стъпки и не може да се обясни по заявка. Изричайте го само там, където има език да го приеме."),
        44 to ("Recognition of a pattern in a person — instinct working off memory. Believe the smell test even if you cannot show the evidence."
            to "Разпознаване на модел в един човек — инстинкт, който работи от паметта. Вярвайте на усета, дори да не можете да покажете доказателството."),
        45 to ("A claim over what belongs to the group and how it gets divided. Distribute; do not try to own it as well."
            to "Претенция върху онова, което е на групата, и как се разпределя. Разпределяйте; не се опитвайте и да го притежавате."),
        46 to ("A body in the right place at the right time, without having planned it. Turn up; the luck is in being physically present."
            to "Тяло на правилното място в правилното време, без да е било планирано. Идете; късметът е в това да сте физически там."),
        47 to ("Going back over what happened until it makes a picture, and the discomfort of the picture being unformed. Sit with it; forcing the meaning produces a wrong one."
            to "Връщане върху случилото се, докато не се получи картина, и неудобството от неоформената картина. Останете с него; насилването на смисъла произвежда грешен."),
        48 to ("Depth that cannot be demonstrated on request, and the fear of not being adequate when it counts. The depth is there; the fear is not evidence."
            to "Дълбочина, която не се показва по заявка, и страхът да не сте достатъчни, когато има значение. Дълбочината я има; страхът не е доказателство."),
        49 to ("Instant clarity about what will and will not be accepted, and the willingness to end things on it. Check the principle is yours before you act on it."
            to "Мигновена яснота какво ще се приеме и какво не, и готовност да прекратите нещо заради това. Проверете дали принципът е ваш, преди да действате."),
        50 to ("Responsibility for what has to be looked after, felt whether or not it was assigned. Name the rule; do not simply enforce it silently."
            to "Отговорност за онова, за което трябва да се полага грижа, усетена независимо дали е била възложена. Назовете правилото; не го налагайте мълчаливо."),
        51 to ("An appetite for going where others will not, and the shock that comes with it. Competitive today about things that may not matter."
            to "Апетит да отидете там, където другите няма, и сътресението, което идва с това. Днес сте състезателни за неща, които може да нямат значение."),
        52 to ("Stillness with a lot held inside it — the condition for real concentration. Sit down and apply it to one thing."
            to "Неподвижност с много задържано вътре — условието за истинско съсредоточаване. Седнете и я приложете към едно нещо."),
        53 to ("Pressure to start, and no interest in where it ends. Begin, and accept that someone else finishes."
            to "Натиск да започнете и никакъв интерес накъде свършва. Започнете и приемете, че друг ще довърши."),
        54 to ("Ambition with no brakes, attaching itself to whatever is in front of you. Aim it deliberately or it aims itself."
            to "Амбиция без спирачки, която се захваща за онова, което е пред вас. Насочете я умишлено, или тя ще се насочи сама."),
        55 to ("The mood decides how full the world looks, and it will swing without a reason. Do not make a decision from either end of it."
            to "Настроението решава колко пълен изглежда светът и ще се люлее без причина. Не вземайте решение от нито един от двата му края."),
        56 to ("A day for telling it — the story that puts people in the room. Have something real to tell or the embellishing starts."
            to "Ден за разказване — историята, която слага хората в стаята. Имайте какво истинско да разкажете, иначе украсяването започва."),
        57 to ("Acute hearing: a voice, a room, the second before something happens. It speaks once; the second opinion is your mind, not your ear."
            to "Остър слух: един глас, една стая, секундата преди нещо да се случи. Говори веднъж; второто мнение е умът ви, не слухът ви."),
        58 to ("Aliveness with no cause, coming out as the urge to improve something. Improve the work, not the people."
            to "Жизненост без причина, която излиза като порив да подобрите нещо. Подобрявайте работата, не хората."),
        59 to ("The ability to get through someone's defences, and the question of whether you should. Closeness today is a decision, not an accident."
            to "Способност да минете през защитата на някого и въпросът дали трябва. Близостта днес е решение, не случайност."),
        60 to ("A limit that will not move, and the new thing that only comes from accepting it. Push and nothing happens; accept and something mutates."
            to "Ограничение, което няма да се помести, и новото, което идва само от приемането му. Блъскате — нищо не става; приемете — нещо мутира."),
        61 to ("Pressure to know why, which no answer relieves. Let the question stay open; tonight it will not be solved."
            to "Натиск да знаете защо, който никакъв отговор не облекчава. Оставете въпроса отворен; тази вечер няма да се реши."),
        62 to ("Details, names and order — the day for making a complicated thing sayable. Organise something; do not opine about it."
            to "Детайли, имена и ред — денят да направите едно сложно нещо изразимо. Подредете нещо; не изказвайте мнение за него."),
        63 to ("Doubt, pointed at whether this actually holds up. Aim it at the plan; aimed at people it does damage."
            to "Съмнение, насочено към това дали нещо наистина издържа. Насочете го към плана; насочено към хора, прави вреди."),
        64 to ("A head full of images from things already finished, out of order and loud. This is not thinking and it will not resolve by trying."
            to "Глава, пълна с образи от вече приключили неща, разбъркани и шумни. Това не е мислене и няма да се разреши с опитване.")
    )
}
