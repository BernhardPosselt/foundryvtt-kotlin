package at.posselt.kingmaker.actor

import at.posselt.kingmaker.RollMode
import at.posselt.kingmaker.data.actor.Attribute
import com.foundryvtt.pf2e.Dc
import com.foundryvtt.pf2e.PF2ERollData
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.toTypedArray


fun rollCheck(
    actor: PF2ECharacter,
    attribute: Attribute,
    dc: Int?,
    rollMode: RollMode = RollMode.PRIVATE,
) = actor.resolveAttribute(attribute)
    ?.let {
        it.roll(PF2ERollData(dc = dc?.let { Dc(value = dc) }, rollMode = rollMode.value))
    }

fun rollChecks(
    actors: Array<PF2ECharacter>,
    attribute: Attribute,
    dc: Int?,
    rollMode: RollMode = RollMode.PRIVATE,
) = actors.asSequence()
    .mapNotNull { rollCheck(it, attribute, dc, rollMode) }
    .toTypedArray()
