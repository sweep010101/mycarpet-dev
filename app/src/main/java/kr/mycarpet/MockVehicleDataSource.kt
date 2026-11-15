package kr.mycarpet

data class Trip(val km: Int, val moodChange: Int, val place: String?)
enum class VehicleState { UNKNOWN, PARKED, MOVING }

class MockVehicleDataSource {
    val scenarios = linkedMapOf(
        "제한속도 준수 · 경포해변" to Trip(5, 5, "경포해변"),
        "졸음쉼터 이용 · 남산" to Trip(8, 8, "남산"),
        "안정적 운행" to Trip(6, 4, null),
        "급가속" to Trip(2, -8, null),
        "급제동" to Trip(2, -8, null),
        "과속 이벤트" to Trip(3, -10, null),
        "신호위반 이벤트" to Trip(4, -15, null)
    )
}


