package at.posselt.kingmaker.actor

import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ECharacter


fun Game.playerCharacters(): List<PF2ECharacter> =
    actors?.contents
        ?.asSequence()
        ?.filterIsInstance<PF2ECharacter>()
        ?.filter { it.hasPlayerOwner }
        ?.toList()
        ?: emptyList()