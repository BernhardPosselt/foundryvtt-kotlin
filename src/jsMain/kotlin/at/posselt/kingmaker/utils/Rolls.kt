package at.posselt.kingmaker.utils

import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.data.checks.determineDegreeOfSuccess
import com.foundryvtt.core.Roll
import com.foundryvtt.core.RollMessageOptions
import js.objects.recordOf
import kotlinx.coroutines.await

suspend fun d20Check(
    dc: Int,
    modifier: Int = 0,
    flavor: String? = null,
    rollMode: RollMode = RollMode.PUBLIC,
    toChat: Boolean = true,
): DegreeOfSuccess {
    val d20 = "1d20"
    val formula = if (modifier > 0) "$d20+$modifier" else d20
    val roll = Roll(formula).evaluate().await()
    if (toChat) {
        roll.toMessage(
            recordOf("flavor" to flavor),
            RollMessageOptions(rollMode = rollMode.value)
        ).await()
    }
    return determineDegreeOfSuccess(dc, roll.total, roll.total - modifier)
}