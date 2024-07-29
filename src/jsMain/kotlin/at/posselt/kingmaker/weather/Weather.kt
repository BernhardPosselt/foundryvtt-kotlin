package at.posselt.kingmaker.weather

import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.isFirstGM
import at.posselt.kingmaker.settings.KingmakerToolsSettings
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.Game
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.documents.Scene
import com.foundryvtt.core.documents.onPreUpdateScene
import com.foundryvtt.core.documents.onUpdateScene


private fun getScenesToSyncWeather(game: Game) =
    listOf(game.scenes.current, game.scenes.active)
        .asSequence()
        .filterNotNull()
        .distinctBy(Scene::id)
        .toList()

/**
 * Read the persisted weather effect name and sound and apply them
 */
suspend fun syncWeather(game: Game) {
    val settings = game.settings.kingmakerTools
    if (game.isFirstGM() && settings.getEnableWeather()) {
        val weather = getCurrentWeatherFx(settings)
        getScenesToSyncWeather(game)
            .filter { it.getWeatherSettings().syncWeather }
            .forEach { setSceneWeatherFx(it, weather) }
        if (settings.getEnableWeatherSoundFx()) {
            game.scenes.active
                ?.takeIf { it.getWeatherSettings().syncWeatherPlaylist }
                ?.let { changeSoundTo(game, weather) }
        }
    }
}

private fun getCurrentWeatherFx(settings: KingmakerToolsSettings) =
    if (settings.getEnableSheltered()) {
        WeatherEffect.NONE
    } else {
        fromCamelCase<WeatherEffect>(settings.getCurrentWeatherFx()) ?: WeatherEffect.NONE
    }

suspend fun setWeather(game: Game, weatherEffect: WeatherEffect) {
    game.settings.kingmakerTools.setCurrentWeatherFx(weatherEffect.toCamelCase())
    syncWeather(game)
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
}
