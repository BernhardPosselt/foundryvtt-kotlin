package at.posselt.kingmaker.migrations.migrations

import at.posselt.kingmaker.kingdom.KingdomData
import at.posselt.kingmaker.kingdom.Notes
import com.foundryvtt.core.Game

class Migration7 : Migration(7) {
    override suspend fun migrateKingdom(game: Game, kingdom: KingdomData) {
        @Suppress("SENSELESS_COMPARISON")
        if (kingdom.notes == null) {
            kingdom.notes = Notes(
                public = "",
                gm = "",
            )
        }
    }
}