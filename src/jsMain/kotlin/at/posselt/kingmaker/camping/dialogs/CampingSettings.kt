package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.actor.party
import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.fromCamelCase
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
}

fun CampingSettings.toRecord() = unsafeCast<AnyObject>()

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
                choices = arrayOf("none", "one", "one-every-4-hours")
            }
            int("increaseWatchActorNumber")
            stringArray("actorUuidsNotKeepingWatch")
            string("huntAndGatherTargetActorUuid", nullable = true)
            string("proxyRandomEncounterTableUuid", nullable = true)
            string("randomEncounterRollMode")
            boolean("ignoreSkillRequirements")
            int("minimumTravelSpeed")
        }
    }
}

@JsPlainObject
external interface CampingSettingsContext : HandlebarsRenderContext {
    val sections: Array<SectionContext>
}

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
        console.log(camping)
        CampingSettingsContext(
            partId = parent.partId,
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
                            value = settings.randomEncounterRollMode.let { fromCamelCase<RollMode>(it) },
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
                        Select(
                            name = "restRollMode",
                            label = "Encounters During Rest",
                            value = settings.restRollMode,
                            options = listOf(
                                SelectOption(label = "None", value = "none"),
                                SelectOption(label = "1 Check", value = "one"),
                                SelectOption(label = "1 Check Every 4 Hours", value = "one-every-4-hours"),
                            ),
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
                        campingActor.setCamping(camping)
                    }
                    close()
                }
            }

            else -> console.log(action)
        }
    }
}