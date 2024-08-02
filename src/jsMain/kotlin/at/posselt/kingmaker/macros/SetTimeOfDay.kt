package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.TimeInput
import at.posselt.kingmaker.app.WaitButton
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.wait
import at.posselt.kingmaker.utils.SetTimeOfDayMode
import at.posselt.kingmaker.utils.secondsBetweenNowAndTarget
import at.posselt.kingmaker.utils.toInstant
import com.foundryvtt.core.Game
import js.objects.recordOf
import kotlinx.coroutines.await
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.js.JsPlainObject
import kotlin.js.Date

@JsPlainObject
private external interface TimeOfDayData {
    val date: Date
}


suspend fun setTimeOfDayMacro(game: Game) {
    val value = Clock.System.now()
        .toLocalDateTime(TimeZone.UTC)
    wait<TimeOfDayData, Unit>(
        title = "Advance/Retract to Time of Day",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                TimeInput(
                    name = "time",
                    label = "Time",
                    value = value,
                ),
            )
        ),
        buttons = listOf(
            WaitButton(label = "Retract") { data, action ->
                val seconds =
                    secondsBetweenNowAndTarget(Clock.System.now(), data.date.toInstant(), SetTimeOfDayMode.RETRACT)
                game.time.advance(seconds.toInt()).await()
            },
            WaitButton(label = "Advance") { data, action ->
                val seconds =
                    secondsBetweenNowAndTarget(Clock.System.now(), data.date.toInstant(), SetTimeOfDayMode.ADVANCE)
                game.time.advance(seconds.toInt()).await()
            },
        )
    )
}