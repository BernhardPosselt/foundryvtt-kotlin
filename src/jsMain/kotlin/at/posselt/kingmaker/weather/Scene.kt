package at.posselt.kingmaker.weather

import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.isFirstGM
import at.posselt.kingmaker.settings.KingmakerToolsSettings
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.setAppFlag
import at.posselt.kingmaker.utils.typeSafeUpdate
import com.foundryvtt.core.Game
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.documents.Scene
import com.foundryvtt.core.documents.onPreUpdateScene
import com.foundryvtt.core.documents.onUpdateScene
import com.foundryvtt.core.onUpdateWorldTime
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

fun getCurrentWeatherFx(settings: KingmakerToolsSettings) =
    if (settings.getEnableSheltered()) {
        WeatherEffect.NONE
    } else {
        fromCamelCase<WeatherEffect>(settings.getCurrentWeatherFx()) ?: WeatherEffect.NONE
    }

fun registerWeatherHooks(game: Game) {
    // these hooks are run when the gm switches scenes and ensures that
    // the new scene is synced to the current weather effect and playlist
    val settings = game.settings.kingmakerTools
    // update scene weather
    Hooks.onPreUpdateScene { document, changed, _, _ ->
        val shouldSyncWeather = game.isFirstGM()
                && settings.getEnableWeather()
                && document.getWeatherSettings().syncWeather
                && changed["active"] == true
        if (shouldSyncWeather) {
            val weather = getCurrentWeatherFx(settings)
            changed["weather"] = toSceneWeatherString(weather)
        }
    }
    // update playlist
    Hooks.onUpdateScene { document, changed, _, _ ->
        val shouldSyncWeather = game.isFirstGM()
                && settings.getEnableWeather()
                && settings.getEnableWeatherSoundFx()
                && document.getWeatherSettings().syncWeatherPlaylist
                && changed["active"] == true
        if (shouldSyncWeather) {
            val weather = getCurrentWeatherFx(settings)
            buildPromise {
                changeSoundTo(game, weather)
            }
        }
    }
    Hooks.onUpdateWorldTime { _, deltaInSeconds, _, _ ->
        if (game.settings.kingmakerTools.getAutoRollWeather()) {
            if (dayHasChanged(game, deltaInSeconds)) {
                buildPromise {
                    rollWeather(game)
                }
            }
        }
    }
}

fun getScenesToSyncWeather(game: Game) =
    listOf(game.scenes.current, game.scenes.active)
        .asSequence()
        .filterNotNull()
        .distinctBy(Scene::id)
        .toList()