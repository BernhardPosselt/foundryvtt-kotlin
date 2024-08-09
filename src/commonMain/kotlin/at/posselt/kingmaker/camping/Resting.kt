package at.posselt.kingmaker.camping

import at.posselt.kingmaker.divideRoundingUp
import kotlin.math.max

fun calculateRestoredHp(
    currentHp: Int,
    maxHp: Int,
    conModifier: Int,
    level: Int,
): Int {
    val maxRestored = max(conModifier, 1) * level
    val hpLost = maxHp - currentHp
    return if (hpLost >= maxRestored) maxRestored else hpLost
}


fun calculateRestDurationSeconds(restSecondsPerPc: Array<Int>): Int {
    val partySize = restSecondsPerPc.size
    val restDurationSeconds = restSecondsPerPc.average().toInt()
    return if (partySize < 2) {
        restDurationSeconds
    } else {
        restDurationSeconds + (restDurationSeconds / (partySize - 1))
    }
}

fun calculateDailyPreparationSeconds(gunsToClean: Int): Int =
    if (gunsToClean == 0) 30 * 60 else gunsToClean.divideRoundingUp(4) * 3600
