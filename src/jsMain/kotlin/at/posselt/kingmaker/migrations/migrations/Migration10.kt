package at.posselt.kingmaker.migrations.migrations

import at.posselt.kingmaker.actor.npcs
import at.posselt.kingmaker.camping.CampingData
import at.posselt.kingmaker.camping.dialogs.CombatTrack
import at.posselt.kingmaker.combattracks.getCombatTrack
import at.posselt.kingmaker.combattracks.setCombatTrack
import at.posselt.kingmaker.kingdom.getParsedStructureData
import at.posselt.kingmaker.settings.*
import at.posselt.kingmaker.utils.typeSafeUpdate
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ENpc

private fun migrateCombatTrack(game: Game, combatTrack: dynamic): CombatTrack? {
    val trackName = combatTrack["name"].unsafeCast<String?>()
    return trackName
        ?.let { game.playlists.getName(it) }
        ?.uuid
        ?.let { CombatTrack(playlistUuid = it) }
}

private val structureNamesToMigrate = setOf(
    "Bridge, Stone",
    "Bridge",
    "Gladiatorial Arena",
    "Magical Streetlamps",
    "Paved Streets",
    "Printing House",
    "Sewer System",
    "Wall, Stone",
    "Wall, Wooden",
)

class Migration10 : Migration(10) {
    override suspend fun migrateCamping(game: Game, camping: CampingData) {
        game.settings.registerScalar<String>(
            "proxyEncounterTable",
            default = "",
            name = "ProxyEncounterTable",
        )
        game.settings.registerScalar<String>(
            "randomEncounterRollMode",
            default = "gmroll",
            name = "ProxyEncounterTable",
        )
        camping.randomEncounterRollMode = game.settings.getString("randomEncounterRollMode").ifBlank { "gmroll" }
        val proxyTableName = game.settings.getString("proxyEncounterTable")
        camping.proxyRandomEncounterTableUuid = game.tables.getName(proxyTableName)?.uuid
    }

    override suspend fun migrateOther(game: Game) {
        // migrate combat tracks
        for (scene in game.scenes) {
            scene.getCombatTrack()?.let { track: dynamic ->
                val combatTrack = migrateCombatTrack(game, track)
                scene.setCombatTrack(combatTrack)
            }
        }
        for (actor in game.actors) {
            actor.getCombatTrack()?.let { track: dynamic ->
                val combatTrack = migrateCombatTrack(game, track)
                actor.setCombatTrack(combatTrack)
            }
        }
        // migrate token images
        for (actor in game.npcs()) {
            val data = actor.getParsedStructureData()
            val name = data?.name
            if (name != null && name in structureNamesToMigrate) {
                val path = "modules/pf2e-kingmaker-tools/img/structures/${name}.webp"
                actor.typeSafeUpdate {
                    prototypeToken.texture.src = path
                    img = path
                }
            }
        }
        val tokensToMigrate = game.scenes.contents
            .flatMap { it.tokens.contents.toList() }
            .filter { it.actor is PF2ENpc }
        for (token in tokensToMigrate) {
            val actor = token.actor as PF2ENpc
            val data = actor.getParsedStructureData()
            val name = data?.name
            if (name != null && name in structureNamesToMigrate) {
                token.typeSafeUpdate {
                    texture.src = "modules/pf2e-kingmaker-tools/img/structures/${name}.webp"
                }
            }
        }
    }
}