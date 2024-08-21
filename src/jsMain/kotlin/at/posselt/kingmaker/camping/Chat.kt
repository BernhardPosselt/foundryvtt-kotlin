package at.posselt.kingmaker.camping

import at.posselt.kingmaker.utils.bindChatClick
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.emitKingmakerTools
import at.posselt.kingmaker.utils.isFirstGM
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
            game.socket.emitKingmakerTools(message.toMessage())
        }
    }
}