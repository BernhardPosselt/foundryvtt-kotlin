package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.data.checks.RollMode
import at.posselt.pfrpg.utils.bindChatClick
import at.posselt.pfrpg.utils.buildPromise
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import at.posselt.pfrpg.utils.postChatMessage
import at.posselt.pfrpg.utils.postChatTemplate
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.objects.recordOf
import org.w3c.dom.get

suspend fun postPassTimeMessage(message: String, hours: Int) {
    postChatTemplate(
        templatePath = "chatmessages/pass-time.hbs",
        templateContext = recordOf(
            "message" to message,
            "seconds" to hours * 3600,
            "label" to "$hours hours"
        ),
        rollMode = RollMode.GMROLL,
    )
}

fun bindCampingChatEventListeners(game: Game, dispatcher: ActionDispatcher) {
    bindChatClick(".km-pass-time") { _, el ->
        el.dataset["seconds"]?.toInt()?.let {
            game.time.advance(it)
        }
    }
    bindChatClick(".km-random-encounter") { _, _ ->
        game.getCampingActor()?.let { actor ->
            buildPromise {
                rollRandomEncounter(game, actor, true)
            }
        }
    }
    bindChatClick(".gain-provisions") { _, el ->
        buildPromise {
            val actor = el.dataset["actorUuid"]?.let { fromUuidTypeSafe<PF2ECharacter>(it) }
            val quantity = el.dataset["quantity"]?.toInt() ?: 0
            if (quantity > 0 && actor != null) {
                actor.addConsumableToInventory(Config.items.provisionsUuid, quantity)
                postChatMessage("${actor.name}: Adding $quantity provisions")
            }
        }
    }
    bindChatClick(".km-add-food") { _, el ->
        val action = ActionMessage(
            action = "addHuntAndGatherResult",
            data = HuntAndGatherData(
                actorUuid = el.dataset["actorUuid"] as String,
                basicIngredients = el.dataset["basicIngredients"]?.toInt() ?: 0,
                specialIngredients = el.dataset["specialIngredients"]?.toInt() ?: 0,
            ).unsafeCast<AnyObject>()
        )
        buildPromise {
            dispatcher.dispatch(action)
        }
    }
}