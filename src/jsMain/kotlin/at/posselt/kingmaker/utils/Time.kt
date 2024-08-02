package at.posselt.kingmaker.utils

import at.posselt.kingmaker.data.regions.Month
import at.posselt.kingmaker.data.regions.getMonth
import com.foundryvtt.core.Game
import kotlinx.datetime.*
import kotlin.js.Date

fun Game.getPF2EWorldTime(): LocalDateTime {
    val createdOn = settings.get<String>("pf2e", "worldClock.worldCreatedOn")
    return Instant.parse(createdOn)
        .plus(time.worldTime, DateTimeUnit.SECOND)
        .toLocalDateTime(TimeZone.UTC)
}

fun Game.getCurrentMonth(): Month =
    getMonth(getPF2EWorldTime().monthNumber - 1)

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