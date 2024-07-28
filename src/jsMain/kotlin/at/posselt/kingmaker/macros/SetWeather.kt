package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.prompt
import at.posselt.kingmaker.app.toSelect
import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.weather.setWeather
import com.foundryvtt.core.game
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface WeatherEffectData {
    val weather: String
}

suspend fun setWeatherMacro() {
    val currentWeatherEffect = fromCamelCase<WeatherEffect>(game.settings.kingmakerTools.getCurrentWeatherFx())!!
    prompt<WeatherEffectData, Unit>(
        title = "Set Weather",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                currentWeatherEffect.toSelect(
                    name = "weather",
                    label = "Weather",
                    value = currentWeatherEffect,
                )
            )
        )
    ) {
        val effect = it.weather
            .takeIf(String::isNotBlank)
            ?.let { fromCamelCase<WeatherEffect>(it) }
        setWeather(game, effect)
    }
}