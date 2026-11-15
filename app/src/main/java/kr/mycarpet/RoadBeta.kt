package kr.mycarpet

import android.Manifest
import android.app.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.location.*
import android.os.Bundle
import android.os.SystemClock
import android.util.Rational
import android.view.Gravity
import android.view.WindowManager
import android.widget.*

class RoadBeta : Activity() {
    private val gps by lazy { getSystemService(LocationManager::class.java) }
    private val store by lazy { getSharedPreferences("road_beta", MODE_PRIVATE) }
    private val recording get() = DriveRecorder.active

    private var status = "주차 후 시작하세요."
    private var greeting = false
    private val mascot by lazy { assets.open("images/reureureu_idle.png").use { BitmapFactory.decodeStream(it) } }

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        if (packageManager.hasSystemFeature("android.hardware.type.automotive")) { finish(); return }

        draw()
    }
    private fun draw() {
        val root = LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER
            setBackgroundColor(0xff282c48.toInt())
        }
        setContentView(root)
        root.addView(ImageView(this).apply {
            setImageBitmap(mascot); contentDescription="르르르"
            if (!recording && !isInPictureInPictureMode) setOnClickListener { greeting=!greeting; draw() }
        },LinearLayout.LayoutParams(-1,0,1f))
        if(isInPictureInPictureMode) return
        fun line(s:String) { root.addView(TextView(this).apply {text=s; gravity=Gravity.CENTER; setTextColor(-1);textSize=17f}) }
        fun action(s:String, f:()->Unit){root.addView(Button(this).apply{text=s;setOnClickListener{f()}})}
        val meters=store.getFloat("meters",0f)
        line("모바일 GPS 베타 · %.2f km · %d XP".format(meters/1000, (meters/1000).toInt()*GameRules.xpPerKm))
        line("이번 주행 · %.2f km · %d분 %02d초".format(store.getFloat("trip",0f)/1000,store.getLong("duration",0)/60000,store.getLong("duration",0)/1000%60))
        line(if(!recording && greeting) "르르르: 같이 드라이브 갈까?" else if (recording) store.getString("signal",status) ?: status else status)
        if(recording) action("주차 후 기록 종료"){ stopRecording() }
        else action("주행 기록 시작"){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),7)
            else startTrip()
        }
        if (!recording && meters > 0f) action("GPS 기록 초기화") {
            AlertDialog.Builder(this).setMessage("누적 GPS 거리와 베타 경험치를 0으로 초기화할까요?")
                .setNegativeButton("취소", null).setPositiveButton("초기화") { _, _ ->
                    store.edit().remove("meters").remove("trip").remove("duration").apply()

                    status = "GPS 기록을 초기화했어요."
                    draw()
                }.show()
        }
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE))
            action("르르르 작은 창"){ if(!enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(1,1)).build())) {
                status="기기 설정에서 화면 속 화면 허용을 확인하세요.";draw()
            } }
    }
    override fun onRequestPermissionsResult(code:Int,p:Array<String>,results:IntArray){
        super.onRequestPermissionsResult(code,p,results)
        if(code==7 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) startTrip()
        else {status="주행 측정에는 정확한 위치 권한이 필요해요.";draw()}
    }
    private fun startTrip(){
        if(!gps.isProviderEnabled(LocationManager.GPS_PROVIDER)){status="휴대폰 위치 서비스를 켜 주세요."; draw(); return}
        startForegroundService(android.content.Intent(this,DriveRecorder::class.java))
        status="기록 시작 중"
        draw()
    }
    private fun stopRecording(){
        stopService(android.content.Intent(this,DriveRecorder::class.java))
        status="주행 기록 저장 완료"
    }
    private val tick=android.os.Handler(android.os.Looper.getMainLooper())
    private val refresh=object:Runnable{
        override fun run(){ if(!isInPictureInPictureMode) draw();tick.postDelayed(this,1500) }
    }
    override fun onStart(){super.onStart();tick.post(refresh)}
    override fun onStop(){tick.removeCallbacks(refresh);super.onStop()}
    override fun onPictureInPictureModeChanged(pip:Boolean,config:Configuration){super.onPictureInPictureModeChanged(pip,config);draw()}
}
