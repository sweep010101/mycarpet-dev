package kr.mycarpet

import android.app.*
import android.content.Intent
import android.location.*
import android.os.*
import android.util.Log

class DriveRecorder : Service(), LocationListener {
    companion object { var active = false; private set }
    private val gps by lazy { getSystemService(LocationManager::class.java) }
    private val store by lazy { getSharedPreferences("road_beta", MODE_PRIVATE) }
    private var previous: Location? = null
    private var started = 0L
    private val clock get() = SystemClock.elapsedRealtime()
    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, id: Int): Int {
        if(active) return START_NOT_STICKY
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel("trip","주행 기록",NotificationManager.IMPORTANCE_LOW))
        val open = Intent(this,RoadBeta::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val note = Notification.Builder(this,"trip").setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("르르르 · GPS 주행 기록 중").setContentText("주차 후 열어서 기록을 종료하세요.")
            .setContentIntent(PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_IMMUTABLE)).setOngoing(true).build()
        try {
            startForeground(31,note)
            gps.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000L,0f,this)
            active=true;started=clock
            store.edit().putFloat("trip",0f).putLong("duration",0L).putString("signal","GPS 수신 대기 중").apply()
        } catch(e: RuntimeException) {
            Log.w("DriveRecorder","GPS start failed",e)
            store.edit().putString("signal","GPS를 시작하지 못했어요. 위치 권한과 위치 설정을 확인하세요.").apply()
            stopSelf()
        }
        return START_NOT_STICKY
    }
    override fun onLocationChanged(loc:Location){
        if(!active || !loc.hasAccuracy() || loc.accuracy>25 || SystemClock.elapsedRealtimeNanos()-loc.elapsedRealtimeNanos>10_000_000_000L) return
        val old=previous
        var gain=0f
        if(old!=null){
            val seconds=(loc.elapsedRealtimeNanos-old.elapsedRealtimeNanos)/1e9
            if(seconds<=0) return
            val gap=old.distanceTo(loc)
            if(seconds<=45 && gap/seconds<=65){
                if(gap>=maxOf(8f,old.accuracy,loc.accuracy) && (!loc.hasSpeed() || loc.speed>=1f)){
                    gain=gap;previous=loc
                }
            }else previous=loc
        }else previous=loc
        store.edit().putFloat("meters",store.getFloat("meters",0f)+gain)
            .putFloat("trip",store.getFloat("trip",0f)+gain).putLong("duration",clock-started)
            .putString("signal","GPS 수신 중").apply()
    }
    override fun onProviderDisabled(provider:String){previous=null;store.edit().putString("signal","GPS 수신 대기 중").apply()}
    override fun onProviderEnabled(provider:String){}
    @Deprecated("Legacy") override fun onStatusChanged(p:String?,s:Int,e:Bundle?){}
    override fun onDestroy(){
        gps.removeUpdates(this)
        if(active) store.edit().putLong("duration",clock-started).putString("signal","주행 기록 저장 완료").apply()
        active=false;stopForeground(STOP_FOREGROUND_REMOVE);super.onDestroy()
    }
}
