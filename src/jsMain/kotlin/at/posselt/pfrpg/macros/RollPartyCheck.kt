package at.posselt.pfrpg.macros

import at.posselt.pfrpg.actor.rollChecks
import at.posselt.pfrpg.app.*
import at.posselt.pfrpg.app.forms.CheckboxInput
import at.posselt.pfrpg.app.forms.Select
import at.posselt.pfrpg.app.forms.SelectOption
import at.posselt.pfrpg.app.forms.formContext
import at.posselt.pfrpg.data.actor.Attribute
import at.posselt.pfrpg.data.actor.Perception
import at.posselt.pfrpg.data.actor.Skill
import at.posselt.pfrpg.data.checks.RollMode
import at.posselt.pfrpg.utils.awaitAll
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