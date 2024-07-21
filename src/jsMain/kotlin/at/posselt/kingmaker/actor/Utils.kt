package at.posselt.kingmaker.actor

import com.foundryvtt.core.Actor
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2EArmy
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.toTypedArray
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <D> isPF2EActor(actor: Actor<D>): Boolean {
    contract {
        returns(true) implies (actor is PF2EActor)
    }
    return actorTypes.contains(actor.type)
}

private val actorTypes = ActorTypes.entries
    .map { it.type }
    .toSet()

enum class ActorTypes(val type: String) {
    NPC("npc"),
    CHARACTER("character"),
    ARMY("army"),
    FAMILIAR("familiar"),
    LOOT("loot"),
    HAZARD("hazard"),
    VEHICLE("vehicle"),
    PARTY("party");

    companion object {
        fun fromString(type: String) = when (type) {
            "npc" -> NPC
            "character" -> CHARACTER
            "army" -> ARMY
            "familiar" -> FAMILIAR
            "loot" -> LOOT
            "hazard" -> HAZARD
            "vehicle" -> VEHICLE
            "party" -> PARTY
            else -> throw IllegalArgumentException("unknown actor type: $type")
        }
    }
}

fun Game.playerActorsOfType(types: Set<ActorTypes>): Array<PF2EActor<*>> =
    @Suppress("UNCHECKED_CAST")
    (actors?.contents
        ?.asSequence()
        ?.filter { types.contains(ActorTypes.fromString(it.type)) && it.hasPlayerOwner }
        ?.toTypedArray() as Array<PF2EActor<*>>?)
        ?: emptyArray()

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