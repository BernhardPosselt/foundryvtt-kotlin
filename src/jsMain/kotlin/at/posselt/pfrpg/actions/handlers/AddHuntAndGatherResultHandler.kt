package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.camping.FoodAmount
import at.posselt.pfrpg.camping.HuntAndGatherData
import at.posselt.pfrpg.camping.addFoodToInventory
import at.posselt.pfrpg.camping.findHuntAndGatherTargetActor
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.utils.postChatTemplate
import com.foundryvtt.core.Game
import js.objects.recordOf

class AddHuntAndGatherResultHandler(
    private val game: Game,
) : ActionHandler(
    action = "addHuntAndGatherResult",
) {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        val result = action.data.unsafeCast<HuntAndGatherData>()
        game.getCampingActor()?.getCamping()?.let { camping ->
            findHuntAndGatherTargetActor(game, result.actorUuid, camping)
                ?.let {
                    it.addFoodToInventory(
                        FoodAmount(
                            basicIngredients = result.basicIngredients,
                            specialIngredients = result.specialIngredients,
                        )
                    )
                    postChatTemplate(
                        templatePath = "chatmessages/add-hunt-and-gather.hbs",
                        templateContext = recordOf(
                            "actorName" to it.name,
                            "basicIngredients" to result.basicIngredients,
                            "specialIngredients" to result.specialIngredients,
                        )
                    )
                }
        }
    }
}