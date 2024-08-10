package at.posselt.kingmaker.camping

import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.settings.RegionSetting
import at.posselt.kingmaker.utils.d20Check
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.rollWithDraw
import com.foundryvtt.core.documents.RollTable

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
    val rollMode = camping.randomEncounterRollMode?.let { fromCamelCase<RollMode>(it) } ?: RollMode.GMROLL
    val proxyTable = camping.proxyRandomEncounterTableUuid?.let { fromUuidTypeSafe<RollTable>(it) }
    val dc = region.encounterDc + calculateModifierIncrease(camping, isDay) + camping.encounterModifier
    val rollCheck = if (includeFlatCheck) {
        d20Check(
            dc = dc,
            flavor = "Rolling Random Encounter for terrain ${region.name} with Flat DC ${dc}",
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
        .filter { (data, activity) -> (data.doesNotRequireACheck() && activity.actorUuid != null) || activity.checkPerformed() }
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
