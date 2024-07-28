package at.posselt.kingmaker.weather

import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.setAppFlag
import com.foundryvtt.core.documents.Scene
import kotlinx.js.JsPlainObject

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