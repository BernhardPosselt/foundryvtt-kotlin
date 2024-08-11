package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.actor.party
import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidsOfTypes
import com.foundryvtt.core.Game
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.pf2e.actor.PF2ECharacter
import com.foundryvtt.pf2e.actor.PF2ELoot
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.actor.PF2EVehicle
import js.core.Void
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
    val randomEncounterRollMode: String?
    val ignoreSkillRequirements: Boolean
    val minimumTravelSpeed: Int?
}

@JsPlainObject
external interface CampingSettingsContext {
    val formRows: Array<FormElementContext>
}

class CampingSettingsApplication(
    private val game: Game,
    private val campingActor: PF2ENpc,
) : FormApp<CampingSettingsContext, CampingSettings>(
    title = "Camping Settings",
    template = "components/forms/application-form.hbs",
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
        context: CampingSettingsContext,
        options: HandlebarsRenderOptions
    ): Promise<CampingSettingsContext> = buildPromise {
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
        val uuidsNotKeepingWatch = setOf(*camping.actorUuidsNotKeepingWatch)
        CampingSettingsContext(
            formRows = formContext(
                NumberInput(
                    name = "minimumTravelSpeed",
                    label = "Minimum Travel Speed",
                    value = settings.minimumTravelSpeed ?: 0,
                    help = "If PCs use horses, use 40"
                ),
                NumberInput(
                    name = "increaseWatchActorNumber",
                    label = "Increase Actors Keeping Watch",
                    value = settings.increaseWatchActorNumber,
                ),
                NumberInput(
                    name = "gunsToClean",
                    label = "Guns To Clean",
                    value = settings.gunsToClean,
                ),
                CheckboxInput(
                    name = "ignoreSkillRequirements",
                    label = "Do not validate activity skill proficiency",
                    value = settings.ignoreSkillRequirements,
                ),
                Select(
                    name = "restRollMode",
                    label = "Rest Roll Mode",
                    value = settings.restRollMode,
                    options = listOf(
                        SelectOption(label = "None During Rest", value = "none"),
                        SelectOption(label = "1 Check During Rest", value = "one"),
                        SelectOption(label = "1 Check Every 4 Hours During Rest", value = "one-every-4-hours"),
                    )
                ),
                Select.fromEnum<RollMode>(
                    name = "randomEncounterRollMode",
                    label = "Random Encounter Roll Mode",
                    value = settings.randomEncounterRollMode?.let { fromCamelCase<RollMode>(it) },
                    labelFunction = { it.label }
                ),
                Select(
                    name = "huntAndGatherTargetActorUuid",
                    value = settings.huntAndGatherTargetActorUuid,
                    label = "Add Ingredients from Hunt and Gather to",
                    required = false,
                    options = huntAndGatherUuids,
                    help = "Default is the actor performing the activity",
                ),
                Select(
                    name = "proxyRandomEncounterTableUuid",
                    value = settings.proxyRandomEncounterTableUuid,
                    label = "Proxy Random Encounter Table",
                    required = false,
                    options = game.tables.contents.mapNotNull { it.toOption(useUuid = true) },
                    help = "Custom Roll Table; use 'Creature' text result to roll on the default random encounter table",
                ),
                *actors.mapIndexed { index, actor ->
                    CheckboxInput(
                        name = "actorUuidsNotKeepingWatch.$index",
                        label = "Skip Watch: ${actor.name}",
                        value = uuidsNotKeepingWatch.contains(actor.uuid),
                    )
                }.toTypedArray()
            )
        )
    }

    override fun fixObject(value: dynamic) {
        value["actorUuidsNotKeepingWatch"] = value["actorUuidsNotKeepingWatch"] ?: emptyArray<String>()
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