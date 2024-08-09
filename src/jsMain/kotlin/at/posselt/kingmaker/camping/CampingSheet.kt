package at.posselt.kingmaker.camping

import at.posselt.kingmaker.actor.party
import at.posselt.kingmaker.app.ActorRef
import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.MenuControl
import at.posselt.kingmaker.calculateHexplorationActivities
import at.posselt.kingmaker.utils.*
import com.foundryvtt.core.Game
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.onUpdateWorldTime
import com.foundryvtt.pf2e.actor.*
import js.core.Void
import kotlinx.datetime.*
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@JsPlainObject
external interface CampingSheetActor {
    val name: String
    val uuid: String
    val image: String?
}

@JsPlainObject
external interface CampingSheetActivity {
    val journalUuid: String?
    val actor: CampingSheetActor?
    val name: String
    val degreeOfSuccess: String?
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
}

@JsPlainObject
external interface CampingSheetFormData


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
        rest = isNightMode(time, "20:00", "09:00"),
    )
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSheet(
    private val actor: PF2ENpc,
    private val game: Game,
) : FormApp<CampingSheetContext, CampingSheetFormData>(
    title = "Camping",
    template = "applications/camping/camping-sheet.hbs",
    width = 970,
    resizable = true,
    classes = arrayOf("km-camping-sheet"),
    controls = arrayOf(
        MenuControl(label = "Show Players", action = "show-players"),
        MenuControl(label = "Activities", action = "activities"),
        MenuControl(label = "Recipes", action = "recipes"),
        MenuControl(label = "Settings", action = "settings"),
        MenuControl(label = "Help", action = "help"),
    ),
    scrollable = arrayOf("#km-camping-content", ".km-camping-actors")
) {


    init {
        actor.apps[id] = this
        onDocumentRefDragstart(".km-camping-actor")
        onDocumentRefDrop(".km-camping-add-actor") { _, documentRef ->
            if (documentRef is ActorRef) {
                console.log(documentRef)
            }
        }
        onDocumentRefDrop(
            ".km-camping-activity",
            { it.dragstartSelector == ".km-camping-actor" || it.type == "Item" }
        ) { _, documentRef ->
            console.log(documentRef)
            if (documentRef is ActorRef) {
                console.log(documentRef)
            }
        }
        appHook.onUpdateWorldTime { _, _, _, _ ->
            this.render()
        }
    }

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "advance-hour" -> advanceHours(target)
            "advance-hexploration" -> advanceHexplorationActivities(target)
            "rest" -> console.log("resting!!!")
        }
    }

    private fun advanceHexplorationActivities(target: HTMLElement) {
        val seconds = getHexplorationActivitySeconds()
        game.time.advance(seconds * (target.dataset["activities"]?.toInt() ?: 0))
    }

    private fun getHexplorationActivitySeconds(): Int =
        ((8 * 3600).toDouble() / getHexplorationActivities()).toInt()

    private fun getHexplorationActivities(): Double {
        // TODO: add a setting to override activities gained
        val speed = game.party()?.system?.attributes?.speed?.total ?: 25
        return calculateHexplorationActivities(speed)
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
        val actorsByUuid = fromUuidsOfTypes(
            camping.actorUuids,
            PF2ENpc::class,
            PF2ECharacter::class,
            PF2EVehicle::class,
            PF2ELoot::class,
        ).associateBy(PF2EActor::uuid)
        val selectedActivitiesByName = camping.campingActivities.associateBy { it.activity }
        val activities = camping.getAllActivities().map {
            val selectedActivity = selectedActivitiesByName[it.name]
            val actor = selectedActivity?.actorUuid?.let { actorsByUuid[it] }
            CampingSheetActivity(
                journalUuid = it.journalUuid,
                name = it.name,
                degreeOfSuccess = selectedActivity?.result,
                actor = actor?.let { act ->
                    CampingSheetActor(
                        name = act.name,
                        uuid = act.uuid,
                        image = act.img,
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
        CampingSheetContext(
            terrain = "mountain",
            pxTimeOffset = pxTimeOffset,
            time = time.toDateInputString(),
            isGM = game.user.isGM,
            isDay = time.isDay(),
            activities = activities,
            actors = camping.actorUuids.mapNotNull { uuid ->
                actorsByUuid[uuid]?.let { actor ->
                    CampingSheetActor(
                        image = actor.img,
                        uuid = uuid,
                        name = actor.name,
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
        )
    }

    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> {
        TODO("Not yet implemented")
    }
}