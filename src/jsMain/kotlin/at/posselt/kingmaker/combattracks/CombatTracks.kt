package at.posselt.kingmaker.combattracks

import at.posselt.kingmaker.camping.findCurrentRegion
import at.posselt.kingmaker.settings.CombatTrack
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.*
import com.foundryvtt.core.Game
import com.foundryvtt.core.Game.settings
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.documents.*
import com.foundryvtt.pf2e.actor.PF2EActor
import kotlinx.coroutines.await

fun PF2EActor.getCombatTrack(): CombatTrack? =
    getAppFlag("combat-track")

suspend fun PF2EActor.setCombatTrack(track: CombatTrack?) {
    setAppFlag("combat-track", track)
}

fun Scene.getCombatTrack(): CombatTrack? =
    getAppFlag("combat-track")

suspend fun Scene.setCombatTrack(track: CombatTrack?) {
    setAppFlag("combat-track", track)
}

suspend fun Scene.stopMusic() {
    playlistSound
        ?.let { sound -> sound.typeSafeUpdate { playing = false } }
        ?: playlist?.stopAll()?.await()
}


suspend fun Scene.startMusic() {
    playlistSound
        ?.let { sound -> sound.typeSafeUpdate { playing = true } }
        ?: playlist?.playAll()?.await()
}

suspend fun Game.findCombatTrack(combatants: Array<Combatant>, active: Scene): CombatTrack? =
    // check for actor overrides
    combatants.asSequence()
        .mapNotNull(Combatant::actor)
        .filterIsInstance<PF2EActor>()
        .mapNotNull(PF2EActor::getCombatTrack)
        .firstOrNull()
        ?: active.getCombatTrack()  // or scene overrides
        ?: findCurrentRegion()?.combatTrack // otherwise fall back to region

suspend fun Game.startCombatTrack(combatants: Array<Combatant>, active: Scene) {
    findCombatTrack(combatants, active)?.let {
        val trackUuid = it.trackUuid
        val playlistUuid = it.playlistUuid
        if (trackUuid != null) {
            scenes.active?.stopMusic()
            fromUuidTypeSafe<PlaylistSound>(trackUuid)
                ?.typeSafeUpdate { playing = true }
        } else {
            scenes.active?.stopMusic()
            fromUuidTypeSafe<Playlist>(playlistUuid)
                ?.playAll()
        }
    }
}

suspend fun Game.stopCombatTrack(combatants: Array<Combatant>, active: Scene) {
    findCombatTrack(combatants, active)?.let {
        val trackUuid = it.trackUuid
        val playlistUuid = it.playlistUuid
        if (trackUuid != null) {
            fromUuidTypeSafe<PlaylistSound>(trackUuid)
                ?.typeSafeUpdate { playing = false }
            scenes.active?.startMusic()
        } else {
            fromUuidTypeSafe<Playlist>(playlistUuid)
                ?.stopAll()
            scenes.active?.startMusic()
        }
    }
}

fun registerCombatTrackHooks(game: Game) {
    Hooks.onPreUpdateCombat { document, changed, _, _ ->
        if (document.round == 0 && changed["round"] == 1) {
            buildPromise {
                val active = game.scenes.active
                if (settings.kingmakerTools.getEnableCombatTracks() && active != null) {
                    game.startCombatTrack(document.combatants.contents, active)
                }
            }
        }
    }
    Hooks.onDeleteCombat { document, _, _ ->
        buildPromise {
            val active = game.scenes.active
            if (settings.kingmakerTools.getEnableCombatTracks() && active != null) {
                game.stopCombatTrack(document.combatants.contents, active)
            }
        }
    }
}
