package at.posselt.kingmaker.utils

import at.posselt.kingmaker.data.regions.Month
import at.posselt.kingmaker.data.regions.getMonth
import at.posselt.kingmaker.toUtcInstant
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
