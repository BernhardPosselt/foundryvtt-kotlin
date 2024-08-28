package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.actor.party
import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.forms.CheckboxInput
import at.posselt.kingmaker.app.forms.NumberInput
import at.posselt.kingmaker.app.forms.Section
import at.posselt.kingmaker.app.forms.SectionsContext
import at.posselt.kingmaker.app.forms.Select
import at.posselt.kingmaker.app.forms.SelectOption
import at.posselt.kingmaker.app.forms.formContext
import at.posselt.kingmaker.app.forms.toOption
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.asSequence
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidsOfTypes
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.data.dsl.buildSchema
import com.foundryvtt.core.utils.flattenObject
import com.foundryvtt.pf2e.actor.PF2ECharacter
import com.foundryvtt.pf2e.actor.PF2ELoot
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.actor.PF2EVehicle
import js.array.toTypedArray
import js.core.Void
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise


@JsPlainObject
external interface CampingSettings {
    val gunsToClean: Int
    val restRollMode: String
    val increaseWatchActorNumber: Int
    val actorUuidsNotKeepingWatch: Array<String>
    val huntAndGatherTargetActorUuid: String?
    val proxyRandomEncounterTableUuid: String?
    val randomEncounterRollMode: String
    val ignoreSkillRequirements: Boolean
    val minimumTravelSpeed: Int?
    val minimumSubsistence: Int
    val alwaysPerformActivities: Array<String>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSettingsDataModel(value: AnyObject) : DataModel(value) {
    companion object {
        @Suppress("unused")
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun defineSchema() = buildSchema {
            int("gunsToClean")
            string("restRollMode") {
                choices = RestRollMode.entries.map { it.toCamelCase() }.toTypedArray()
            }
            int("increaseWatchActorNumber")
            stringArray("actorUuidsNotKeepingWatch")
            stringArray("alwaysPerformActivities")
            string("huntAndGatherTargetActorUuid", nullable = true)
            string("proxyRandomEncounterTableUuid", nullable = true)
            string("randomEncounterRollMode")
            boolean("ignoreSkillRequirements")
            int("minimumTravelSpeed")
            int("minimumSubsistence")
        }
    }
}

@JsPlainObject
external interface CampingSettingsContext : HandlebarsRenderContext, SectionsContext {
    val isFormValid: Boolean
}

enum class RestRollMode {
    NONE,
    ONE,
    ONE_EVERY_FOUR_HOURS,
}

private val companionActivities = setOf(
    "Blend Into The Night",
    "Bolster Confidence",
    "Bolster Confidence",
    "Enhance Weapons",
    "Healer's Blessing",
    "Intimidating Posture",
    "Maintain Armor",
    "Set Alarms",
    "Set Traps",
    "Undead Guardians",
    "Water Hazards",
    "Wilderness Survival",
)

@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSettingsApplication(
    private val game: Game,
    private val campingActor: PF2ENpc,
) : FormApp<CampingSettingsContext, CampingSettings>(
    title = "Camping Settings",
    template = "components/forms/application-form.hbs",
    debug = true,
    dataModel = CampingSettingsDataModel::class.js,
) {
    var settings: CampingSettings

    init {
        val camping = campingActor.getCamping()!!
        settings = CampingSettings(
            gunsToClean = camping.gunsToClean,
            restRollMode = camping.restRollMode,
            increaseWatchActorNumber = camping.increaseWatchActorNumber,
            actorUuidsNotKeepingWatch = camping.actorUuidsNotKeepingWatch,
            huntAndGatherTargetActorUuid = camping.huntAndGatherTargetActorUuid,
            proxyRandomEncounterTableUuid = camping.proxyRandomEncounterTableUuid,
            randomEncounterRollMode = camping.randomEncounterRollMode,
            ignoreSkillRequirements = camping.ignoreSkillRequirements,
            minimumTravelSpeed = camping.minimumTravelSpeed,
            minimumSubsistence = camping.cooking.minimumSubsistence,
            alwaysPerformActivities = camping.alwaysPerformActivities,
        )
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<CampingSettingsContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val camping = campingActor.getCamping()!!
        val actors = fromUuidsOfTypes(
            camping.actorUuids,
            PF2ENpc::class,
            PF2ECharacter::class,
            PF2EVehicle::class,
            PF2ELoot::class,
        )
        val huntAndGatherUuids = (actors + listOfNotNull(game.party()))
            .mapNotNull { it.toOption(useUuid = true) }
        val uuidsNotKeepingWatch = setOf(*settings.actorUuidsNotKeepingWatch)
        CampingSettingsContext(
            partId = parent.partId,
            isFormValid = isFormValid,
            sections = formContext(
                Section(
                    legend = "Exploration",
                    formRows = listOf(
                        NumberInput(
                            name = "minimumTravelSpeed",
                            label = "Minimum Travel Speed",
                            value = settings.minimumTravelSpeed ?: 0,
                            help = "If PCs use horses, use 40",
                            stacked = false,
                        ),
                        Select.fromEnum<RollMode>(
                            name = "randomEncounterRollMode",
                            label = "Random Encounter Roll Mode",
                            value = fromCamelCase<RollMode>(settings.randomEncounterRollMode),
                            labelFunction = { it.label },
                            stacked = false,
                        ),
                        Select(
                            name = "proxyRandomEncounterTableUuid",
                            value = settings.proxyRandomEncounterTableUuid,
                            label = "Proxy Random Encounter Table",
                            required = false,
                            options = game.tables.contents.mapNotNull { it.toOption(useUuid = true) },
                            help = "Custom Roll Table; use 'Creature' text result to roll on the default random encounter table",
                            stacked = false,
                        ),
                    )
                ),
                Section(
                    legend = "Activities",
                    formRows = listOf(
                        CheckboxInput(
                            name = "ignoreSkillRequirements",
                            label = "Do not validate activity skill proficiency",
                            value = settings.ignoreSkillRequirements,
                        ),
                        Select(
                            name = "huntAndGatherTargetActorUuid",
                            value = settings.huntAndGatherTargetActorUuid,
                            label = "Add Ingredients from Hunt and Gather to",
                            required = false,
                            options = huntAndGatherUuids,
                            help = "Default is the actor performing the activity",
                            stacked = false,
                        ),
                    )
                ),
                Section(
                    legend = "Always Performed Activities",
                    formRows = companionActivities.map {
                        CheckboxInput(
                            label = it,
                            name = "alwaysPerformActivities.$it",
                            value = settings.alwaysPerformActivities.contains(it),
                            stacked = false,
                            help = "Activity will be hidden from list of activities and will be automatically enabled"
                        )
                    }
                ),
                Section(
                    legend = "Cooking",
                    formRows = listOf(
                        NumberInput(
                            name = "minimumSubsistence",
                            label = "Minimum Subsistence",
                            help = "Reduce ration meal cost by this amount",
                            value = settings.minimumSubsistence,
                            stacked = false,
                        ),
                    )
                ),
                Section(
                    legend = "Resting",
                    formRows = listOf(
                        NumberInput(
                            name = "gunsToClean",
                            label = "Guns To Clean",
                            value = settings.gunsToClean,
                            stacked = false,
                        ),
                        NumberInput(
                            name = "increaseWatchActorNumber",
                            label = "Increase Actors Keeping Watch",
                            value = settings.increaseWatchActorNumber,
                            stacked = false,
                        ),
                        Select.fromEnum<RestRollMode>(
                            name = "restRollMode",
                            label = "Roll Random Encounter During Rest",
                            value = fromCamelCase<RestRollMode>(settings.restRollMode),
                            stacked = false,
                        ),
                        *actors.mapIndexed { index, actor ->
                            CheckboxInput(
                                name = "actorUuidsNotKeepingWatch.${actor.uuid}",
                                label = "Skip Watch: ${actor.name}",
                                value = uuidsNotKeepingWatch.contains(actor.uuid),
                            )
                        }.toTypedArray()
                    )
                ),
            )
        )
    }

    override fun fixObject(value: dynamic) {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val actors = (value["actorUuidsNotKeepingWatch"] ?: jso()) as Record<String, Boolean>
        value["actorUuidsNotKeepingWatch"] = flattenObject(actors).asSequence()
            .filter { it.component2() == true }
            .map { it.component1() }
            .toTypedArray()
        val activities = (value["alwaysPerformActivities"] ?: jso()) as Record<String, Boolean>
        value["alwaysPerformActivities"] = flattenObject(activities).asSequence()
            .filter { it.component2() == true }
            .map { it.component1() }
            .toTypedArray()
    }

    override fun onParsedSubmit(value: CampingSettings): Promise<Void> = buildPromise {
        settings = value
        undefined
    }

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    campingActor.getCamping()?.let { camping ->
                        camping.gunsToClean = settings.gunsToClean
                        camping.restRollMode = settings.restRollMode
                        camping.increaseWatchActorNumber = settings.increaseWatchActorNumber
                        camping.actorUuidsNotKeepingWatch = settings.actorUuidsNotKeepingWatch
                        camping.huntAndGatherTargetActorUuid = settings.huntAndGatherTargetActorUuid
                        camping.proxyRandomEncounterTableUuid = settings.proxyRandomEncounterTableUuid
                        camping.randomEncounterRollMode = settings.randomEncounterRollMode
                        camping.ignoreSkillRequirements = settings.ignoreSkillRequirements
                        camping.minimumTravelSpeed = settings.minimumTravelSpeed
                        camping.cooking.minimumSubsistence = settings.minimumSubsistence
                        camping.alwaysPerformActivities = settings.alwaysPerformActivities
                        campingActor.setCamping(camping)
                    }
                    close()
                }
            }

            else -> console.log(action)
        }
    }
}