package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.camping.CampingActivity
import at.posselt.pfrpg.camping.clearMealEffects
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.camping.syncCampingEffects
import at.posselt.pfrpg.data.checks.RollMode
import at.posselt.pfrpg.utils.postChatTemplate
import com.foundryvtt.core.Game
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface SyncActivitiesAction {
    val activities: Array<CampingActivity>
    val rollRandomEncounter: Boolean
    val clearMealEffects: Boolean
}

class SyncActivitiesHandler(
    private val game: Game,
) : ActionHandler("syncActivities") {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        val data = action.data.unsafeCast<SyncActivitiesAction>()
        val camping = game.getCampingActor()?.getCamping()
        if (camping != null) {
            if (data.clearMealEffects) {
                camping.clearMealEffects()
            }
            camping.syncCampingEffects(data.activities)
        }
        if (data.rollRandomEncounter) {
            postChatTemplate(
                "chatmessages/random-camping-encounter.hbs",
                rollMode = RollMode.BLINDROLL
            );
        }
    }
}