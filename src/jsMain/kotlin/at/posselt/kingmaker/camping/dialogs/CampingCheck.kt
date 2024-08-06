package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.actor.resolveAttribute
import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.prompt
import at.posselt.kingmaker.camping.CampingActivityData
import at.posselt.kingmaker.camping.SkillRequirement
import at.posselt.kingmaker.data.actor.*
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.checks.getLevelBasedDC
import at.posselt.kingmaker.data.regions.Zone
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.fromOrdinal
import at.posselt.kingmaker.slugify
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.Game
import com.foundryvtt.core.ui
import com.foundryvtt.pf2e.Dc
import com.foundryvtt.pf2e.PF2ERollOptions
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.push
import js.objects.recordOf
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

fun actorSatisfiesSkillRequirement(
    actor: PF2ECharacter,
    skill: String,
    skillRequirements: Array<SkillRequirement>
): Boolean {
    val requirements = skillRequirements
        .find { it.skill == skill }
    val rank = actor.skills[skill]?.rank ?: 0
    return if (requirements == null) {
        true
    } else {
        fromCamelCase<Proficiency>(requirements.proficiency)
            ?.let { rank >= it.ordinal }
            ?: false
    }
}

suspend fun campingCheck(
    game: Game,
    actor: PF2ECharacter,
    zone: Zone,
    activity: CampingActivityData,
    disableSkillRequirements: Boolean
) {
    // TODO: ask dc when null
    val skills = activity.skills
        .filter {
            if (disableSkillRequirements) {
                true
            } else {
                actorSatisfiesSkillRequirement(actor, it, activity.skillRequirements)
            }
        }
    val firstSkill = skills.firstOrNull()
    if (firstSkill == null) {
        ui.notifications.error("${actor.name} does not fulfill the proficiency requirements to perform ${activity.name}")
        return
    } else if (skills.size > 1) {
        // TODO: get skill popup
    } else {
        performCampingCheck(
            game = game,
            actor = actor,
            attribute = Attribute.fromString(firstSkill),
            isSecret = activity.isSecret,
            activity = activity,
            zone = zone,
        )
    }
}


@JsPlainObject
external interface AskDcData {
    val dc: Int
}

private suspend fun askDc(reason: String): Int {
    return Promise { resolve, reject ->
        buildPromise {
            prompt<AskDcData, Unit>(
                title = "Provide DC: $reason",
                templatePath = "components/forms/form.hbs",
                templateContext = recordOf(
                    "formRows" to Select.dc().toContext()
                ),
            ) {
                resolve(it.dc)
            }
        }
    }.await()
}

private suspend fun performCampingCheck(
    game: Game,
    actor: PF2ECharacter,
    attribute: Attribute,
    isSecret: Boolean = false,
    isWatch: Boolean = false,
    activity: CampingActivityData? = null,
    zone: Zone,
): DegreeOfSuccess? {
    val dc = when (activity?.dc) {
        "zone" -> zone.zoneDc
        "actorLevel" -> getLevelBasedDC(actor.level)
        null -> askDc(activity?.name ?: attribute.label)
        else -> activity.dc as Int
    }
    val data = PF2ERollOptions(
        rollMode = if (isSecret) "blindroll" else undefined,
        dc = Dc(value = dc),
        extraRollOptions = arrayOf("camping")
    )
    if (activity != null) {
        data.extraRollOptions?.push("action:${activity.name.slugify()}")
    }
    if (isWatch) {
        data.extraRollOptions?.push("watch")
    }
    val result = actor.resolveAttribute(attribute)
        ?.roll(data)
        ?.await()
        ?.let { fromOrdinal<DegreeOfSuccess>(it.degreeOfSuccess) }
    // TODO: post result
    // TODO: check for random encounter
    return result
}


