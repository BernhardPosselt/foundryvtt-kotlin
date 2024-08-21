package at.posselt.kingmaker.camping

import at.posselt.kingmaker.actor.resolveAttribute
import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.awaitablePrompt
import at.posselt.kingmaker.camping.dialogs.RegionSetting
import at.posselt.kingmaker.data.actor.*
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.data.checks.getLevelBasedDC
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.fromOrdinal
import at.posselt.kingmaker.slugify
import at.posselt.kingmaker.utils.postChatTemplate
import at.posselt.kingmaker.utils.postDegreeOfSuccess
import com.foundryvtt.pf2e.Dc
import com.foundryvtt.pf2e.PF2ERollOptions
import com.foundryvtt.pf2e.actor.PF2ECreature
import js.array.push
import js.objects.Object
import js.objects.recordOf
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface AskDcData {
    val dc: Int
}

private suspend fun askDc(activity: String): Int {
    return awaitablePrompt<AskDcData, Int>(
        title = "$activity: Select DC",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to Select.dc().toContext()
        ),
    ) {
        it.dc
    }
}

@JsPlainObject
private external interface AskSkillData {
    val skill: String
}

fun PF2ECreature.satisfiesSkillRequirement(
    selectedSkill: String,
    skillRequirements: Array<SkillRequirement>,
): Boolean {
    val requirements = skillRequirements.find { it.skill == selectedSkill }
    return if (requirements == null) {
        true
    } else {
        val attribute = Attribute.fromString(selectedSkill)
        val rank = resolveAttribute(attribute)?.rank ?: 0
        fromCamelCase<Proficiency>(requirements.proficiency)
            ?.let { rank >= it.ordinal }
            ?: false
    }
}

fun PF2ECreature.hasAnyActivitySkill(
    activity: CampingActivityData,
): Boolean =
    findCampingActivitySkills(activity, true)
        .map { Attribute.fromString(it) }
        .any { resolveAttribute(it) != null }

fun PF2ECreature.findCampingActivitySkills(
    activity: CampingActivityData,
    disableSkillRequirements: Boolean,
): List<String> {
    val activitySkills = activity.skills
    val availableSkills = if (activitySkills == "any") {
        Object.keys(skills)
    } else {
        @Suppress("UNCHECKED_CAST")
        activitySkills as Array<String>
    }
    return availableSkills.filter {
        if (disableSkillRequirements) {
            true
        } else {
            satisfiesSkillRequirement(it, activity.skillRequirements)
        }
    }
}

/**
 * @throws Error if a popup asking for a skill or dc is closed
 */
suspend fun PF2ECreature.campingActivityCheck(
    region: RegionSetting,
    activity: CampingActivityData,
    skill: Attribute,
): DegreeOfSuccess? {
    val activityName = activity.name
    val extraRollOptions = arrayOf("action:${activityName.slugify()}")
    val dc = when (val activityDc = activity.dc) {
        "zone" -> region.zoneDc
        "actorLevel" -> getLevelBasedDC(level)
        null -> askDc(activityName)
        is String -> activityDc.toInt()
        else -> activityDc as Int
    }
    val result = performCampingCheck(
        attribute = skill,
        isSecret = activity.isSecret,
        extraRollOptions = extraRollOptions,
        dc = dc,
    )
    if (result != null) {
        val config = when (result) {
            DegreeOfSuccess.CRITICAL_FAILURE -> activity.criticalFailure
            DegreeOfSuccess.FAILURE -> activity.failure
            DegreeOfSuccess.SUCCESS -> activity.success
            DegreeOfSuccess.CRITICAL_SUCCESS -> activity.criticalSuccess
        }
        postDegreeOfSuccess(degreeOfSuccess = result, message = config?.message)
        if (config?.checkRandomEncounter == true) {
            postChatTemplate(
                "chatmessages/random-camping-encounter.hbs",
                rollMode = RollMode.BLINDROLL
            );
        }
    }
    return result
}


private suspend fun PF2ECreature.performCampingCheck(
    attribute: Attribute,
    isSecret: Boolean = false,
    isWatch: Boolean = false,
    extraRollOptions: Array<String> = emptyArray(),
    dc: Int,
): DegreeOfSuccess? {
    val data = PF2ERollOptions(
        rollMode = if (isSecret) "blindroll" else undefined,
        dc = Dc(value = dc),
        extraRollOptions = arrayOf("camping") + extraRollOptions
    )
    if (isWatch) {
        data.extraRollOptions?.push("watch")
    }
    return resolveAttribute(attribute)
        ?.roll(data)
        ?.await()
        ?.let { fromOrdinal<DegreeOfSuccess>(it.degreeOfSuccess) }
}


