package kr.mycarpet

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
import java.time.LocalDate

class Garage : Activity() {
    private val prefs by lazy { getSharedPreferences("garage", MODE_PRIVATE) }
    private val timer = Handler(Looper.getMainLooper())
    private lateinit var pet: Pet
    private val mock = MockVehicleDataSource()
    private var state = VehicleState.UNKNOWN
    private val driving get() = state != VehicleState.PARKED
    private var scarf = false
    private var message = "함께 달릴 준비가 됐어요."
    private var fuel = 50
    private var threshold = 20
    private var alerts = true
    private var summary = "아직 기록이 없어요"
    private var ev = false
    private var lastReply = "안녕! 오늘은 어떤 이야기를 해볼까?"
    private val energy get() = Energy(ev, fuel.takeIf { it in 0..100 })
    private var bond = 0
    private var coins = 0
    private var gift = 0
    private var guest = false
    private var guestOwned = false
    private var subscriber = false
    private var visits = mutableSetOf<String>()
    private val name get() = if (guest) "밤이" else "르르르"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pet = Pet(prefs.getInt("xp", 0), prefs.getInt("mood", 70), prefs.getInt("km", 0), prefs.getInt("odo", 10000))
        summary = prefs.getString("summary",summary) ?: summary
        subscriber = prefs.getBoolean("testMember",false)
        ev = prefs.getBoolean("ev",false)
        fuel = prefs.getInt("fuel", 50)
        threshold = prefs.getInt("threshold", 20)
        alerts = prefs.getBoolean("alerts", true)
        scarf = prefs.getBoolean("scarf", false)
        bond = prefs.getInt("bond", 0)
        coins = prefs.getInt("coins", 0)
        gift = prefs.getInt("gift", 0)
        guest = prefs.getBoolean("guest", false)
        guestOwned = prefs.getBoolean("guestOwned", false)
        visits = prefs.getStringSet("visits", emptySet()).orEmpty().toMutableSet()
        state = if (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK ==
            android.content.res.Configuration.UI_MODE_TYPE_CAR || packageManager.hasSystemFeature("android.hardware.type.automotive"))
            VehicleState.UNKNOWN else VehicleState.PARKED
        render()
    }

    private fun save() {
        prefs.edit().putInt("xp", pet.xp).putInt("mood", pet.happiness).putInt("km", pet.distanceKm)
            .putInt("odo",pet.lastKm ?: 10000).putBoolean("ev",ev).putString("summary",summary).putBoolean("testMember",subscriber)
            .putInt("fuel", fuel).putInt("threshold", threshold).putBoolean("alerts", alerts)
            .putBoolean("scarf", scarf).putInt("bond", bond).putInt("coins", coins)
            .putInt("gift", gift).putBoolean("guest", guest).putBoolean("guestOwned", guestOwned)
            .putStringSet("visits", visits.toSet()).apply()
    }

    private fun choose(title: String, options: List<String>, select: (Int) -> Unit) {
        android.app.AlertDialog.Builder(this).setTitle(title).setItems(options.toTypedArray()) { _, i ->
            if (!driving) { select(i); save(); render() }
        }.show()
    }

    private fun simulate(trip: Trip) {
        state = VehicleState.MOVING
        message = "도착하면 여행 결과를 알려줄게요."
        val odometer = (pet.lastKm ?: 10000) + trip.km
        timer.postDelayed({
            pet = pet.observe(odometer,trip)
            if(fuel >= 0) fuel = (fuel - trip.km).coerceAtLeast(0)
            val newPlace = trip.place?.let { visits.add(it) } == true
            if (newPlace) coins += GameRules.visitCoins
            state = VehicleState.PARKED
            message = "도착! ${trip.km} km · +${trip.km * GameRules.xpPerKm} XP" + if (newPlace) " · 첫 방문 +10 코인" else ""
            save()
            render()
        }, 4000)
    }
    private fun dp(n: Int) = (resources.displayMetrics.density * n).toInt()
    private fun box(shade: String) = android.graphics.drawable.GradientDrawable().apply {
        cornerRadius = dp(16).toFloat()
        setColor(Color.parseColor(shade))
        setStroke(dp(1), Color.parseColor("#747B9E"))
    }
    private fun label(value: String, size: Int) = TextView(this).apply {
        text = value; textSize = size.toFloat(); setTextColor(Color.WHITE)
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(8),dp(2),dp(8),dp(2))
    }
    private fun button(title: String, click: () -> Unit) = android.widget.Button(this).apply {
        text = title; textSize = 14f; isAllCaps = false; setTextColor(Color.WHITE)
        background = box("#444971"); isEnabled = !driving
        setOnClickListener { if (!driving) click() }
    }
    private fun LinearLayout.share(view: android.view.View, weight: Float = 1f) {
        addView(view, LinearLayout.LayoutParams(0,-1,weight).apply { setMargins(dp(2),0,dp(2),0) })
    }
    private fun meter(value: Int, shade: String) = ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply {
        max = 100; progress = value
        progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor(shade))
    }
    private fun render() {
        val home = ReferenceLayout(this)
        home.setBackgroundColor(Color.rgb(39,39,65))
        setContentView(home)
        if (driving) {
            home.addView(label(if(state == VehicleState.UNKNOWN) "주차 상태 미확인 · 조작 잠금" else "데모 주행 중 · 도착 후 만나요",22))
            return
        }
        home.addView(PetHomeScene(this,guest,scarf,pet.sick,energy.hungry(threshold)==true))
        with(home) {
            fun place(v:android.view.View,x:Int,y:Int,ww:Int,hh:Int) {
                addRegion(v,x,y,ww,hh)
            }
            fun title(s:String,size:Int,shade:String?=null):TextView = label(s,14).apply {
                gravity=Gravity.CENTER; typeface=Typeface.DEFAULT_BOLD;setPadding(2,1,2,1)
                home.registerFont(this,size)
                if(shade!=null) background=box(shade)
            }
            fun nav(s:String,y:Int,action:()->Unit){
                place(title(s,25,if(y==180) "#726AA9" else null).apply {setOnClickListener { action() }},17,y,150,103)
            }
            place(android.view.View(this@Garage).apply {setBackgroundColor(Color.rgb(41,41,68))},0,0,182,941)
            place(title("MY\nCAR PET",40),7,18,170,124)
            nav("⌂\n홈",180){render()}
            nav("•••\n대화하기",300){chat()}
            nav("♧\n꾸미기",415){choose("펫 꾸미기",listOf(if(scarf) "스카프 벗기" else "스카프 착용")){scarf=!scarf}}
            nav("▤\n상점",530){choose("캐릭터 상점 · $coins 코인",listOf(if(guestOwned) "르르르 / 밤이 교체" else "밤이 입양 · ${GameRules.characterCost} 코인")){
                if(guestOwned) guest=!guest else if(coins>=GameRules.characterCost){coins-=GameRules.characterCost;guestOwned=true;guest=true}
                else message="여행지에서 드라이브 코인을 모아줘!"
            }}
            nav("⌖\n여행기록",650){choose("여행 기록 · ${pet.distanceKm} km",visits.sorted().map{"$it · +${GameRules.visitCoins} 코인"}.ifEmpty{listOf("아직 방문한 여행지가 없어요.")}){}}
            nav("⚙\n설정",775){settings()}
            fun card(x:Int,y:Int,ww:Int,hh:Int){place(android.view.View(this@Garage).apply{background=box("#EF393D64")},x,y,ww,hh)}
            card(314,30,608,94)
            place(title("Lv. ${pet.level}",36,"#736BC4"),335,44,180,64)
            val xp=pet.xp % GameRules.levelXp
            place(title("$xp / ${GameRules.levelXp} XP",24),615,42,260,38)
            place(meter(100*xp/GameRules.levelXp,"#FFD36B"),524,83,359,26)
            place(title("🪙  $coins",30,"#EE343759"),1165,28,202,75)
            place(title("♛   MyHyundai\n"+if(subscriber) "테스트 가입" else "테스트 미가입",22,"#EE343759").apply{setOnClickListener{membership()}},1395,28,235,75)
            val quote=when{alerts && energy.hungry(threshold)==true -> if(ev) "배고파! 충전하러 가자!" else "배고파! 밥 먹으러 가자!"
                pet.sick -> "오늘은 조금 지쳤어. 잠깐 쉬어 갈까?";else->message}
            place(title(quote,30,"#FAFFFCF6").apply{setTextColor(Color.rgb(43,37,58));setPadding(dp(6),0,dp(6),0)},317,311,316,186)
            card(1142,138,508,232)
            place(title("💗   행복도",29),1163,155,230,47)
            place(title("${pet.happiness}%",28),1490,155,130,47)
            place(meter(pet.happiness,"#F7A3DB"),1253,203,339,36)
            place(title(if(ev) "🔋   배터리" else "⛽   연료",29),1163,265,230,46)
            place(title(energy.percent?.let{"$it%"}?:"미확인",28),1463,265,157,46)
            energy.percent?.let{place(meter(it,"#ABE67A"),1253,311,339,36)}
            card(1142,387,508,252)
            place(title("최근 운행 요약",31),1170,405,320,50)
            place(title(summary,27),1166,477,458,77)
            place(title("데모 모드  ·  주차 중",21),1170,564,320,47)
        }
    }
    private fun membership(){
        choose("테스트 멤버십 · " + if(gift>0) "$gift 번 보관 중" else "럭키박스", (1..5).map{"?   박스 $it"}) {
            when { !subscriber -> message="설정에서 테스트 회원으로 전환해 주세요."
                gift>0 -> message="이미 $gift 번 상자를 선택했어요."
                else -> { gift=it+1;message="$gift 번 상자를 보관했어요." } }
        }
    }
    private fun settings(){
        choose("설정 · 데모", listOf("배고픔 기준 $threshold%","연료 알림 " + if(alerts) "켜짐" else "꺼짐",
            "차량 시뮬레이터","테스트 멤버십 · "+(if(subscriber) "가입" else "미가입"), "모바일 GPS 베타")){
            when(it){0->choose("알림 기준",listOf("10%","20%","30%")){i->threshold=(i+1)*10}
                1->alerts=!alerts
                2->{ val keys=mock.scenarios.keys.toList()
                    choose("모의 차량 신호",keys+listOf("주유 / 충전 완료","잔량 8%","잔량 미확인","연료 / 전기차 전환")){
                        i->when(i){keys.size->fuel=100; keys.size+1->fuel=8; keys.size+2->fuel=-1; keys.size+3->ev=!ev
                            else->{summary=keys[i];simulate(mock.scenarios.getValue(keys[i]))} }
                    }
                }
                3->subscriber=!subscriber
                4->startActivity(android.content.Intent(this,RoadBeta::class.java))
            }
        }
    }
    private fun chat(){
        val endpoint=getString(R.string.pet_chat_endpoint)
        val input=EditText(this).apply{hint="펫에게 하고 싶은 말";maxLines=3}
        android.app.AlertDialog.Builder(this).setTitle("$name · "+if(endpoint.isBlank()) "모의 대화" else "AI 대화")
            .setMessage(lastReply).setView(input).setNegativeButton("닫기",null).setPositiveButton("보내기"){_,_->
                val words=input.text.toString().trim().take(500)
                if(words.isEmpty() || driving) return@setPositiveButton
                val condition=when(energy.hungry(threshold)){true->"배고픔";false->"든든함";null->"잔량 미확인"}
                if(endpoint.isBlank()){
                    lastReply=when{pet.sick->"조금 지쳤어. 같이 쉬어 가면 좋겠어."
                        condition=="배고픔"->if(ev) "배고파! 충전하러 갈까?" else "배고파! 밥 먹으러 가자!"
                        words.contains("여행")->"어디로 가볼까? 바다가 보이는 경포해변도 좋아!"
                        words.contains("안녕")->"안녕! 나는 $name. 오늘도 옆자리에 있을게."
                        else->"응, 듣고 있어! "+if(pet.happiness>=70) "오늘은 기분이 좋아." else "천천히 함께 가자."}
                    chat()
                }else{
                    val companion=name;val mood=pet.happiness;val ill=pet.sick
                    Thread{val reply=runCatching{PetChat.ask(endpoint,companion,mood,words,condition,ill)}.getOrElse{"대화 서버 연결을 확인해 주세요."}
                        runOnUiThread{if(!isDestroyed && !isFinishing && !driving){lastReply=reply;chat()}}
                    }.start()
                }
            }.show()
    }
    override fun onDestroy() {
        timer.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}







