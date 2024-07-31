package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.prompt
import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.weather.setWeather
import com.foundryvtt.core.Game
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface WeatherEffectData {
    val weather: String
}

suspend fun setWeatherMacro(game: Game) {
    val currentWeatherEffect =
        fromCamelCase<WeatherEffect>(game.settings.kingmakerTools.getCurrentWeatherFx()) ?: WeatherEffect.NONE
    prompt<WeatherEffectData, Unit>(
        title = "Set Weather",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                Select.fromEnum<WeatherEffect>(
                    name = "weather",
                    label = "Weather",
                    value = currentWeatherEffect,
                )
            )
        )
    ) {
        val effect = it.weather
            .takeIf(String::isNotBlank)
            ?.let { name -> fromCamelCase<WeatherEffect>(name) }
            ?: WeatherEffect.NONE
        setWeather(game, effect)
    }
}