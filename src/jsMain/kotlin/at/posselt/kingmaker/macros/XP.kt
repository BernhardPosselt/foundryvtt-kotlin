package at.posselt.kingmaker.macros

import at.posselt.kingmaker.awaitAll
import at.posselt.kingmaker.dialog.NumberInput
import at.posselt.kingmaker.dialog.formContext
import at.posselt.kingmaker.dialog.prompt
import at.posselt.kingmaker.postChatMessage
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.objects.recordOf
import kotlinx.js.JsPlainObject

private suspend fun updateXP(players: Array<PF2ECharacter>, amount: Int) {
    players.map {
        val currentXP = it.system.details.xp.value
        val xpThreshold = it.system.details.xp.max
        val currentLevel = it.system.details.level.value
        val addLevels = (currentXP + amount) / xpThreshold
        val xpGain = (currentXP + amount) % xpThreshold
        it.update(
            recordOf(
                "system.details.xp.value" to xpGain,
                "system.details.level.value" to currentLevel + addLevels,
            )
        )
    }.awaitAll()
    postChatMessage("Players gained $amount XP!")
}

@JsPlainObject
external interface XpFormData {
    val amount: Int
}

suspend fun awardXP(players: Array<PF2ECharacter>) {
    prompt<XpFormData, Unit>(
        title = "Award Party XP",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                NumberInput(
                    name = "amount",
                    label = "Amount",
                )
            )
        )
    ) {
        if (it.amount > 0) {
            updateXP(players, it.amount)
        }
    }
}