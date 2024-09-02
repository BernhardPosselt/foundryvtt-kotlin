package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.utils.bindChatClick
import at.posselt.pfrpg.utils.buildPromise
import at.posselt.pfrpg.utils.emitPfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import at.posselt.pfrpg.utils.isFirstGM
import at.posselt.pfrpg.utils.postChatMessage
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ECharacter
import org.w3c.dom.get

fun bindCampingChatEventListeners(game: Game) {
    bindChatClick(".km-random-encounter") { ev, elem ->
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
        val message = HuntAndGatherMessage(
            actorUuid = el.dataset["actorUuid"] as String,
            basicIngredients = el.dataset["basicIngredients"]?.toInt() ?: 0,
            specialIngredients = el.dataset["specialIngredients"]?.toInt() ?: 0,
        )
        if (game.isFirstGM()) {
            game.getCampingActor()?.getCamping()?.let { camping ->
                buildPromise {
                    addHuntAndGather(game, camping, message)
                }
            }
        } else {
            game.socket.emitPfrpg2eKingdomCampingWeather(message.toMessage())
        }
    }
}