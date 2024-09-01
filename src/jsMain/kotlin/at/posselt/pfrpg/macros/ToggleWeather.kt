package at.posselt.pfrpg.macros

import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.weather.syncWeather
import com.foundryvtt.core.Game

suspend fun toggleWeatherMacro(game: Game) {
    val isEnabled = game.settings.pfrpg2eKingdomCampingWeather.getEnableWeather()
    game.settings.pfrpg2eKingdomCampingWeather.setEnableWeather(!isEnabled)
    syncWeather(game)
}
