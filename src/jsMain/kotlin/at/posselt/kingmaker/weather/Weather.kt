package at.posselt.kingmaker.weather

import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.isFirstGM
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.toCamelCase
import com.foundryvtt.core.Game


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


suspend fun setWeather(game: Game, weatherEffect: WeatherEffect) {
    game.settings.kingmakerTools.setCurrentWeatherFx(weatherEffect.toCamelCase())
    syncWeather(game)
}

