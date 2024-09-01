package at.posselt.pfrpg.combattracks

import at.posselt.pfrpg.camping.dialogs.CombatTrack
import at.posselt.pfrpg.camping.findCurrentRegion
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.utils.*
import com.foundryvtt.core.Actor
import com.foundryvtt.core.Game
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.documents.*
import com.foundryvtt.pf2e.actor.PF2EActor
import kotlinx.coroutines.await

fun Actor.getCombatTrack(): CombatTrack? =
    getAppFlag("combat-track")

suspend fun Actor.setCombatTrack(track: CombatTrack?) {
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

fun Game.findCombatTrack(combatants: Array<Combatant>, active: Scene): CombatTrack? =
    // check for actor overrides
    combatants.asSequence()
        .mapNotNull(Combatant::actor)
        .filterIsInstance<PF2EActor>()
        .mapNotNull(PF2EActor::getCombatTrack)
        .firstOrNull()
        ?: active.getCombatTrack()  // or scene overrides
        ?: getCampingActor()?.getCamping()?.findCurrentRegion()?.combatTrack // otherwise fall back to region

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
                if (game.settings.pfrpg2eKingdomCampingWeather.getEnableCombatTracks() && active != null) {
                    game.startCombatTrack(document.combatants.contents, active)
                }
            }
        }
    }
    Hooks.onDeleteCombat { document, _, _ ->
        buildPromise {
            val active = game.scenes.active
            if (game.settings.pfrpg2eKingdomCampingWeather.getEnableCombatTracks() && active != null) {
                game.stopCombatTrack(document.combatants.contents, active)
            }
        }
    }
}
