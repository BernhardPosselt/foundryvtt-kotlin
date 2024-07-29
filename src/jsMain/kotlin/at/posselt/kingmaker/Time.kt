package at.posselt.kingmaker

import com.foundryvtt.core.Game
import kotlinx.datetime.*

fun Game.getPF2EWorldTime(): LocalDateTime {
    val createdOn = settings.get<String>("pf2e", "worldClock.worldCreatedOn")
    return Instant.parse(createdOn)
        .plus(time.worldTime, DateTimeUnit.SECOND)
        .toLocalDateTime(TimeZone.UTC)
}