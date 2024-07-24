package at.posselt.kingmaker.macros

import at.posselt.kingmaker.RollMode
import at.posselt.kingmaker.actor.rollChecks
import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.data.actor.Skill
import at.posselt.kingmaker.dialog.*
import at.posselt.kingmaker.utils.awaitAll
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.objects.recordOf
import kotlinx.js.JsPlainObject

private val skills = listOf(
    Perception,
    Skill.NATURE,
    Skill.SURVIVAL
)

@JsPlainObject
private external interface FormData {
    val skill: String
    val dc: Int
    val private: Boolean
}

suspend fun rollPartyCheck(players: Array<PF2ECharacter>) {
    prompt<FormData, Unit>(
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                NumberInput(label = "DC", name = "dc"),
                CheckboxInput(label = "Private GM Roll", name = "private"),
                Select(label = "Skill", name = "skill", options = skills.map {
                    SelectOption(label = it.label, value = it.value)
                })
            )
        ),
        title = "Roll Party Skill Check",
        promptType = PromptType.ROLL,
    ) { data ->
        rollChecks(
            actors = players,
            attribute = Attribute.fromString(data.skill),
            dc = data.dc,
            rollMode = if (data.private) RollMode.PRIVATE else RollMode.PUBLIC
        ).awaitAll()
    }
}