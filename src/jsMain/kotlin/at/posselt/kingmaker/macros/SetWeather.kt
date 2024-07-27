package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.SelectOption
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.prompt
import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.deCamelCase
import at.posselt.kingmaker.setWeather
import at.posselt.kingmaker.settings.getString
import com.foundryvtt.core.game
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface WeatherEffectData {
    val weather: String
}

suspend fun setWeatherMacro() {
    val currentWeatherEffect = WeatherEffect.fromString(game.settings.getString("currentWeatherFx"))
    val choices = WeatherEffect.entries
        .map { SelectOption(label = it.value.deCamelCase(), value = it.value) }
    prompt<WeatherEffectData, Unit>(
        title = "Set Weather",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                Select(
                    name = "weather",
                    label = "Weather",
                    options = choices,
                    value = currentWeatherEffect.value,
                )
            )
        )
    ) {
        val effect = it.weather
            .takeIf(String::isNotBlank)
            ?.let(WeatherEffect::fromString)
        setWeather(game, effect)
    }
}