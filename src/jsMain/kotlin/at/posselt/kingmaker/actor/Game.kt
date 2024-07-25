package at.posselt.kingmaker.actor

import at.posselt.kingmaker.data.actor.ActorTypes
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.*
import js.array.toTypedArray


fun Game.playerActorsOfType(types: Set<ActorTypes>): Array<PF2EActor> =
    @Suppress("UNCHECKED_CAST")
    (actors?.contents
        ?.asSequence()
        ?.filter { types.contains(ActorTypes.fromString(it.type)) && it.hasPlayerOwner }
        ?.toTypedArray() as Array<PF2EActor>?)
        ?: emptyArray()

fun Game.partyMembers(): Array<PF2ECharacter> =
    actors?.contents
        ?.asSequence()
        ?.filterIsInstance<PF2EParty>()
        ?.flatMap { it.members.asSequence() }
        ?.toTypedArray()
        ?: emptyArray()

fun Game.playerCharacters(): Array<PF2ECharacter> =
    actors?.contents
        ?.asSequence()
        ?.filterIsInstance<PF2ECharacter>()
        ?.filter { it.hasPlayerOwner }
        ?.toTypedArray()
        ?: emptyArray()

fun Game.playerFamiliars(): Array<PF2EFamiliar> =
    actors?.contents
        ?.asSequence()
        ?.filterIsInstance<PF2EFamiliar>()
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