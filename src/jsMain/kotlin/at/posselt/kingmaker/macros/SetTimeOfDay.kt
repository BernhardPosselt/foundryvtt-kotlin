package at.posselt.kingmaker.macros

import at.posselt.kingmaker.SetTimeOfDayMode
import at.posselt.kingmaker.app.TimeInput
import at.posselt.kingmaker.app.WaitButton
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.wait
import at.posselt.kingmaker.secondsBetweenNowAndTarget
import at.posselt.kingmaker.utils.toInstant
import com.foundryvtt.core.Game
import js.objects.recordOf
import kotlinx.browser.localStorage
import kotlinx.coroutines.await
import kotlinx.datetime.*
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.js.JsPlainObject
import kotlin.js.Date

@JsPlainObject
private external interface TimeOfDayData {
    val date: Date
}


suspend fun setTimeOfDayMacro(game: Game) {
    val stored = localStorage.getItem("kingmaker-tools.time-input") ?: "00:00"
    val time = LocalTime.parse(stored, LocalTime.Format {
        hour(padding = Padding.ZERO)
        char(':')
        minute(padding = Padding.ZERO)
    })
    wait<TimeOfDayData, Unit>(
        title = "Advance/Retract to Time of Day",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                TimeInput(
                    name = "time",
                    label = "Time",
                    value = time,
                ),
            )
        ),
        buttons = listOf(
            WaitButton(label = "Retract") { data, action ->
                val now = Clock.System.now()
                val target = data.date.toInstant()
                val seconds = secondsBetweenNowAndTarget(now, target, SetTimeOfDayMode.RETRACT)
                advanceTimeTo(game, seconds, target)
            },
            WaitButton(label = "Advance") { data, action ->
                val now = Clock.System.now()
                val target = data.date.toInstant()
                val seconds = secondsBetweenNowAndTarget(now, target, SetTimeOfDayMode.ADVANCE)
                advanceTimeTo(game, seconds, target)
            },
        )
    )
}

private suspend fun advanceTimeTo(game: Game, seconds: Long, target: Instant) {
    game.time.advance(seconds.toInt()).await()
    val time = target.toLocalDateTime(TimeZone.UTC)
    localStorage.setItem(
        "kingmaker-tools.time-input", time.format(LocalDateTime.Format {
            hour(padding = Padding.ZERO)
            char(':')
            minute(padding = Padding.ZERO)
        })
    )
}