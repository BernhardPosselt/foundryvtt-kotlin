package at.posselt.pfrpg.macros

import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import com.foundryvtt.core.Game

suspend fun toggleCombatTracksMacro(game: Game) {
    val settings = game.settings.pfrpg2eKingdomCampingWeather
    settings.setEnableCombatTracks(!settings.getEnableCombatTracks())
}