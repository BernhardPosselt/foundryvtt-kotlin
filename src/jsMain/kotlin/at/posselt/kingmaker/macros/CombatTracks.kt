package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.combattracks.getCombatTrack
import at.posselt.kingmaker.combattracks.setCombatTrack
import at.posselt.kingmaker.settings.CombatTrack
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import com.foundryvtt.core.Game
import com.foundryvtt.core.Ui
import com.foundryvtt.core.documents.Playlist
import com.foundryvtt.core.documents.PlaylistSound
import com.foundryvtt.pf2e.actor.PF2EActor
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface CombatTrackData {
    val playlistUuid: String
    val trackUuid: String?
}

suspend fun combatTrackMacro(game: Game, actor: PF2EActor?) {
    val currentScene = game.scenes.current
    if (currentScene == null) {
        Ui.notifications.error("Can not run macro without a scene")
        return
    }
    val name = actor?.name ?: currentScene.name
    val track = if (actor == null) {
        currentScene.getCombatTrack()
    } else {
        actor.getCombatTrack()
    }
    val playlist = track?.let { fromUuidTypeSafe<Playlist>(it.playlistUuid) }
    val playlistSound = track?.trackUuid?.let { fromUuidTypeSafe<PlaylistSound>(it) }
    prompt<CombatTrackData, Unit>(
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                Select(
                    required = false,
                    name = "playlistUuid",
                    label = "Playlist",
                    value = playlist?.uuid,
                    options = game.playlists.contents.mapNotNull { it.toOption(useUuid = true) }
                ),
                Select(
                    required = false,
                    name = "trackUuid",
                    label = "Track",
                    value = playlistSound?.uuid,
                    options = playlist?.sounds?.contents?.mapNotNull { it.toOption(useUuid = true) } ?: emptyList()
                )
            )
        ),
        title = "Combat Track: $name",
    ) { data ->
        // TODO: re-render on change
        val combatTrack = CombatTrack(playlistUuid = data.playlistUuid, trackUuid = data.trackUuid)
        if (actor == null) {
            currentScene.setCombatTrack(combatTrack)
        } else {
            actor.setCombatTrack(combatTrack)
        }
    }
}