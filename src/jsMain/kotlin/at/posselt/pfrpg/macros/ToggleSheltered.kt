package at.posselt.pfrpg.macros

import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.weather.syncWeather
import com.foundryvtt.core.Game

suspend fun toggleShelteredMacro(game: Game) {
    val isEnabled = game.settings.pfrpg2eKingdomCampingWeather.getEnableSheltered()
    game.settings.pfrpg2eKingdomCampingWeather.setEnableSheltered(!isEnabled)
    syncWeather(game)
}
