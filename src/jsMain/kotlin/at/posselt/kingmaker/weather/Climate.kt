package at.posselt.kingmaker.weather

import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.data.regions.*
import at.posselt.kingmaker.utils.DieValue
import at.posselt.kingmaker.utils.d20Check
import at.posselt.kingmaker.utils.postChatMessage
import com.foundryvtt.core.Game
import com.foundryvtt.core.documents.RollTable
import com.foundryvtt.core.documents.RollTableDraw
import com.foundryvtt.core.documents.TableMessageOptions
import js.objects.recordOf

private val hazardLevelRegex = "\\(Hazard (?<level>\\d+)\\)".toRegex(RegexOption.IGNORE_CASE)

/**
 * All Weather Events results from the roll table should include a "(Hazard X)"
 * text part where X is the level of the Hazard
 */
private fun parseHazardLevel(eventName: String): Int? =
    hazardLevelRegex.find(eventName)?.let {
        it.groups["level"]?.value?.toIntOrNull()
    }

private suspend fun rollWeatherEvent(
    averagePartyLevel: Int,
    maximumRange: Int,
    rollMode: RollMode,
) {
    val table: RollTable = js("") // TODO
    val draw: RollTableDraw = js("") // TODO
    val text = draw.results.firstOrNull()?.text
    val hazardLevel = text?.let(::parseHazardLevel)
    if (hazardLevel == null) {
        console.error("Can not parse hazard level from weather events table result, add a (Hazard X) part where X is the level of the hazard")
        return
    }
    if (hazardLevel > averagePartyLevel + maximumRange) {
        console.log("Re-Rolling event, level $hazardLevel is more than $maximumRange higher than party level $averagePartyLevel")
        rollWeatherEvent(averagePartyLevel, maximumRange, rollMode)
    } else {
        table.toMessage(
            draw.results,
            TableMessageOptions(roll = draw.roll, messageOptions = recordOf("rollMode" to rollMode))
        )
    }
}

suspend fun rollWeather(
    game: Game,
    month: Month,
    climate: Array<Climate>,
    averagePartyLevel: Int,
    maximumRange: Int,
    rollMode: RollMode,
) {
    climate.find { it.month == month }
        ?.let {
            // 1. roll flat checks
            val checkPrecipitation = d20Check(
                it.precipitationDc,
                flavor = "Checking for Precipitation with DC ${it.precipitationDc}",
                rollMode = rollMode,
            )
            val checkCold = it.coldDc?.let { coldDc ->
                d20Check(
                    coldDc,
                    flavor = "Checking for Cold with DC $coldDc",
                    rollMode = rollMode,
                )
            }
            // 2. check if weather events happen
            val checkEvent = d20Check(
                it.weatherEventDc,
                flavor = "Checking for Weather Event with DC ${it.weatherEventDc}",
                rollMode = rollMode,
            )
            val checkSecondEvent = checkEvent.dieValue
                .takeIf(DieValue::isNat20)
                ?.run {
                    d20Check(
                        it.weatherEventDc,
                        flavor = "Checking for Second Weather Event with DC ${it.weatherEventDc}",
                        rollMode = rollMode,
                    )
                }
            // 3. roll weather events
            if (checkEvent.degreeOfSuccess.succeeded()) rollWeatherEvent(
                averagePartyLevel,
                maximumRange,
                rollMode,
            )
            if (checkSecondEvent?.degreeOfSuccess?.succeeded() == true) rollWeatherEvent(
                averagePartyLevel,
                maximumRange,
                rollMode,
            )
            // 4. post weather result to chat
            val type = findWeatherType(
                isCold = checkCold?.degreeOfSuccess?.succeeded() == true,
                hasPrecipitation = checkPrecipitation.degreeOfSuccess.succeeded(),
            )
            val weatherEffect = when (type) {
                WeatherType.COLD -> {
                    postChatMessage("Weather: Cold")
                    WeatherEffect.SUNNY
                }

                WeatherType.SNOWY -> {
                    postChatMessage("Weather: Cold & Snowing")
                    WeatherEffect.SNOW
                }

                WeatherType.RAINY -> {
                    postChatMessage("Weather: Rainy")
                    WeatherEffect.RAIN
                }

                WeatherType.SUNNY -> {
                    postChatMessage("Weather: Sunny")
                    WeatherEffect.SNOW
                }
            }
            // 5. set new weather
            setWeather(game, weatherEffect)
        }
}