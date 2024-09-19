package at.posselt.pfrpg2e.actions.handlers

import at.posselt.pfrpg2e.actions.ActionMessage
import at.posselt.pfrpg2e.actions.ActionDispatcher
import at.posselt.pfrpg2e.camping.openCampingSheet
import com.foundryvtt.core.Game

class OpenCampingSheetHandler(
    private val game: Game,
) : at.posselt.pfrpg2e.actions.handlers.ActionHandler(
    action = "openCampingSheet",
    mode = at.posselt.pfrpg2e.actions.handlers.ExecutionMode.OTHERS,
) {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        openCampingSheet(game, dispatcher)
    }
}