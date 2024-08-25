package at.posselt.kingmaker.migrations.migrations

import at.posselt.kingmaker.camping.CampingSkill
import com.foundryvtt.core.Game


class Migration11 : Migration(11) {
    override suspend fun migrateCamping(game: Game, camping: dynamic) {
        val homebrewCampingActivities = camping.homebrewCampingActivities.unsafeCast<Array<dynamic>>()
        val newSkills: Map<String, Array<CampingSkill>> = homebrewCampingActivities.map { activity ->
            val activityName = activity.name as String
            val dc = parseDcValue(activity)
            val dcType = parseDcType(activity)
            if (activity.skills == "any") {
                activityName to arrayOf(
                    CampingSkill(
                        name = "any",
                        proficiency = "untrained",
                        dcType = dcType,
                        dc = dc,
                    )
                )
            } else {
                activityName to activity.skills.map { skill: String ->
                    CampingSkill(
                        name = skill,
                        proficiency = activity.skillRequirements.find { req ->
                            req.skill == skill
                        }?.proficiency ?: "untrained",
                        dcType = dcType,
                        dc = dc,
                    )
                }
            }
        }.toMap()
        camping.homebrewCampingActivities.forEach { activity ->
            activity.skills = newSkills[activity.name]?.takeIf { it.isNotEmpty() } ?: emptyArray<CampingSkill>()
        }
        camping.cooking.minimumSubsistence = 0
    }
}

private fun parseDcValue(activity: dynamic): Int? = when (val activityDc = activity.dc) {
    "zone" -> null
    "actorLevel" -> null
    null -> null
    else -> activityDc as Int
}

private fun parseDcType(activity: dynamic): String = when (activity.dc) {
    "zone" -> "zone"
    "actorLevel" -> "actorLevel"
    null -> "none"
    else -> "static"
}