package at.posselt.kingmaker.utils

import kotlinx.datetime.*
import kotlin.js.Date

fun Date.toInstant() =
    Instant.fromEpochSeconds(getSeconds().toLong())

fun Date.toLocalUtcDateTime() =
    toInstant().toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.toJsUtcDate() =
    Date(toUtcInstant().epochSeconds)

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