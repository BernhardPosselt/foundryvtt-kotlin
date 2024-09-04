package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.camping.openCampingSheet
import com.foundryvtt.core.Game

class OpenCampingSheetHandler(
    private val game: Game,
) : ActionHandler(
    action = "openCampingSheet",
    mode = ExecutionMode.OTHERS,
) {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        openCampingSheet(game, dispatcher)
    }
}