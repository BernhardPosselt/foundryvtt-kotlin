package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.combattracks.CombatTrackPlaylist
import at.posselt.kingmaker.combattracks.getCombatTrack
import at.posselt.kingmaker.combattracks.setCombatTrack
import com.foundryvtt.core.Game
import com.foundryvtt.core.Ui
import com.foundryvtt.pf2e.actor.PF2EActor
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface CombatTrackData {
    val playlist: String
}

suspend fun combatTrackMacro(game: Game, actor: PF2EActor?) {
    val currentScene = game.scenes.current
    if (currentScene == null) {
        Ui.notifications.error("Can not run macro without a scene")
        return
    }
    val name = actor?.name ?: currentScene.name
    val playlistName = if (actor == null) {
        currentScene.getCombatTrack()?.name
    } else {
        actor.getCombatTrack()?.name
    }
    prompt<CombatTrackData, Unit>(
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                Select(
                    required = false,
                    name = "playlist",
                    label = "Playlist",
                    value = playlistName,
                    options = game.playlists.contents.mapNotNull { it.toOption() }
                )
            )
        ),
        title = "Combat Track: $name",
    ) { data ->
        if (actor == null) {
            currentScene.setCombatTrack(CombatTrackPlaylist(name = data.playlist))
        } else {
            actor.setCombatTrack(CombatTrackPlaylist(name = data.playlist))
        }
    }
}