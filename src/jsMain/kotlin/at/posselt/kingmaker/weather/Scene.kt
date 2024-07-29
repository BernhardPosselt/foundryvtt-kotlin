package at.posselt.kingmaker.weather

import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.setAppFlag
import at.posselt.kingmaker.utils.typeSafeUpdate
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


fun toSceneWeatherString(effect: WeatherEffect) =
    if (effect == WeatherEffect.NONE || effect == WeatherEffect.SUNNY) {
        null
    } else {
        effect.toCamelCase()
    }

suspend fun setSceneWeatherFx(scene: Scene, effect: WeatherEffect) {
    scene.typeSafeUpdate { weather = toSceneWeatherString(effect) }
}

