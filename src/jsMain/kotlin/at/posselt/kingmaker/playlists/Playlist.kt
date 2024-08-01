package at.posselt.kingmaker.playlists

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.settings.CombatTrack
import com.foundryvtt.core.collections.Playlists

fun Playlists.getCombatOverrideTrack(combatTrackName: String): CombatTrack? =
    getName("Kingmaker.$combatTrackName")?.let {
        CombatTrack(
            playlistUuid = it.uuid,
        )
    }

fun Playlists.getKingmakerCombatTrack(combatTrackName: String): CombatTrack? {
    val playlist = get(Config.kingmakerModule.combat.playlistId)
    return playlist?.sounds?.getName(combatTrackName)?.let {
        CombatTrack(
            playlistUuid = playlist.uuid,
            trackUuid = it.uuid,
        )
    }
}