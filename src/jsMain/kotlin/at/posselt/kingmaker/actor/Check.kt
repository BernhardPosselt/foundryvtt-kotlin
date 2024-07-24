package at.posselt.kingmaker.actor

import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.checks.RollMode
import com.foundryvtt.pf2e.Dc
import com.foundryvtt.pf2e.PF2ERollData
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.toTypedArray


fun PF2ECharacter.rollCheck(
    attribute: Attribute,
    dc: Int?,
    rollMode: RollMode = RollMode.PRIVATE,
) = resolveAttribute(attribute)?.let {
    it.roll(PF2ERollData(dc = dc?.let { Dc(value = dc) }, rollMode = rollMode.value))
}

fun Array<PF2ECharacter>.rollChecks(
    attribute: Attribute,
    dc: Int?,
    rollMode: RollMode = RollMode.PRIVATE,
) = asSequence()
    .mapNotNull { it.rollCheck(attribute, dc, rollMode) }
    .toTypedArray()
