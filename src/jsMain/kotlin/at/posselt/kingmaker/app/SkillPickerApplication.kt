package at.posselt.kingmaker.app

import at.posselt.kingmaker.data.actor.Proficiency
import at.posselt.kingmaker.slugify
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.utils.deepClone
import js.array.push
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.Boolean
import kotlin.String
import kotlin.js.Promise

@JsPlainObject
external interface PickerSkill {
    val label: String
    val name: String
    val enabled: Boolean
    val isLore: Boolean
    val proficiency: Proficiency?
    val required: Boolean
    val validateOnly: Boolean
}

@JsPlainObject
external interface SkillContext {
    val cells: Array<FormElementContext>
}


@JsPlainObject
external interface SkillPickerContext : HandlebarsRenderContext {
    val allowLores: Boolean
    val skills: Array<SkillContext>
    val anySkill: SkillContext?
}

@JsPlainObject
external interface SkillPickerSubmitData

@OptIn(ExperimentalJsExport::class)
@JsExport
class SkillPickerApplication(
    skills: Array<PickerSkill>,
    private val allowLores: Boolean = false,
    private val afterSubmit: (skills: Array<PickerSkill>) -> Unit,
) : FormApp<SkillPickerContext, SkillPickerSubmitData>(
    title = "Choose Skills",
    template = "components/skill-picker/skill-picker.hbs",
    width = 600,
    submitOnChange = false,
    debug = true,
    classes = arrayOf("skill-picker"),
) {
    var currentSkills = deepClone(skills)

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "save" -> buildPromise {
                afterSubmit(currentSkills)
                close().await()
            }

            "add-lore" -> buildPromise {
                currentSkills.push(
                    PickerSkill(
                        label = "New Lore",
                        name = "new-lore",
                        enabled = true,
                        isLore = true,
                        proficiency = Proficiency.UNTRAINED,
                        required = false,
                        validateOnly = false,
                    )
                )
                render()
            }

            "delete-lore" -> buildPromise { }
        }
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<SkillPickerContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val anySkill =
            currentSkills.find { it.name == "any" }?.let { toContext(it, hideRequired = true, hideValidateOnly = true) }
        val skills = currentSkills.filter { it.name != "any" }.map { toContext(it) }.toTypedArray()
        SkillPickerContext(
            partId = parent.partId,
            skills = skills,
            anySkill = anySkill,
            allowLores = allowLores,
        )
    }

    private fun toContext(
        skill: PickerSkill,
        hideValidateOnly: Boolean = false,
        hideRequired: Boolean = false,
    ): SkillContext {
        val prefix = if (skill.isLore) {
            formContext(
                CheckboxInput(
                    label = skill.label,
                    name = "${skill.name}.enabled",
                    value = skill.enabled,
                    hideLabel = true,
                    stacked = false,
                ),
                TextInput(
                    label = skill.label,
                    name = "${skill.name}.name",
                    value = skill.label,
                    hideLabel = true,
                    stacked = false,
                )
            )
        } else {
            formContext(
                CheckboxInput(
                    label = skill.label,
                    name = "${skill.name}.enabled",
                    value = skill.enabled,
                    stacked = false,
                ),
                HiddenInput(
                    label = skill.label,
                    name = "${skill.name}.name",
                    value = skill.name,
                )
            )
        }
        return SkillContext(
            cells = prefix + formContext(
                Select.fromEnum<Proficiency>(
                    name = "${skill.name}.proficiency",
                    value = skill.proficiency,
                    hideLabel = true,
                    elementClasses = listOf("km-proficiency"),
                    label = "Proficiency",
                    stacked = false,
                ),
                CheckboxInput(
                    label = "Validate Only",
                    name = "${skill.name}.validateOnly",
                    value = skill.validateOnly,
                    stacked = false,
                    hideLabel = hideValidateOnly,
                    elementClasses = if (hideValidateOnly) listOf("km-hidden") else emptyList(),
                ),
                CheckboxInput(
                    label = "Required",
                    name = "${skill.name}.required",
                    value = skill.required,
                    stacked = false,
                    hideLabel = hideRequired,
                    elementClasses = if (hideRequired) listOf("km-hidden") else emptyList(),
                ),
            )
        )
    }

    override fun onParsedSubmit(value: SkillPickerSubmitData): Promise<Void> = buildPromise {
        // TODO: validate that at least one skill was chosen
//        currentSkills = arrayOf()
        undefined
    }

}