package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.data.actor.Lore
import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.data.actor.Proficiency
import at.posselt.kingmaker.data.actor.Skill
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.data.dsl.buildSchema
import com.foundryvtt.core.documents.JournalEntry
import com.foundryvtt.core.documents.JournalEntryPage
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.item.PF2EEffect
import js.array.push
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.String
import kotlin.js.Promise


@JsPlainObject
external interface ActivityContext : SectionsContext, HandlebarsRenderContext {
    val isFormValid: Boolean
}

@JsPlainObject
external interface ActivityOutcomeSubmitData {
    val message: String
    val modifyRandomEncounterDc: ModifyEncounterDc
    val checkRandomEncounter: Boolean
}

@JsPlainObject
external interface ActivitySubmitData {
    val name: String
    val journalUuid: String
    val journalEntryUuid: String?
    val dc: String?
    val modifyRandomEncounterDc: ModifyEncounterDc
    val isSecret: Boolean
    val criticalSuccess: ActivityOutcomeSubmitData
    val success: ActivityOutcomeSubmitData
    val failure: ActivityOutcomeSubmitData
    val criticalFailure: ActivityOutcomeSubmitData
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class ActivityDataModel(value: AnyObject) : DataModel(value) {
    companion object {
        @Suppress("unused")
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun defineSchema() = buildSchema {
            string("name")
            boolean("isSecret")
            string("journalUuid")
            string("journalyEntryUuid", nullable = true)
            schema("modifyRandomEncounterDc") {
                int("day")
                int("night")
            }
            schema("criticalSuccess") {
                string("message")
                boolean("checkRandomEncounter")
                schema("modifyRandomEncounterDc") {
                    int("day")
                    int("night")
                }
            }
            schema("success") {
                string("message")
                boolean("checkRandomEncounter")
                schema("modifyRandomEncounterDc") {
                    int("day")
                    int("night")
                }
            }
            schema("failure") {
                string("message")
                boolean("checkRandomEncounter")
                schema("modifyRandomEncounterDc") {
                    int("day")
                    int("night")
                }
            }
            schema("criticalFailure") {
                string("message")
                boolean("checkRandomEncounter")
                schema("modifyRandomEncounterDc") {
                    int("day")
                    int("night")
                }
            }
        }
    }
}

private fun launchSkillPicker(skills: Array<ParsedCampingSkill>, afterSubmit: (Array<CampingSkill>) -> Unit) {
    val skillsByAttribute = skills.associateBy { it.attribute }
    val loreAttributes = skills.filter { it.attribute is Lore }.map { it.attribute }
    val anySkill = skills.find { it.attribute.value == "any" }?.let {
        PickerSkill(
            label = "Any",
            name = "any",
            enabled = true,
            isLore = false,
            proficiency = it.proficiency,
            required = false,
            validateOnly = false,
            dcType = it.dcType.toCamelCase(),
            dc = it.dc,
        )
    } ?: PickerSkill(
        label = "Any",
        name = "any",
        enabled = false,
        isLore = false,
        proficiency = Proficiency.UNTRAINED,
        required = false,
        validateOnly = false,
        dcType = "zone",
        dc = null,
    )
    val skills = (Skill.entries + Perception + loreAttributes).mapNotNull { attribute ->
        val existingValue = skillsByAttribute[attribute]
        if (existingValue == null) {
            PickerSkill(
                label = attribute.label,
                name = attribute.value,
                enabled = false,
                isLore = attribute is Lore,
                proficiency = Proficiency.UNTRAINED,
                required = false,
                validateOnly = false,
                dcType = "zone",
                dc = null,
            )
        } else {
            PickerSkill(
                label = existingValue.attribute.label,
                name = existingValue.attribute.value,
                enabled = true,
                isLore = attribute is Lore,
                proficiency = existingValue.proficiency,
                required = existingValue.required,
                validateOnly = existingValue.validateOnly,
                dcType = existingValue.dcType.toCamelCase(),
                dc = existingValue.dc,
            )
        }
    }.toTypedArray()
    SkillPickerApplication(
        allowLores = true,
        skills = skills + anySkill,
        dcTypes = DcType.entries.map { it.toCamelCase() }.toTypedArray(),
        afterSubmit = { allSkills ->
            val anySkill = allSkills.find { it.name == "any" && it.enabled }
            val skills = if (anySkill == null) {
                allSkills
                    .filter { it.name == "any" }
                    .map {
                        CampingSkill(
                            name = it.name,
                            proficiency = it.proficiency.toCamelCase(),
                            dcType = it.dcType,
                            dc = it.dc,
                            validateOnly = it.validateOnly,
                            required = it.required,
                        )
                    }
                    .toTypedArray()
            } else {
                arrayOf(
                    CampingSkill(
                        name = "any",
                        proficiency = anySkill.proficiency.toCamelCase(),
                        dcType = anySkill.dcType,
                        dc = anySkill.dc,
                        validateOnly = false,
                        required = false,
                    )
                )
            }
            afterSubmit(skills)
        }
    ).launch()
}

private data class Journals(
    val entry: JournalEntry,
    val page: JournalEntryPage? = null
)

@OptIn(ExperimentalJsExport::class)
@JsExport
class ActivityApplication(
    private val game: Game,
    private val actor: PF2ENpc,
    data: CampingActivityData? = null,
    private val afterSubmit: () -> Unit,
) : FormApp<ActivityContext, ActivitySubmitData>(
    title = if (data == null) "Add Activity" else "Edit Activity: ${data.name}",
    template = "components/forms/application-form.hbs",
    debug = true,
    dataModel = ActivityDataModel::class.js,
) {
    private val editActivityName = data?.name
    private val editActivityLocked = data?.isLocked
    private var currentActivity: CampingActivityData? = data

    init {
        launchSkillPicker(emptyArray()) {
            console.log(it)
        }
    }

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "openDocumentLink" -> buildPromise {
                event.preventDefault()
                event.stopPropagation()
                target.dataset["uuid"]?.let { fromUuidTypeSafe<PF2EEffect>(it)?.sheet?.launch() }
            }

            "save" -> save()
        }
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<ActivityContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val effects = game.items.contents
            .filterIsInstance<PF2EEffect>()
        val journal = currentActivity
            ?.journalUuid
            ?.let {
                when (val journal = fromUuidTypeSafe<JournalEntryPage>(it) ?: fromUuidTypeSafe<JournalEntry>(it)) {
                    is JournalEntryPage -> Journals(journal.parent!!, journal)
                    is JournalEntry -> Journals(journal)
                    else -> game.journal.contents
                        .firstOrNull()
                        ?.let { Journals(it) }
                }
            }

        val criticalSuccess = createActivityEffectInputs(
            namePrefix = "criticalSuccess.",
            outcome = currentActivity?.criticalSuccess,
            allEffects = effects,
        )
        val success = createActivityEffectInputs(
            namePrefix = "success.",
            outcome = currentActivity?.success,
            allEffects = effects,
        )
        val failure = createActivityEffectInputs(
            namePrefix = "failure.",
            outcome = currentActivity?.failure,
            allEffects = effects,
        )
        val criticalFailure = createActivityEffectInputs(
            namePrefix = "criticalFailure.",
            outcome = currentActivity?.criticalFailure,
            allEffects = effects,
        )
        ActivityContext(
            partId = parent.partId,
            isFormValid = isFormValid,
            sections = arrayOf(
                SectionContext(
                    legend = "Basic",
                    formRows = formContext(
                        TextInput(
                            stacked = false,
                            label = "Name",
                            name = "name",
                            disabled = editActivityName != null,
                            value = currentActivity?.name ?: "",
                            required = true,
                            help = "To override an existing activity, use the same name",
                        ),
                        Select(
                            label = "Journal",
                            name = "journalUuid",
                            value = journal?.page?.uuid,
                            options = game.journal.contents.mapNotNull { it.toOption(useUuid = true) },
                            stacked = false,
                        ),
                        Select(
                            label = "JournalEntry",
                            name = "journalEntryUuid",
                            required = false,
                            value = journal?.entry?.uuid,
                            options = journal?.entry?.pages?.contents?.mapNotNull { it.toOption(useUuid = true) }
                                ?: emptyList(),
                            stacked = false,
                        ),
                        // TODO: skills
                        CheckboxInput(
                            label = "Secret Check",
                            name = "isSecret",
                            value = currentActivity?.isSecret ?: false,
                        ),
                    ),
                ),
                SectionContext(
                    legend = "When Performed",
                    formRows = formContext(
                        // TODO: effects
                        *createEncounterModifierInputs(
                            dc = currentActivity?.modifyRandomEncounterDc,
                        )
                    )
                ),
                SectionContext(
                    legend = "Critical Success",
                    formRows = criticalSuccess,
                ),
                SectionContext(
                    legend = "Success",
                    formRows = success,
                ),
                SectionContext(
                    legend = "Failure",
                    formRows = failure,
                ),
                SectionContext(
                    legend = "Critical Failure",
                    formRows = criticalFailure,
                ),
            )
        )
    }


    fun save(): Promise<Void> = buildPromise {
        if (isValid()) {
            actor.getCamping()?.let { camping ->
                currentActivity?.let { data ->
                    camping.homebrewCampingActivities = camping.homebrewCampingActivities
                        .filter { it.name != data.name }
                        .toTypedArray()
                    camping.homebrewCampingActivities.push(data)
                    actor.setCamping(camping)
                    close().await()
                    afterSubmit()
                }
            }
        }
        undefined
    }

    override fun onParsedSubmit(value: ActivitySubmitData): Promise<Void> = buildPromise {
        currentActivity = CampingActivityData(
            name = editActivityName ?: value.name,
            journalUuid = value.journalEntryUuid ?: value.journalUuid,
            skills = emptyArray(), // TODO
            modifyRandomEncounterDc = value.modifyRandomEncounterDc,
            isSecret = value.isSecret,
            isLocked = editActivityLocked == true,
            effectUuids = emptyArray(), // TODO
            isHomebrew = true,
            criticalSuccess = parseOutcome(currentActivity?.criticalSuccess, value.criticalSuccess),
            success = parseOutcome(currentActivity?.success, value.success),
            failure = parseOutcome(currentActivity?.failure, value.failure),
            criticalFailure = parseOutcome(currentActivity?.criticalFailure, value.criticalFailure),
        )
        undefined
    }

}

private fun createEncounterModifierInputs(
    namePrefix: String = "",
    dc: ModifyEncounterDc?,
): Array<NumberInput> {
    return arrayOf(
        NumberInput(
            label = "Day: Encounter DC Modifier",
            name = "${namePrefix}modifyRandomEncounterDc.day",
            help = "Negative values decrease the modifier",
            value = dc?.day ?: 0,
            stacked = false,
        ),
        NumberInput(
            label = "Night: Encounter DC Modifier",
            name = "${namePrefix}modifyRandomEncounterDc.night",
            help = "Negative values decrease the modifier",
            value = dc?.night ?: 0,
            stacked = false,
        ),
    )
}

private fun createActivityEffectInputs(
    namePrefix: String,
    outcome: ActivityOutcome?,
    allEffects: List<PF2EEffect>,
): Array<FormElementContext> {
    return formContext(
        TextArea(
            name = "${namePrefix}message",
            label = "Chat Message",
            value = "",
            stacked = false,
        ),
        CheckboxInput(
            name = "${namePrefix}checkRandomEncounter",
            value = outcome?.checkRandomEncounter == true,
            label = "Random Encounter Check",
        ),
        *createEncounterModifierInputs(namePrefix = namePrefix, dc = outcome?.modifyRandomEncounterDc)
        // TODO effects
    )
}

private fun parseOutcome(
    current: ActivityOutcome?,
    submitted: ActivityOutcomeSubmitData
) = ActivityOutcome(
    message = submitted.message,
    effectUuids = current?.effectUuids ?: emptyArray(),
    modifyRandomEncounterDc = submitted.modifyRandomEncounterDc,
    checkRandomEncounter = submitted.checkRandomEncounter,
)