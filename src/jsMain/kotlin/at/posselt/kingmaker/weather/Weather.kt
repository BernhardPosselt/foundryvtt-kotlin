package at.posselt.kingmaker.weather

import at.posselt.kingmaker.data.regions.WeatherEffect
import at.posselt.kingmaker.isFirstGM
import at.posselt.kingmaker.settings.kingmakerTools
import com.foundryvtt.core.Game
import com.foundryvtt.core.documents.Scene

suspend fun setWeather(game: Game, weatherEffect: WeatherEffect?) {
    console.log(weatherEffect)
}

private fun getSceneWeatherValue(game: Game, scene: Scene) {
    if (scene.getWeatherSettings().syncWeather) {

    }
}

private suspend fun syncSceneWeather(game: Game, scene: Scene) {
//    val effectName = getSceneWeatherValue()
}

suspend fun syncSceneWeatherPlaylist(game: Game, it: Scene) {
    TODO("Not yet implemented")
}

suspend fun syncWeather(game: Game) {
    if (game.isFirstGM() && game.settings.kingmakerTools.getEnableWeather()) {
        val current = game.scenes.current
        val active = game.scenes.active
        if (active != null && current != null && active.id == current.id) {
            syncSceneWeather(game, active)
        } else {
            active?.let { syncSceneWeather(game, it) }
            current?.let { syncSceneWeather(game, it) }
        }
        active?.let { syncSceneWeatherPlaylist(game, it) }
    }
}






