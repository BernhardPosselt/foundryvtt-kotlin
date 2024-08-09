package at.posselt.kingmaker

import kotlinx.datetime.*

fun LocalDateTime.toUtcInstant() =
    toInstant(TimeZone.UTC)

enum class SetTimeOfDayMode {
    ADVANCE,
    RETRACT
}

private fun daysToAdd(now: Instant, target: Instant, mode: SetTimeOfDayMode): Int =
    if (now >= target && mode == SetTimeOfDayMode.ADVANCE) {
        1
    } else if (now <= target && mode == SetTimeOfDayMode.RETRACT) {
        -1
    } else {
        0
    }

fun secondsBetweenNowAndTarget(now: Instant, target: Instant, mode: SetTimeOfDayMode): Long {
    val addDays = daysToAdd(now, target, mode)
    val targetDay = target.plus(addDays, DateTimeUnit.DAY, TimeZone.UTC)
    return targetDay.epochSeconds - now.epochSeconds
}
