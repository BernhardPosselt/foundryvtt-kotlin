package at.posselt.pfrpg.migrations.migrations

import at.posselt.pfrpg.camping.CampingData
import at.posselt.pfrpg.kingdom.KingdomData
import com.foundryvtt.core.Game


class Migration11 : Migration(11) {
    override suspend fun migrateCamping(game: Game, camping: CampingData) {
    }

}

