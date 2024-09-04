package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.camping.CampingSheetSection
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.camping.setCamping
import at.posselt.pfrpg.data.checks.RollMode
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.postChatTemplate
import com.foundryvtt.core.Game
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface SkipActivitiesAction {
    val rollRandomEncounter: Boolean
}

class SkipActivitiesHandler(
    private val game: Game,
) : ActionHandler("skipActivities") {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        val data = action.data.unsafeCast<SkipActivitiesAction>()
        game.getCampingActor()?.let { actor ->
            actor.getCamping()?.let { camping ->
                camping.section = CampingSheetSection.EATING.toCamelCase()
                actor.setCamping(camping)
            }
        }
        if (data.rollRandomEncounter) {
            postChatTemplate(
                "chatmessages/random-camping-encounter.hbs",
                rollMode = RollMode.BLINDROLL
            );
        }

    }
}