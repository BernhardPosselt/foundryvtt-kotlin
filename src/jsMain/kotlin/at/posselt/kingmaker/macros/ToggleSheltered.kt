package at.posselt.kingmaker.macros

import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.weather.syncWeather
import com.foundryvtt.core.Game

suspend fun toggleShelteredMacro(game: Game) {
    val isEnabled = game.settings.kingmakerTools.getEnableSheltered()
    game.settings.kingmakerTools.setEnableSheltered(!isEnabled)
    syncWeather(game)
}
