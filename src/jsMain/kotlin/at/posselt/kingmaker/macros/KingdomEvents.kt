package at.posselt.kingmaker.macros

import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.rollWithCompendiumFallback
import com.foundryvtt.core.Game

suspend fun rollEvent(game: Game, tableName: String) {
    val rollMode = game.settings.kingmakerTools.getKingdomEventRollMode()
    game.rollWithCompendiumFallback(
        tableName = tableName,
        rollMode = rollMode,
    )
}

suspend fun rollKingdomEventMacro(game: Game) {
    val tableName = game.settings.kingmakerTools.getKingdomEventsTable()
    rollEvent(game, tableName)
}