package at.posselt.kingmaker.macros

import at.posselt.kingmaker.SetTimeOfDayMode
import at.posselt.kingmaker.app.forms.TimeInput
import at.posselt.kingmaker.app.WaitButton
import at.posselt.kingmaker.app.forms.formContext
import at.posselt.kingmaker.app.wait
import at.posselt.kingmaker.secondsBetweenNowAndTarget
import at.posselt.kingmaker.toUtcInstant
import at.posselt.kingmaker.utils.fromDateInputString
import at.posselt.kingmaker.utils.getPF2EWorldTime
import at.posselt.kingmaker.utils.toDateInputString
import at.posselt.kingmaker.utils.toLocalUtcDate
import com.foundryvtt.core.Game
import js.objects.recordOf
import kotlinx.browser.localStorage
import kotlinx.coroutines.await
import kotlinx.datetime.*
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface TimeOfDayData {
    val time: String
}


suspend fun setTimeOfDayMacro(game: Game) {
    val stored = localStorage.getItem("kingmaker-tools.time-input") ?: "00:00"
    val time = LocalTime.fromDateInputString(stored)
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
                val now = game.getPF2EWorldTime().toUtcInstant()
                val target = getTargetTime(data.time, now)
                val seconds = secondsBetweenNowAndTarget(now, target, SetTimeOfDayMode.RETRACT)
                advanceTimeTo(game, seconds, target)
            },
            WaitButton(label = "Advance") { data, action ->
                val now = game.getPF2EWorldTime().toUtcInstant()
                val target = getTargetTime(data.time, now)
                val seconds = secondsBetweenNowAndTarget(now, target, SetTimeOfDayMode.ADVANCE)
                advanceTimeTo(game, seconds, target)
            },
        )
    )
}

private fun getTargetTime(time: String, now: Instant): Instant {
    val targetTime = LocalTime.fromDateInputString(time)
    val targetDate = now.toLocalUtcDate()
    val target = targetDate.atTime(targetTime).toInstant(TimeZone.UTC)
    return target
}

private suspend fun advanceTimeTo(game: Game, seconds: Long, target: Instant) {
    game.time.advance(seconds.toInt()).await()
    val time = target.toLocalDateTime(TimeZone.UTC)
    localStorage.setItem("kingmaker-tools.time-input", time.time.toDateInputString())
}