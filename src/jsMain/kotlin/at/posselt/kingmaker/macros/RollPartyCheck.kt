package at.posselt.kingmaker.macros

import at.posselt.kingmaker.actor.rollChecks
import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.data.actor.Skill
import at.posselt.kingmaker.data.checks.RollMode
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

suspend fun rollPartyCheckMacro(players: Array<PF2ECharacter>) {
    prompt<FormData, Unit>(
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                Select.dc(label = "DC", name = "dc", required = false),
                CheckboxInput(label = "Private GM Roll", name = "private", value = true, required = false),
                Select(label = "Skill", name = "skill", options = skills.map {
                    SelectOption(label = it.label, value = it.value)
                })
            )
        ),
        title = "Roll Party Skill Check",
        promptType = PromptType.ROLL,
    ) { data ->
        players.rollChecks(
            attribute = Attribute.fromString(data.skill),
            dc = data.dc,
            rollMode = if (data.private) RollMode.GMROLL else RollMode.PUBLICROLL
        ).awaitAll()
    }
}