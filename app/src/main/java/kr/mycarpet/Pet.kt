package kr.mycarpet

object GameRules {
    const val xpPerKm = 2
    const val levelXp = 20
    const val illness = 25
    const val visitCoins = 10
    const val characterCost = 20
}
data class Energy(val electric: Boolean, val percent: Int?) {
    fun hungry(threshold: Int): Boolean? = percent?.takeIf { it in 0..100 }?.let { it <= threshold }
}
data class Pet(val xp: Int, val happiness: Int, val distanceKm: Int, val lastKm: Int? = null) {
    val level get() = 1 + xp / GameRules.levelXp
    val sick get() = happiness <= GameRules.illness
    fun observe(totalKm: Int, trip: Trip): Pet {
        if (totalKm < 0) return this
        val previous = lastKm ?: return copy(lastKm = totalKm)
        if (totalKm <= previous) return this
        val delta = totalKm - previous
        return copy(xp = xp + delta * GameRules.xpPerKm, distanceKm = distanceKm + delta,
            happiness = (happiness + trip.moodChange).coerceIn(0, 100), lastKm = totalKm)
    }
}
