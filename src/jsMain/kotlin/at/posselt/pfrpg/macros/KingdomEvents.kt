package at.posselt.pfrpg.macros

import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.utils.rollWithCompendiumFallback
import com.foundryvtt.core.Game

suspend fun rollEvent(game: Game, tableName: String) {
    val rollMode = game.settings.pfrpg2eKingdomCampingWeather.getKingdomEventRollMode()
    game.rollWithCompendiumFallback(
        tableName = tableName,
        rollMode = rollMode,
    )
}

suspend fun rollKingdomEventMacro(game: Game) {
    val tableName = game.settings.pfrpg2eKingdomCampingWeather.getKingdomEventsTable()
    rollEvent(game, tableName)
}