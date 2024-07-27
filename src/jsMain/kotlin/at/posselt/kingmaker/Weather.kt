package at.posselt.kingmaker

import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.settings.registerScalar
import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.setAppFlag
import com.foundryvtt.core.Game
import com.foundryvtt.core.documents.Scene
import kotlinx.js.JsPlainObject

suspend fun setWeather(game: Game, weatherEffect: WeatherEffect?) {
    console.log(weatherEffect)
}

@JsPlainObject
external interface SceneWeatherSettings {
    val syncWeather: Boolean
    val syncWeatherPlaylist: Boolean
}

suspend fun Scene.setWeatherSettings(settings: SceneWeatherSettings) {
    setAppFlag("weather", settings)
}

fun Scene.getWeatherSettings() =
    getAppFlag<Scene, SceneWeatherSettings>("weather") ?: SceneWeatherSettings(
        syncWeather = true,
        syncWeatherPlaylist = true,
    )

fun registerWeatherSettings(game: Game) {
    game.settings.registerScalar<String>(
        key = "currentWeatherFx",
        default = "",
        name = "Current Weather Effect",
    )
}