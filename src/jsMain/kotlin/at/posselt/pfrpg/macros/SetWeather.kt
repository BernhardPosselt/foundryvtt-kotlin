package at.posselt.pfrpg.macros

import at.posselt.pfrpg.app.forms.Select
import at.posselt.pfrpg.app.forms.formContext
import at.posselt.pfrpg.app.prompt
import at.posselt.pfrpg.data.regions.WeatherEffect
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.weather.setWeather
import com.foundryvtt.core.Game
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface WeatherEffectData {
    val weather: String
}

suspend fun setWeatherMacro(game: Game) {
    val currentWeatherEffect =
        fromCamelCase<WeatherEffect>(game.settings.pfrpg2eKingdomCampingWeather.getCurrentWeatherFx())
            ?: WeatherEffect.NONE
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