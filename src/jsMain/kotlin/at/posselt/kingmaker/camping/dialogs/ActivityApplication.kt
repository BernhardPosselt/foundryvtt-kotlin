package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.data.dsl.buildSchema
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.item.PF2EEffect
import js.array.push
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise


@JsPlainObject
external interface ActivityContext : SectionsContext, HandlebarsRenderContext {
    val isFormValid: Boolean
}

@JsPlainObject
external interface ActivityOutcomeSubmitData {
    val message: String
    val effectUuids: Array<ActivityEffect>
    val modifyRandomEncounterDc: ModifyEncounterDc
    val checkRandomEncounter: Boolean
}

@JsPlainObject
external interface ActivitySubmitData {
    val name: String
    val journalUuid: String

    //    val skillRequirements: Int
    val dc: String?

    //    val skills: String
    val modifyRandomEncounterDc: ModifyEncounterDc
    val isSecret: Boolean
    val isLocked: Boolean
    val effectUuids: Array<ActivityEffect>
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
            string("dc", nullable = true)
            boolean("isSecret")
            schema("modifyRandomEncounterDc") {
                int("day")
                int("night")
            }
            stringArray("hi") // FIXME
        }
    }
}

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
        val criticalSuccess = createActivityEffectInputs(
            namePrefix = "criticalSuccess",
            outcome = currentActivity?.criticalSuccess,
            allEffects = effects,
        )
        val success = createActivityEffectInputs(
            namePrefix = "success",
            outcome = currentActivity?.success,
            allEffects = effects,
        )
        val failure = createActivityEffectInputs(
            namePrefix = "failure",
            outcome = currentActivity?.failure,
            allEffects = effects,
        )
        val criticalFailure = createActivityEffectInputs(
            namePrefix = "criticalFailure",
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
//                        Select(
//                            label = "Journal",
//                            name = "journalUuid",
//                            value = journalItem?.uuid,
//                            options = effects.mapNotNull { it.toOption(useUuid = true) },
//                            stacked = false,
//                            item = journalItem,
//                        ),
                        // TODO: skills
                        Select(
                            stacked = false,
                            value = currentActivity?.dc?.toString(),
                            label = "DC",
                            name = "dc",
                            required = false,
                            options = listOf(
                                SelectOption(label = "Zone", value = "zone"),
                                SelectOption(label = "Actor Level", value = "actorLevel"),
                            ) + generateSequence(0) { it + 1 }
                                .take(61)
                                .map { SelectOption(it.toString(), it.toString()) }
                                .toList(),
                        ),
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
                        NumberInput(
                            label = "Day: Encounter DC Modifier",
                            name = "modifyRandomEncounterDc.day",
                            help = "Negative values decrease the modifier",
                            value = currentActivity?.modifyRandomEncounterDc?.day ?: 0,
                            stacked = false,
                        ),
                        NumberInput(
                            label = "Night: Encounter DC Modifier",
                            name = "modifyRandomEncounterDc.night",
                            help = "Negative values decrease the modifier",
                            value = currentActivity?.modifyRandomEncounterDc?.night ?: 0,
                            stacked = false,
                        ),
                        // TODO: effects
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
            journalUuid = value.journalUuid,
            skillRequirements = emptyArray(), // TODO
            dc = parseDc(value.dc),
            skills = "", // TODO
            modifyRandomEncounterDc = value.modifyRandomEncounterDc,
            isSecret = value.isSecret,
            isLocked = editActivityLocked ?: false,
            effectUuids = emptyArray(), // TODO
            isHomebrew = true,
            criticalSuccess = parseOutcome(value.criticalSuccess), // TODO
            success = parseOutcome(value.success), // TODO
            failure = parseOutcome(value.failure), // TODO
            criticalFailure = parseOutcome(value.criticalFailure), // TODO
        )
        undefined
    }

}

private suspend fun createActivityEffectInputs(
    namePrefix: String,
    outcome: ActivityOutcome?,
    allEffects: List<PF2EEffect>,
): Array<FormElementContext> {
//    val firstEffect = outcomes?.effects?.firstOrNull()
//    val item = firstEffect?.uuid
//        ?.let { fromUuidTypeSafe<PF2EEffect>(it) }
//        ?: allEffects.firstOrNull()
    return formContext(
        TextInput(
            name = "hi",
            label = "hi",
            value = "",
            stacked = false,
        )
//        Select(
//            label = "Effect",
//            name = "$namePrefix.uuid",
//            options = allEffects.mapNotNull { it.toOption(useUuid = true) },
//            stacked = false,
//            item = item,
//            value = item?.uuid,
//        ),
//        CheckboxInput(
//            label = "Remove after Rest",
//            name = "$namePrefix.removeAfterRest",
//            stacked = false,
//            value = firstEffect?.removeAfterRest ?: false,
//        ),
//        CheckboxInput(
//            label = "Doubles Healing",
//            help = "Double HP regained from resting, does not stack with other effects that double healing",
//            name = "$namePrefix.doublesHealing",
//            value = firstEffect?.doublesHealing ?: false,
//        ),
//        NumberInput(
//            label = "Rest Duration",
//            help = "Seconds to add to an individuals rest duration; can be negative",
//            name = "$namePrefix.changeRestDurationSeconds",
//            stacked = false,
//            value = firstEffect?.changeRestDurationSeconds ?: 0,
//        ),
    )
}

fun parseDc(value: String?) =
    when (value) {
        null -> null
        "zone", "actorLevel" -> value
        else -> value.toInt()
    }

private fun parseOutcome(outcome: ActivityOutcomeSubmitData): ActivityOutcome {
    TODO("Not yet implemented")
}