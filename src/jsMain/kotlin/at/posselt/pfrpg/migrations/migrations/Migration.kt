package at.posselt.pfrpg.migrations.migrations

import com.foundryvtt.core.Game

abstract class Migration(
    val version: Int,
) {
    open suspend fun migrateCamping(game: Game, camping: dynamic) {

    }

    open suspend fun migrateKingdom(game: Game, kingdom: dynamic) {

    }

    open suspend fun migrateOther(game: Game) {

    }
}