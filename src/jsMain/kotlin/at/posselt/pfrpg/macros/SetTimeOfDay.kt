package at.posselt.pfrpg.macros

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.SetTimeOfDayMode
import at.posselt.pfrpg.app.forms.TimeInput
import at.posselt.pfrpg.app.WaitButton
import at.posselt.pfrpg.app.forms.formContext
import at.posselt.pfrpg.app.wait
import at.posselt.pfrpg.secondsBetweenNowAndTarget
import at.posselt.pfrpg.toUtcInstant
import at.posselt.pfrpg.utils.fromDateInputString
import at.posselt.pfrpg.utils.getPF2EWorldTime
import at.posselt.pfrpg.utils.toDateInputString
import at.posselt.pfrpg.utils.toLocalUtcDate
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
    val stored = localStorage.getItem("${Config.moduleId}.time-input") ?: "00:00"
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
    localStorage.setItem("${Config.moduleId}.time-input", time.time.toDateInputString())
}