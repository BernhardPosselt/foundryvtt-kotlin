package at.posselt.pfrpg.migrations.migrations

import at.posselt.pfrpg.kingdom.KingdomData
import com.foundryvtt.core.Game

class Migration6 : Migration(6) {
    override suspend fun migrateKingdom(game: Game, kingdom: KingdomData) {
        @Suppress("SENSELESS_COMPARISON")
        if (kingdom.homebrewActivities == null) {
            kingdom.homebrewActivities = emptyArray()
        }
    }
}