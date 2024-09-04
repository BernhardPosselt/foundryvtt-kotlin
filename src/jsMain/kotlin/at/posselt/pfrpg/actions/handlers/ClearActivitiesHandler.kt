package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.camping.clearCampingEffects
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import com.foundryvtt.core.Game

class ClearActivitiesHandler(
    private val game: Game,
) : ActionHandler("clearActivities") {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        game.getCampingActor()
            ?.getCamping()
            ?.clearCampingEffects()
    }
}