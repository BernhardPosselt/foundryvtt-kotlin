package at.posselt.pfrpg.app.forms

import at.posselt.pfrpg.app.SkillInputArrayContext
import at.posselt.pfrpg.camping.CampingSkill
import at.posselt.pfrpg.toLabel
import com.foundryvtt.core.AnyObject
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface SkillInputContext {
    val skills: Array<SkillInputArrayContext>
}

class SkillPicker(
    override val label: String = "Skills",
    val context: SkillInputContext,
    val stacked: Boolean = true,
    override val name: String = "",
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() =
        Component(
            label = label,
            templatePartial = "skillPickerInput",
            value = context.unsafeCast<AnyObject>(),
            stacked = false,
            hideLabel = hideLabel,
            help = help,
        ).toContext()
}

fun toSkillContext(skills: Array<CampingSkill>): SkillInputContext {
    val anySkill = skills.find { it.name == "any" }
    return if (anySkill == null) {
        SkillInputContext(
            skills = skills
                .filter { it.validateOnly != true }
                .map {
                    SkillInputArrayContext(
                        label = it.name.toLabel(),
                        proficiency = it.proficiency.toLabel(),
                    )
                }
                .toTypedArray()
        )
    } else {
        SkillInputContext(
            skills = arrayOf(
                SkillInputArrayContext(
                    label = "Any",
                    proficiency = anySkill.proficiency.toLabel(),
                )
            )
        )
    }
}