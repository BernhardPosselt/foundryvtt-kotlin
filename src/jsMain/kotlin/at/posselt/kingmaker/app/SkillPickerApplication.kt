package at.posselt.kingmaker.app

import at.posselt.kingmaker.data.actor.Proficiency
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.toLabel
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.utils.deepClone
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@JsPlainObject
external interface Skill {
    val label: String
    val name: String
    val enabled: Boolean
    val isLore: Boolean
    val proficiency: Proficiency?
}

@JsPlainObject
external interface SkillPickerContext : HandlebarsRenderContext {
    val pickProficiency: Boolean
    val allowLores: Boolean
    val skills: Array<Skill>
    val allSkill: Skill?
    val proficiencies: Array<Option>
}

@JsPlainObject
external interface SkillPickerSubmitData

@OptIn(ExperimentalJsExport::class)
@JsExport
class SkillPickerApplication(
    skills: Array<Skill>,
    private val pickProficiency: Boolean = false,
    private val allowLores: Boolean = false,
    private val afterSubmit: (skills: Array<Skill>) -> Unit,
) : FormApp<SkillPickerContext, SkillPickerSubmitData>(
    title = "Choose Skills",
    template = "components/skill-picker/skill-picker.hbs",
    isDialogForm = false,
) {
    var currentSkills = deepClone(skills)

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "save" -> buildPromise {
                afterSubmit(currentSkills)
                close().await()
            }
        }
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<SkillPickerContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        SkillPickerContext(
            partId = parent.partId,
            skills = currentSkills.filter { it.name != "all" }.toTypedArray(),
            allSkill = currentSkills.find { it.name == "all" },
            pickProficiency = pickProficiency,
            proficiencies = Proficiency.values()
                .map { Option(label = it.toLabel(), value = it.toCamelCase()) }
                .toTypedArray(),
            allowLores = allowLores,
        )
    }

    override fun onParsedSubmit(value: SkillPickerSubmitData): Promise<Void> = buildPromise {
        currentSkills = arrayOf()
        undefined
    }

}