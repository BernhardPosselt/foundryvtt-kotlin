package at.posselt.kingmaker.macros

import at.posselt.kingmaker.settings.kingmakerTools
import com.foundryvtt.core.Game

suspend fun toggleCombatTracksMacro(game: Game) {
    val settings = game.settings.kingmakerTools
    settings.setEnableCombatTracks(!settings.getEnableCombatTracks())
}