package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.dialogs.CombatTrack
import at.posselt.kingmaker.combattracks.getCombatTrack
import at.posselt.kingmaker.combattracks.setCombatTrack
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.Game
import com.foundryvtt.core.Ui
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.documents.Playlist
import com.foundryvtt.core.documents.PlaylistSound
import com.foundryvtt.core.documents.Scene
import com.foundryvtt.pf2e.actor.PF2EActor
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@JsPlainObject
private external interface CombatTrackData {
    val playlistUuid: String
    val trackUuid: String?
}

@JsPlainObject
private external interface CombatTrackContext : HandlebarsRenderContext {
    val formRows: Array<FormElementContext>
}

private class CombatTrackApplication(
    private val game: Game,
    private val scene: Scene,
    private val actor: PF2EActor?,
) : FormApp<CombatTrackContext, CombatTrackData>(
    title = "Set Combat Track: ${actor?.name ?: scene.name}",
    template = "components/forms/application-form.hbs",
) {
    var combatTrack: CombatTrack? = if (actor == null) {
        scene.getCombatTrack()
    } else {
        actor.getCombatTrack()
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<CombatTrackContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val playlist = combatTrack?.let { fromUuidTypeSafe<Playlist>(it.playlistUuid) }
        val playlistSound = combatTrack?.trackUuid?.let { fromUuidTypeSafe<PlaylistSound>(it) }
        CombatTrackContext(
            partId = parent.partId,
            formRows = formContext(
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
        )
    }

    override fun onParsedSubmit(value: CombatTrackData): Promise<Void> = buildPromise {
        combatTrack = CombatTrack(playlistUuid = value.playlistUuid, trackUuid = value.trackUuid)
        undefined
    }

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    if (actor == null) {
                        scene.setCombatTrack(combatTrack)
                    } else {
                        actor.setCombatTrack(combatTrack)
                    }
                    close()
                }
            }

            else -> console.log(action)
        }
    }
}

fun combatTrackMacro(game: Game, actor: PF2EActor?) {
    val currentScene = game.scenes.current
    if (currentScene == null) {
        Ui.notifications.error("Can not run macro without a scene")
        return
    }
    CombatTrackApplication(game, currentScene, actor).launch()
}