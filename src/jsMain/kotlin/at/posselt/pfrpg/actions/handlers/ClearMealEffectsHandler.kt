package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.camping.removeMealEffects
import at.posselt.pfrpg.camping.getActorsInCamp
import at.posselt.pfrpg.camping.getAllRecipes
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import com.foundryvtt.core.Game

class ClearMealEffectsHandler(
    private val game: Game,
) : ActionHandler("clearMealEffects") {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        game.getCampingActor()
            ?.getCamping()
            ?.let {
                removeMealEffects(it.getAllRecipes().toList(), it.getActorsInCamp())
            }
    }
}