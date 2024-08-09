package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.actor.resolveAttribute
import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.SelectOption
import at.posselt.kingmaker.app.awaitablePrompt
import at.posselt.kingmaker.camping.CampingActivityData
import at.posselt.kingmaker.camping.SkillRequirement
import at.posselt.kingmaker.data.actor.*
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.checks.getLevelBasedDC
import at.posselt.kingmaker.data.regions.Zone
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.fromOrdinal
import at.posselt.kingmaker.slugify
import at.posselt.kingmaker.unslugify
import at.posselt.kingmaker.utils.postDegreeOfSuccess
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.Dc
import com.foundryvtt.pf2e.PF2ERollOptions
import com.foundryvtt.pf2e.actor.PF2ECharacter
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

private suspend fun askSkill(
    activity: String,
    skills: List<String>,
): String {
    return awaitablePrompt<AskSkillData, String>(
        title = "$activity: Select Skill",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to Select(
                label = "Skill",
                name = "skill",
                required = true,
                options = skills.map {
                    SelectOption(label = it.unslugify(), value = it)
                },
            ).toContext()
        ),
    ) {
        it.skill
    }
}

fun PF2ECharacter.satisfiesSkillRequirement(
    skill: String,
    skillRequirements: Array<SkillRequirement>
): Boolean {
    val requirements = skillRequirements
        .find { it.skill == skill }
    val rank = skills[skill]?.rank ?: 0
    return if (requirements == null) {
        true
    } else {
        fromCamelCase<Proficiency>(requirements.proficiency)
            ?.let { rank >= it.ordinal }
            ?: false
    }
}

/**
 * @throws Error if a popup asking for a skill or dc is closed
 */
suspend fun campingActivityCheck(
    game: Game,
    actor: PF2ECharacter,
    zone: Zone,
    activity: CampingActivityData,
    disableSkillRequirements: Boolean,
): DegreeOfSuccess? {
    val activityName = activity.name
    val extraRollOptions = arrayOf("action:${activityName.slugify()}")
    val dc = when (val activityDc = activity.dc) {
        "zone" -> zone.zoneDc
        "actorLevel" -> getLevelBasedDC(actor.level)
        null -> askDc(activityName)
        is String -> activityDc.toInt()
        else -> activityDc as Int
    }
    val activitySkills = activity.skills
    val availableSkills = if (activitySkills == "any") {
        Object.keys(actor.skills)
    } else {
        @Suppress("UNCHECKED_CAST")
        activitySkills as Array<String>
    }
    val skills = availableSkills.filter {
        if (disableSkillRequirements) {
            true
        } else {
            actor.satisfiesSkillRequirement(it, activity.skillRequirements)
        }
    }
    val skill = if (skills.size > 1) {
        askSkill(activityName, skills)
    } else {
        skills.firstOrNull()
    }
    return skill?.let {
        val result = performCampingCheck(
            game = game,
            actor = actor,
            attribute = Attribute.fromString(it),
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
                // TODO: post random encounter check
            }
        }
        result
    }
}


private suspend fun performCampingCheck(
    game: Game,
    actor: PF2ECharacter,
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
    return actor.resolveAttribute(attribute)
        ?.roll(data)
        ?.await()
        ?.let { fromOrdinal<DegreeOfSuccess>(it.degreeOfSuccess) }
}


