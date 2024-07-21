package at.posselt.kingmaker.actor

import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2EArmy
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.toTypedArray


fun Game.playerCharacters(): Array<PF2ECharacter> =
    actors?.contents
        ?.asSequence()
        ?.filterIsInstance<PF2ECharacter>()
        ?.filter { it.hasPlayerOwner }
        ?.toTypedArray()
        ?: emptyArray()

fun Game.playerArmies(): Array<PF2EArmy> =
    actors?.contents
        ?.asSequence()
        ?.filterIsInstance<PF2EArmy>()
        ?.filter { it.hasPlayerOwner }
        ?.toTypedArray()
        ?: emptyArray()