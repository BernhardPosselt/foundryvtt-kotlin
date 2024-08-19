package at.posselt.kingmaker.camping

import at.posselt.kingmaker.utils.bindChatClick
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.Game

fun bindCampingChatEventListeners(game: Game) {
    bindChatClick("km-random-encounter") { ev, elem ->
        game.getCampingActor()?.let { actor ->
            buildPromise {
                rollRandomEncounter(game, actor, true)
            }
        }
    }
}