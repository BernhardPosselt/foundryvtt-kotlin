package at.posselt.kingmaker.migrations

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.actor.npcs
import at.posselt.kingmaker.camping.getCamping
import at.posselt.kingmaker.camping.getCampingActor
import at.posselt.kingmaker.camping.setCamping
import at.posselt.kingmaker.kingdom.getKingdom
import at.posselt.kingmaker.kingdom.setKingdom
import at.posselt.kingmaker.migrations.migrations.*
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.isFirstGM
import at.posselt.kingmaker.utils.toRecord
import com.foundryvtt.core.Game
import com.foundryvtt.core.ui
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.objects.recordOf

private suspend fun createBackups(
    game: Game,
    kingdomActors: List<PF2ENpc>,
    campingActor: PF2ENpc?,
    currentVersion: Int
) {
    val backup = recordOf(
        "version" to currentVersion,
        "camping" to campingActor?.getCamping(),
        "kingdoms" to kingdomActors.map {
            it.id!! to it.getKingdom()
        }.toRecord()
    )
    game.settings.kingmakerTools.setLatestMigrationBackup(
        JSON.stringify(backup)
    )
}

private val migrations = listOf(
    Migration6(),
    Migration7(),
    Migration9(),
    Migration10(),
    Migration11(),
)

val latestMigrationVersion = migrations.maxOfOrNull { it.version }!!

suspend fun Game.migrateKingmakerTools() {
    val currentVersion = settings.kingmakerTools.getSchemaVersion()
    if (currentVersion < 6) {
        ui.notifications.error(
            "${Config.moduleName}: Upgrades from versions prior to 0.12.2 are not supported anymore. " +
                    "Please upgrade to 1.1.1 first"
        )
        return
    }
    if (isFirstGM() && currentVersion < latestMigrationVersion) {
        ui.notifications.info("$Config.moduleName: Running migrations, please do not close the window")

        // create backups
        val kingdomActors = npcs().filter { it.getKingdom() != null }
        val campingActor = getCampingActor()
        createBackups(this, kingdomActors, campingActor, currentVersion)

        migrations.filter { it.version > currentVersion }
            .forEach { migration ->
                campingActor?.let { actor ->
                    actor.getCamping()?.let { camping ->
                        migration.migrateCamping(this, camping)
                        actor.setCamping(camping)
                    }
                }

                kingdomActors.forEach { actor ->
                    actor.getKingdom()?.let { kingdom ->
                        migration.migrateKingdom(this, kingdom)
                        actor.setKingdom(kingdom)
                    }
                }

                migration.migrateOther(this)
            }

        settings.kingmakerTools.setSchemaVersion(latestMigrationVersion)
        ui.notifications.info("Kingdom Building, Camping & Weather: Migration successful")
    }
}