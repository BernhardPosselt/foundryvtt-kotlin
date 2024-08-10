package at.posselt.kingmaker.camping

import at.posselt.kingmaker.actor.openActor
import at.posselt.kingmaker.actor.party
import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.calculateHexplorationActivities
import at.posselt.kingmaker.camping.dialogs.findCampingActivitySkills
import at.posselt.kingmaker.utils.*
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.documents.onCreateItem
import com.foundryvtt.core.documents.onDeleteItem
import com.foundryvtt.core.documents.onUpdateItem
import com.foundryvtt.core.game
import com.foundryvtt.core.onUpdateWorldTime
import com.foundryvtt.core.ui
import com.foundryvtt.pf2e.actor.*
import com.foundryvtt.pf2e.item.*
import js.array.push
import js.core.Void
import js.objects.recordOf
import kotlinx.datetime.*
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise
import kotlin.reflect.KClass

@JsPlainObject
external interface CampingSheetActor {
    val name: String
    val uuid: String
    val image: String?
    val choseActivity: Boolean
}

@JsPlainObject
external interface CampingSheetActivity {
    val journalUuid: String?
    val actor: CampingSheetActor?
    val name: String
    val degreeOfSuccess: String?
    val locked: Boolean
}

@JsPlainObject
external interface NightModes {
    val retract2: Boolean
    val retract1: Boolean
    val retractHex: Boolean
    val time: Boolean
    val advanceHex: Boolean
    val advance1: Boolean
    val advance2: Boolean
    val rest: Boolean
}

@JsPlainObject
external interface CampingSheetContext {
    val actors: Array<CampingSheetActor>
    val activities: Array<CampingSheetActivity>
    val isDay: Boolean
    val isGM: Boolean
    val time: String
    val terrain: String
    val pxTimeOffset: Int
    val night: NightModes
    val hexplorationActivityDuration: String
    val hexplorationActivitiesAvailable: Int
    val hexplorationActivitiesMax: String
    val adventuringFor: String
    val restDuration: String
    val restDurationLeft: String?
    val encounterDc: Int
    val region: FormElementContext
    val section: String
}

@JsPlainObject
external interface CampingSheetFormData {
    val region: String
}


private fun isNightMode(
    now: LocalTime,
    visibleAfter: String,
    visibleBefore: String,
): Boolean {
    val start = LocalTime.fromDateInputString(visibleAfter)
    val end = LocalTime.fromDateInputString(visibleBefore)
    return if (start > end) {
        !((now < end) || (now > start))
    } else {
        !((start < now) && (now < end))
    }
}

private fun calculateNightModes(time: LocalTime): NightModes {
    return NightModes(
        retract2 = isNightMode(time, "10:00", "23:00"),
        retract1 = isNightMode(time, "09:00", "22:00"),
        retractHex = isNightMode(time, "08:00", "21:00"),
        time = isNightMode(time, "06:00", "19:00"),
        advanceHex = isNightMode(time, "04:00", "17:00"),
        advance1 = isNightMode(time, "03:00", "16:00"),
        advance2 = isNightMode(time, "02:00", "15:00"),
        rest = isNightMode(time, "19:00", "08:00"),
    )
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSheet(
    private val actor: PF2ENpc,
) : FormApp<CampingSheetContext, CampingSheetFormData>(
    title = "Camping",
    template = "applications/camping/camping-sheet.hbs",
    width = 970,
    classes = arrayOf("km-camping-sheet"),
    controls = arrayOf(
        MenuControl(label = "Show Players", action = "show-players"),
        MenuControl(label = "Activities", action = "configure-activities"),  // TODO
        MenuControl(label = "Recipes", action = "configure-recipes"),  // TODO
        MenuControl(label = "Settings", action = "settings"),  // TODO
        MenuControl(label = "Help", action = "help"),
    ),
    scrollable = arrayOf("#km-camping-content", ".km-camping-actors")
) {
    private val allowedActorTypes = arrayOf(
        PF2ENpc::class,
        PF2ECharacter::class,
        PF2EVehicle::class,
        PF2ELoot::class,
    )
    private val allowedActivityActorTypes: Array<KClass<out PF2EActor>> = arrayOf(
        PF2ENpc::class,
        PF2ECharacter::class,
    )
    private val allowedDnDItems = arrayOf(
        PF2EAction::class,
        PF2ECampaignFeature::class,
        PF2ECondition::class,
        PF2EConsumable::class,
        PF2EEffect::class,
        PF2EEquipment::class,
        PF2EAffliction::class,
        PF2EWeapon::class,
        PF2EArmor::class,
        PF2EShield::class,
        PF2ETreasure::class,
        PF2EBackpack::class,
    )

    init {
        actor.apps[id] = this
        onDocumentRefDragstart(".km-camping-actor")
        onDocumentRefDrop(".km-camping-add-actor") { _, documentRef ->
            if (documentRef is ActorRef) {
                buildPromise {
                    addActor(documentRef.uuid)
                }
            }
        }
        onDocumentRefDrop(
            ".km-camping-actor",
            { it.type == "Item" }
        ) { event, documentRef ->
            buildPromise {
                val target = event.target as HTMLElement
                val tile = target.closest(".km-camping-actor") as HTMLElement?
                val actor = tile?.dataset?.get("uuid")
                    ?.let { fromUuidTypeSafe<PF2EActor>(it) }
                buildPromise {
                    if (actor != null) {
                        addItemToActor(documentRef, actor)
                    }
                }
            }
        }

        onDocumentRefDrop(
            ".km-camping-activity",
            { it.dragstartSelector == ".km-camping-actor" || it.type == "Item" }
        ) { event, documentRef ->
            buildPromise {
                val target = event.target as HTMLElement
                val tile = target.closest(".km-camping-activity") as HTMLElement?
                val actor = tile?.dataset?.get("actorUuid")
                    ?.let { fromUuidTypeSafe<PF2EActor>(it) }
                val activityName = tile?.dataset?.get("activityName")
                buildPromise {
                    if (documentRef is ActorRef && activityName != null) {
                        assignActivityTo(documentRef.uuid, activityName)
                    } else if (actor != null && activityName != null) {
                        addItemToActor(documentRef, actor)
                    }
                }
            }
        }
        appHook.onUpdateWorldTime { _, _, _, _ -> render() }
        appHook.onCreateItem { _, _, _, _ -> render() }
        appHook.onDeleteItem { _, _, _ -> render() }
        appHook.onUpdateItem { _, _, _, _ -> render() }
    }

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "configure-recipes" -> console.log("recipes")
            "configure-activities" -> console.log("activities")
            "settings" -> console.log("settings")
            "rest" -> console.log("resting")
            "roll-camping-check" -> console.log("rolling camping check")
            "next-section" -> console.log("next section")
            "previous-section" -> console.log("previous section")
            "check-encounter" -> console.log("check encounter")
            "roll-encounter" -> console.log("roll encounter")
            "advance-hour" -> advanceHours(target)
            "advance-hexploration" -> advanceHexplorationActivities(target)
            "clear-actor" -> {
                buildPromise {
                    target.dataset["uuid"]?.let { clearActor(it) }
                }
            }

            "clear-activity" -> {
                buildPromise {
                    target.dataset["name"]?.let { clearActivity(it) }
                }
            }

            "show-players" -> buildPromise {
                game.socket.emitKingmakerTools(
                    recordOf(
                        "action" to "openCampingSheet"
                    )
                )
            }

            "open-journal" -> {
                event.preventDefault()
                event.stopPropagation()
                buildPromise {
                    target.dataset["uuid"]?.let { openJournal(it) }
                }
            }

            "help" -> buildPromise {
                openJournal("Compendium.pf2e-kingmaker-tools.kingmaker-tools-journals.JournalEntry.iAQCUYEAq4Dy8uCY.JournalEntryPage.7z4cDr3FMuSy22t1")
            }

            "open-actor" -> {
                event.preventDefault()
                event.stopPropagation()
                buildPromise {
                    target.dataset["uuid"]?.let { openActor(it) }
                }
            }

            "increase-encounter-dc" -> buildPromise {
                target.dataset["value"]?.toInt()?.let { changeEncounterDcModifier(it) }
            }

            "reset-encounter-dc" -> buildPromise {
                changeEncounterDcModifier(null)
            }

            "reset-adventuring-for" -> buildPromise {
                resetAdventuringTimeTracker()
            }
        }
    }


    private suspend fun assignActivityTo(actorUuid: String, activityName: String) {
        actor.getCamping()?.let { camping ->
            val activity = camping.getAllActivities().find { it.name == activityName }

            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
            val activityActor = fromUuidOfTypes(actorUuid, *allowedActivityActorTypes) as PF2ECreature?
            if (activityActor == null) {
                ui.notifications.error("Only NPCs and Characters can perform camping activities")
            } else if (activity == null) {
                ui.notifications.error("Activity with name $activityName not found")
            } else if (activityActor.findCampingActivitySkills(activity, camping.ignoreSkillRequirements).isEmpty()) {
                ui.notifications.error("Actor does not satisfy skill requirements to perform $activityName")
            } else {
                camping.campingActivities =
                    camping.campingActivities.filter { it.activity != activityName }.toTypedArray()
                camping.campingActivities.push(
                    CampingActivity(
                        activity = activityName,
                        actorUuid = actorUuid,
                    )
                )
                actor.setCamping(camping)
            }
        }
    }

    private suspend fun changeEncounterDcModifier(modifier: Int?) {
        actor.getCamping()?.let { camping ->
            if (modifier == null) {
                camping.encounterModifier = 0
            } else {
                camping.encounterModifier += modifier
            }
            actor.setCamping(camping)
        }
    }

    private suspend fun resetAdventuringTimeTracker() {
        actor.getCamping()?.let { camping ->
            camping.dailyPrepsAtTime = game.time.worldTime
            actor.setCamping(camping)
        }
    }

    private suspend fun addActor(uuid: String) {
        actor.getCamping()?.let { camping ->
            if (uuid !in camping.actorUuids) {
                val campingActor = fromUuidOfTypes(uuid, *allowedActorTypes)
                if (campingActor == null) {
                    ui.notifications.error("Only NPCs, Characters, Loot and Vehicles can be added to the camping sheet")
                } else {
                    camping.actorUuids.push(uuid)
                    camping.cooking.actorMeals.push(
                        ActorMeal(
                            actorUuid = uuid,
                            favoriteMeal = null,
                            chosenMeal = "meal",
                        )
                    )
                    actor.setCamping(camping)
                }
            }
        }
    }

    private suspend fun addItemToActor(documentRef: DocumentRef<*>, actor: PF2EActor) {
        val document = documentRef.getDocument()
        if (allowedDnDItems.any { it.isInstance(document) }) {
            actor.addToInventory(document.toObject())
        } else {
            ui.notifications.error("Unsupported Item dragged onto camping actors")
        }
    }

    private suspend fun clearActor(uuid: String) {
        actor.getCamping()?.let {
            it.actorUuids = it.actorUuids.filter { id -> id != uuid }.toTypedArray()
            it.campingActivities = it.campingActivities.filter { a -> a.actorUuid != uuid }.toTypedArray()
            it.cooking.actorMeals = it.cooking.actorMeals.filter { m -> m.actorUuid != uuid }.toTypedArray()
            actor.setCamping(it)
        }
    }

    private suspend fun clearActivity(name: String) {
        actor.getCamping()?.let {
            it.campingActivities
                .find { activity -> activity.activity == name }
                ?.actorUuid = null
            actor.setCamping(it)
        }
    }

    private fun advanceHexplorationActivities(target: HTMLElement) {
        val seconds = getHexplorationActivitySeconds()
        game.time.advance(seconds * (target.dataset["activities"]?.toInt() ?: 0))
    }

    private fun getHexplorationActivitySeconds(): Int =
        ((8 * 3600).toDouble() / getHexplorationActivities()).toInt()

    private fun getHexplorationActivities(): Double {
        val travelSpeed = game.party()?.system?.attributes?.speed?.total ?: 25
        val override = actor.getCamping()?.increaseTravelSpeedByFeet ?: 0
        return calculateHexplorationActivities(travelSpeed + override)
    }

    private fun getHexplorationActivitiesDuration(): String =
        LocalTime.fromSecondOfDay(getHexplorationActivitySeconds()).toDateInputString()

    private fun getHexplorationActivitiesAvailable(camping: CampingData): Int =
        ((8 * 3600 - (game.time.worldTime - camping.dailyPrepsAtTime)) / getHexplorationActivitySeconds())

    private fun getAdventuringFor(camping: CampingData): String {
        val elapsedSeconds = game.time.worldTime - camping.dailyPrepsAtTime
        val isNegative = camping.dailyPrepsAtTime > game.time.worldTime
        return formatSeconds(elapsedSeconds, isNegative)
    }

    private fun getRestDurationLeft(camping: CampingData): String? {
        // TODO
        return null
    }

    fun advanceHours(target: HTMLElement) {
        game.time.advance(3600 * (target.dataset["hours"]?.toInt() ?: 0))
    }

    override fun _preparePartContext(
        partId: String,
        context: CampingSheetContext,
        options: HandlebarsRenderOptions
    ): Promise<CampingSheetContext> = buildPromise {

        val time = game.getPF2EWorldTime().time
        val dayPercentage = time.toSecondOfDay().toFloat() / 86400f
        val pxTimeOffset = -((dayPercentage * 968).toInt() - 968 / 2)

        val camping = actor.getCamping() ?: getDefaultCamping(game)
        val actorsByUuid = fromUuidsOfTypes(camping.actorUuids, *allowedActorTypes).associateBy(PF2EActor::uuid)
        val selectedActivitiesByName = camping.campingActivities.associateBy { it.activity }
        val activities = camping.getAllActivities().map {
            val selectedActivity = selectedActivitiesByName[it.name]
            val actor = selectedActivity?.actorUuid?.let { actorsByUuid[it] }
            CampingSheetActivity(
                journalUuid = it.journalUuid,
                name = it.name,
                degreeOfSuccess = selectedActivity?.result,
                locked = camping.lockedActivities.contains(it.name),
                actor = actor?.let { act ->
                    CampingSheetActor(
                        name = act.name,
                        uuid = act.uuid,
                        image = act.img,
                        choseActivity = true,
                    )
                },
            )
        }.toTypedArray()
        val fullRestDuration = getFullRestDuration(
            watchers = actorsByUuid.values.filter { !camping.actorUuidsNotKeepingWatch.contains(it.uuid) },
            recipes = camping.getAllRecipes().toList(),
            gunsToClean = camping.gunsToClean,
            increaseActorsKeepingWatch = camping.increaseWatchActorNumber,
        )
        val currentRegionName = game.getCurrentRegionName()
        val regions = game.getRegions()
        val currentRegion = currentRegionName?.let { name -> regions.find { it.name == name } }
        val isGM = game.user.isGM
        val section = "Camping Activities"
        CampingSheetContext(
            terrain = currentRegion?.terrain ?: "plains",
            region = Select(
                label = "Region",
                value = currentRegionName,
                options = regions.map {
                    SelectOption(label = it.name, value = it.name)
                },
                required = true,
                name = "region",
                disabled = !isGM
            ).toContext(),
            pxTimeOffset = pxTimeOffset,
            time = time.toDateInputString(),
            isGM = isGM,
            isDay = time.isDay(),
            activities = activities,
            actors = camping.actorUuids.mapNotNull { uuid ->
                actorsByUuid[uuid]?.let { actor ->
                    CampingSheetActor(
                        image = actor.img,
                        uuid = uuid,
                        name = actor.name,
                        choseActivity = section == "Camping Activities" && camping.campingActivities.any {
                            it.actorUuid == uuid
                        },
                    )
                }
            }.toTypedArray(),
            night = calculateNightModes(time),
            hexplorationActivityDuration = getHexplorationActivitiesDuration(),
            hexplorationActivitiesAvailable = getHexplorationActivitiesAvailable(camping),
            hexplorationActivitiesMax = "${getHexplorationActivities()}",
            adventuringFor = getAdventuringFor(camping),
            restDuration = fullRestDuration,
            restDurationLeft = getRestDurationLeft(camping),
            encounterDc = camping.encounterModifier + (currentRegion?.encounterDc ?: 0),
            section = section,
        )
    }

    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> = buildPromise {
        console.log(value)
        actor.getCamping()?.let { camping ->
            camping.currentRegion = value.region
            actor.setCamping(camping)
        }
        undefined
    }
}