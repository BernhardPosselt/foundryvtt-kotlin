package at.posselt.kingmaker.macros

import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.weather.syncWeather
import com.foundryvtt.core.Game

suspend fun toggleWeatherMacro(game: Game) {
    val isEnabled = game.settings.kingmakerTools.getEnableWeather()
    game.settings.kingmakerTools.setEnableWeather(!isEnabled)
    syncWeather(game)
}
