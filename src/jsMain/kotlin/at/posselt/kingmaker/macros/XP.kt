package at.posselt.kingmaker.macros

import at.posselt.kingmaker.awaitAll
import at.posselt.kingmaker.dialog.prompt
import at.posselt.kingmaker.postChatMessage
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.objects.Record
import js.objects.recordOf

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

suspend fun awardXP(players: Array<PF2ECharacter>) {
    prompt<Record<String, Int>, Unit>(
        title = "Award Party XP",
        templatePath = "components/km-dialog-form/award-xp.hbs",
    ) {
        val amount = it["amount"] ?: 0
        if (amount > 0) {
            updateXP(players, amount)
        }
    }
}