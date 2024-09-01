package at.posselt.pfrpg.camping

import at.posselt.pfrpg.utils.bindChatClick
import at.posselt.pfrpg.utils.buildPromise
import at.posselt.pfrpg.utils.emitPfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.utils.isFirstGM
import com.foundryvtt.core.Game
import org.w3c.dom.get

fun bindCampingChatEventListeners(game: Game) {
    bindChatClick(".km-random-encounter") { ev, elem ->
        game.getCampingActor()?.let { actor ->
            buildPromise {
                rollRandomEncounter(game, actor, true)
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