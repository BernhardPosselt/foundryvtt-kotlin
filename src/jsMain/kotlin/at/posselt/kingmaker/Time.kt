package at.posselt.kingmaker

import at.posselt.kingmaker.data.regions.Month
import at.posselt.kingmaker.data.regions.getMonth
import com.foundryvtt.core.Game
import kotlinx.datetime.*

fun Game.getPF2EWorldTime(): LocalDateTime {
    val createdOn = settings.get<String>("pf2e", "worldClock.worldCreatedOn")
    return Instant.parse(createdOn)
        .plus(time.worldTime, DateTimeUnit.SECOND)
        .toLocalDateTime(TimeZone.UTC)
}

fun Game.getCurrentMonth(): Month =
    getMonth(getPF2EWorldTime().monthNumber - 1)