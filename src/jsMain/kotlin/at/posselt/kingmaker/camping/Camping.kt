package at.posselt.kingmaker.camping

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.data.regions.stolenLandsZones
import at.posselt.kingmaker.playlists.getCombatOverrideTrack
import at.posselt.kingmaker.playlists.getKingmakerCombatTrack
import at.posselt.kingmaker.rolltables.findRollTableWithCompendiumFallback
import at.posselt.kingmaker.settings.RegionSetting
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.getAppFlag
import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.deepClone
import com.foundryvtt.pf2e.actor.PF2ENpc
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface Camping {
    val currentRegion: String
}

fun PF2ENpc.getCamping(): Camping? =
    getAppFlag<PF2ENpc, Camping?>("camping-sheet")
        ?.let(::deepClone)

fun Game.getCampingActor(): PF2ENpc? =
    actors.contents
        .filterIsInstance<PF2ENpc>()
        .find { it.name == Config.camping.actorName }

fun Game.getCurrentRegionName() =
    getCampingActor()
        ?.getCamping()
        ?.currentRegion


suspend fun Game.findCurrentRegion(): RegionSetting? {
    val regionName = getCurrentRegionName()
    val kingmakerTools = settings.kingmakerTools
    return if (kingmakerTools.getRegionSettings().useStolenLands) {
        stolenLandsZones
            .find { it.name == regionName }
            ?.let {
                RegionSetting(
                    name = it.name,
                    zoneDc = it.zoneDc,
                    encounterDc = it.encounterDc,
                    level = it.level,
                    rollTableUuid = findRollTableWithCompendiumFallback(
                        tableName = "Zone ${it.level.toString().padStart(2, '0')}: ${it.name}",
                        fallbackName = it.name,
                    )?.uuid,
                    combatTrack = playlists.getCombatOverrideTrack(it.combatTrackName)
                        ?: playlists.getKingmakerCombatTrack(it.combatTrackName),
                )
            }
    } else {
        kingmakerTools.getRegionSettings().regions
            .find { it.name == regionName }
    }
}