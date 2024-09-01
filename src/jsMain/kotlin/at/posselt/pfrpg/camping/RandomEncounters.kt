package at.posselt.pfrpg.camping

import at.posselt.pfrpg.camping.dialogs.RegionSetting
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.data.checks.RollMode
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.utils.*
import com.foundryvtt.core.Game
import com.foundryvtt.core.documents.RollTable
import com.foundryvtt.pf2e.actor.PF2ENpc

suspend fun rollRandomEncounter(
    game: Game,
    actor: PF2ENpc,
    includeFlatCheck: Boolean
) {
    actor.getCamping()?.let { camping ->
        val currentRegion = camping.findCurrentRegion() ?: camping.regionSettings.regions.firstOrNull()
        currentRegion?.let { region ->
            rollRandomEncounter(
                camping = camping,
                includeFlatCheck = includeFlatCheck,
                region = region,
                isDay = game.getPF2EWorldTime().time.isDay(),
            )
        }
    }
}

suspend fun rollRandomEncounter(
    camping: CampingData,
    includeFlatCheck: Boolean,
    region: RegionSetting,
    isDay: Boolean,
): Boolean {
    val table = region.rollTableUuid?.let { fromUuidTypeSafe<RollTable>(it) }
    if (table == null) {
        return false
    }
    val rollMode = fromCamelCase<RollMode>(camping.randomEncounterRollMode) ?: RollMode.GMROLL
    val proxyTable = camping.proxyRandomEncounterTableUuid?.let { fromUuidTypeSafe<RollTable>(it) }
    val dc = region.encounterDc + calculateModifierIncrease(camping, isDay) + camping.encounterModifier
    val rollCheck = if (includeFlatCheck) {
        d20Check(
            dc = dc,
            flavor = "Rolling Random Encounter for terrain ${region.name} with Flat DC $dc",
            rollMode = rollMode,
        ).degreeOfSuccess.succeeded()
    } else {
        true
    }
    return if (rollCheck) {
        val proxyResult = proxyTable?.rollWithDraw(rollMode = rollMode)
            ?.draw
            ?.results
            ?.get(0)
            ?.text
            ?.trim()
            ?: "Creature"
        if (proxyResult == "Creature") {
            table.rollWithDraw(rollMode = rollMode)
        }
        true
    } else {
        false
    }
}

private fun calculateModifierIncrease(camping: CampingData, isDay: Boolean): Int =
    camping.groupActivities().asSequence()
        .filter { it.done() || camping.alwaysPerformActivities.contains(it.data.name) }
        .map { (data, activity) -> calculateModifierIncrease(data, isDay, activity.parseResult()) }
        .sum()


private fun calculateModifierIncrease(
    data: CampingActivityData,
    isDay: Boolean,
    checkResult: DegreeOfSuccess?
): Int {
    val activityMod = data.modifyRandomEncounterDc?.atTime(isDay) ?: 0
    val resultMod = checkResult?.let { result ->
        data.getOutcome(result)
            ?.modifyRandomEncounterDc
            ?.atTime(isDay)
    } ?: 0
    console.log(data.name, activityMod, resultMod)
    return activityMod + resultMod
}
