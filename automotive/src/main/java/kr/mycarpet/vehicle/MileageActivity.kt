package kr.mycarpet.vehicle

import ai.pleos.playground.vehicle.Vehicle
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.util.Log

class MileageActivity : Activity() {
    private lateinit var display: TextView
    private var vehicle: Vehicle? = null
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        display = TextView(this).apply { textSize = 26f; gravity = 17 }
        setContentView(display)
    }
    override fun onStart() {
        super.onStart()
        display.text = "Pleos 차량 서비스 연결 중"
        try {
            vehicle = Vehicle(this).also { it.initialize() }
            readMileage()
        } catch (e: Exception) { show("연결 실패 · " + e.message) }
    }
    private fun show(value: String) { display.text = value; Log.i("MyCarPetVehicle", value) }
    private fun readMileage() {
        val sdk = vehicle ?: return
        fun report(value: String) = runOnUiThread {
            if (vehicle === sdk) {
                show(value)
                handler.postDelayed({ readMileage() }, 5000)
            }
        }
        try {
            sdk.getOdometer().getCurrentDistanceDriven(
                { reading -> report("누적 주행거리 · " + (reading?.toString() ?: "값 없음")) },
                { error -> report("조회 실패 · " + error.message) })
        } catch (e: Exception) { report("조회 실패 · " + e.message) }
    }
    override fun onStop() {
        val sdk = vehicle
        vehicle = null
        handler.removeCallbacksAndMessages(null)
        sdk?.release()
        super.onStop()
    }
}
