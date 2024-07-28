package at.posselt.kingmaker.actor

import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.fromOrdinal
import at.posselt.kingmaker.toCamelCase
import com.foundryvtt.pf2e.Dc
import com.foundryvtt.pf2e.PF2ERollOptions
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.toTypedArray
import kotlin.js.Promise

data class ParsedRollResult(
    val degreeOfSuccess: DegreeOfSuccess
)

fun PF2ECharacter.rollCheck(
    attribute: Attribute,
    dc: Int?,
    rollMode: RollMode = RollMode.GMROLL,
): Promise<ParsedRollResult>? = resolveAttribute(attribute)
    ?.let {
        it.roll(PF2ERollOptions(dc = dc?.let { Dc(value = dc) }, rollMode = rollMode.toCamelCase()))
            .then { result -> ParsedRollResult(fromOrdinal<DegreeOfSuccess>(result.degreeOfSuccess)!!) }
    }

fun Array<PF2ECharacter>.rollChecks(
    attribute: Attribute,
    dc: Int?,
    rollMode: RollMode = RollMode.GMROLL,
) = asSequence()
    .mapNotNull { it.rollCheck(attribute, dc, rollMode) }
    .toTypedArray()
