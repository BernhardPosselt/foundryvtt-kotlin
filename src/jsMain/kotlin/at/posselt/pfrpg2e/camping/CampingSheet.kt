package at.posselt.pfrpg2e.camping

import at.posselt.pfrpg2e.actor.openActor
import at.posselt.pfrpg2e.actor.party
import at.posselt.pfrpg2e.app.ActorRef
import at.posselt.pfrpg2e.app.DocumentRef
import at.posselt.pfrpg2e.app.FormApp
import at.posselt.pfrpg2e.app.HandlebarsRenderContext
import at.posselt.pfrpg2e.app.forms.FormElementContext
import at.posselt.pfrpg2e.app.MenuControl
import at.posselt.pfrpg2e.app.forms.Select
import at.posselt.pfrpg2e.app.forms.SelectOption
import at.posselt.pfrpg2e.calculateHexplorationActivities
import at.posselt.pfrpg2e.camping.dialogs.CampingSettingsApplication
import at.posselt.pfrpg2e.camping.dialogs.ManageActivitiesApplication
import at.posselt.pfrpg2e.camping.dialogs.ManageRecipesApplication
import at.posselt.pfrpg2e.camping.dialogs.RegionConfig
import at.posselt.pfrpg2e.camping.dialogs.pickSpecialRecipe
import at.posselt.pfrpg2e.data.checks.DegreeOfSuccess
import at.posselt.pfrpg2e.fromCamelCase
import at.posselt.pfrpg2e.takeIfInstance
import at.posselt.pfrpg2e.toCamelCase
import at.posselt.pfrpg2e.toLabel
import at.posselt.pfrpg2e.utils.buildPromise
import at.posselt.pfrpg2e.utils.emitPfrpg2eKingdomCampingWeather
import at.posselt.pfrpg2e.utils.formatSeconds
import at.posselt.pfrpg2e.utils.fromDateInputString
import at.posselt.pfrpg2e.utils.fromUuidTypeSafe
import at.posselt.pfrpg2e.utils.getPF2EWorldTime
import at.posselt.pfrpg2e.utils.isDay
import at.posselt.pfrpg2e.utils.launch
import at.posselt.pfrpg2e.utils.openItem
import at.posselt.pfrpg2e.utils.openJournal
import at.posselt.pfrpg2e.utils.postChatMessage
import at.posselt.pfrpg2e.utils.toDateInputString
import com.foundryvtt.core.Game
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.documents.onCreateItem
import com.foundryvtt.core.documents.onDeleteItem
import com.foundryvtt.core.documents.onUpdateItem
import com.foundryvtt.core.onUpdateWorldTime
import com.foundryvtt.core.ui
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2ECreature
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.item.itemFromUuid
import js.array.push
import js.core.Void
import js.objects.ReadonlyRecord
import js.objects.recordOf
import kotlinx.coroutines.await
import kotlinx.datetime.LocalTime
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.String
import kotlin.js.Promise
import kotlin.math.max


@JsPlainObject
external interface BaseActorContext {
    val name: String
    val uuid: String
    val image: String?
}

@JsPlainObject
external interface CampingSheetActor : BaseActorContext {
    val choseActivity: Boolean
    val degreeOfSuccess: FormElementContext?
}

@JsPlainObject
external interface CampingSheetActivity {
    val journalUuid: String?
    val actor: CampingSheetActor?
    val name: String
    val hidden: Boolean
    val requiresCheck: Boolean
    val skills: FormElementContext?
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
external interface RecipeActorContext : BaseActorContext {
    val chosenMeal: String
    val favoriteMeal: String?
}

@JsPlainObject
external interface RecipeContext {
    val name: String
    val targetRecipe: String
    val cost: FoodCost?
    val uuid: String?
    val icon: String
    val requiresCheck: Boolean
    val hidden: Boolean
    val rations: Boolean
    val actors: Array<RecipeActorContext>
}

@JsPlainObject
external interface CampingSheetContext : HandlebarsRenderContext {
    var actors: Array<CampingSheetActor>
    var prepareCamp: CampingSheetActivity?
    var activities: Array<CampingSheetActivity>
    var isDay: Boolean
    var isGM: Boolean
    var time: String
    var terrain: String
    var pxTimeOffset: Int
    var night: NightModes
    var hexplorationActivityDuration: String
    var hexplorationActivitiesAvailable: Int
    var hexplorationActivitiesMax: String
    var adventuringFor: String
    var restDuration: String
    var restDurationLeft: String?
    var encounterDc: Int
    var region: FormElementContext
    var section: String
    var prepareCampSection: Boolean
    var campingActivitiesSection: Boolean
    var eatingSection: Boolean
    var recipes: Array<RecipeContext>
    var totalFoodCost: FoodCost
    var availableFood: FoodCost
}

@JsPlainObject
external interface CampingSheetActivitiesFormData {
    val degreeOfSuccess: ReadonlyRecord<String, String>
    val selectedSkill: ReadonlyRecord<String, String>
}

@JsPlainObject
external interface CampingSheetFormData {
    val region: String
    val activities: CampingSheetActivitiesFormData
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

private enum class CampingSheetSection {
    PREPARE_CAMP,
    CAMPING_ACTIVITIES,
    EATING,
}

private const val windowWidth = 970


@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("CampingSheet")
class CampingSheet(
    private val game: Game,
    private val actor: PF2ENpc,
) : FormApp<CampingSheetContext, CampingSheetFormData>(
    title = "Camping",
    template = "applications/camping/camping-sheet.hbs",
    width = windowWidth,
    classes = arrayOf("km-camping-sheet"),
    controls = arrayOf(
        MenuControl(label = "Show Players", action = "show-players", gmOnly = true),
        MenuControl(label = "Reset Activities", action = "reset-activities", gmOnly = true),
        MenuControl(label = "Reset Chosen Meals", action = "reset-meals", gmOnly = true),
        MenuControl(label = "Activities", action = "configure-activities", gmOnly = true),
        MenuControl(label = "Recipes", action = "configure-recipes", gmOnly = true),
        MenuControl(label = "Regions", action = "configure-regions", gmOnly = true),
        MenuControl(label = "Settings", action = "settings", gmOnly = true),
        MenuControl(label = "Help", action = "help"),
    ),
    scrollable = arrayOf("#km-camping-content-wrapper", ".km-camping-actors", ".km-recipe-actors"),
    renderOnSubmit = false,
) {
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
        onDocumentRefDrop(
            ".km-camping-recipe",
            { it.dragstartSelector == ".km-camping-actor" }
        ) { event, documentRef ->
            buildPromise {
                val target = event.target as HTMLElement
                val tile = target.closest(".km-camping-recipe") as HTMLElement?
                val recipeName = tile?.dataset?.get("recipeName")
                buildPromise {
                    if (documentRef is ActorRef && recipeName != null) {
                        assignRecipeTo(documentRef.uuid, recipeName)
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
            "configure-regions" -> RegionConfig(actor).launch()
            "configure-recipes" -> ManageRecipesApplication(game, actor).launch()
            "configure-activities" -> ManageActivitiesApplication(game, actor).launch()
            "reset-activities" -> buildPromise { resetActivities() }
            "reset-meals" -> buildPromise { resetMeals() }
            "settings" -> CampingSettingsApplication(game, actor).launch()
            "rest" -> console.log("resting")
            "consume-rations" -> console.log("consuming rations")
            "roll-camping-check" -> buildPromise {
                target.closest(".km-camping-activity")
                    ?.takeIfInstance<HTMLElement>()
                    ?.let { tile ->
                        tile.dataset["activityName"]?.let { activity ->
                            tile.dataset["actorUuid"]?.let { actorUuid ->
                                rollCheck(activity, actorUuid)
                            }
                        }
                    }
            }

            "next-section" -> buildPromise { nextSection() }
            "previous-section" -> buildPromise { previousSection() }
            "check-encounter" -> buildPromise { rollEncounter(includeFlatCheck = true) }
            "roll-encounter" -> buildPromise { rollEncounter(includeFlatCheck = false) }
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
                game.socket.emitPfrpg2eKingdomCampingWeather(
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

            "open-item" -> {
                event.preventDefault()
                event.stopPropagation()
                buildPromise {
                    target.dataset["uuid"]?.let { openItem(it) }
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

    private suspend fun rollCheck(activityName: String, actorUuid: String) {
        val checkActor = getCampingActivityCreatureByUuid(actorUuid)
        checkNotNull(checkActor) { "Could not find camping actor with uuid $actorUuid" }

        val camping = actor.getCamping()
        checkNotNull(camping) { "Could not find camping data on actor ${actor.uuid}" }

        val campingCheckData = checkActor.getCampingCheckData(camping, activityName)
        checkNotNull(campingCheckData) { "Could not resolve skill or region" }

        val activity = campingCheckData.activityData.data

        // preparing check removes all meal effects
        if (activity.isPrepareCamp()) {
            camping.getActorsInCamp()
                .forEach { it.clearMealEffects(camping.getAllRecipes()) }
            postChatMessage("Preparing Campsite, removing all existing Meal Effects")
        }

        // if it's a recipe we need to know the dc
        val recipe = if (activity.isDiscoverSpecialMeal()) askRecipe(camping) else null
        checkActor.campingActivityCheck(
            data = campingCheckData,
            overrideDc = recipe?.cookingLoreDC,
        )?.let { result ->
            camping.campingActivities.find { it.activity == activityName }?.result = result.toCamelCase()
            actor.setCamping(camping)

            if (activity.isHuntAndGather()) {
                postHuntAndGather(
                    actor = checkActor,
                    degreeOfSuccess = result,
                    zoneDc = campingCheckData.region.zoneDc,
                    regionLevel = campingCheckData.region.level,
                )
            } else if (activity.isDiscoverSpecialMeal() && recipe != null) {
                postDiscoverSpecialMeal(
                    actorUuid = checkActor.uuid,
                    recipe = recipe,
                    degreeOfSuccess = result,
                )
            }
        }
    }

    private suspend fun askRecipe(
        camping: CampingData
    ): RecipeData? =
        try {
            pickSpecialRecipe(camping = camping, partyActor = game.party())
        } catch (_: Exception) {
            ui.notifications.error("Discover Special Meal: No recipe chosen!")
            null
        }


    private suspend fun resetMeals() {
        actor.getCamping()?.let { camping ->
            camping.cooking.actorMeals.forEach { it.chosenMeal = "nothing" }
            actor.setCamping(camping)
        }
    }


    private suspend fun resetActivities() {
        actor.getCamping()?.let { camping ->
            camping.campingActivities = camping.campingActivities.filter { it.isPrepareCamp() }.toTypedArray()
            actor.setCamping(camping)
        }
    }

    private suspend fun previousSection() {
        actor.getCamping()?.let { camping ->
            camping.section = when (fromCamelCase<CampingSheetSection>(camping.section)) {
                CampingSheetSection.EATING -> if (camping.canPerformActivities()) {
                    CampingSheetSection.CAMPING_ACTIVITIES
                } else {
                    CampingSheetSection.PREPARE_CAMP
                }

                else -> CampingSheetSection.PREPARE_CAMP
            }.toCamelCase()
            actor.setCamping(camping)
        }
    }

    private suspend fun nextSection() {
        actor.getCamping()?.let { camping ->
            camping.section = when (fromCamelCase<CampingSheetSection>(camping.section)) {
                CampingSheetSection.PREPARE_CAMP -> if (camping.canPerformActivities()) {
                    CampingSheetSection.CAMPING_ACTIVITIES
                } else {
                    CampingSheetSection.EATING
                }

                else -> CampingSheetSection.EATING
            }.toCamelCase()
            actor.setCamping(camping)
        }
    }

    private suspend fun rollEncounter(includeFlatCheck: Boolean) {
        rollRandomEncounter(game, actor, includeFlatCheck)
    }

    private suspend fun assignRecipeTo(actorUuid: String, recipeName: String) {
        actor.getCamping()?.let { camping ->
            val existingMeal = camping.cooking.actorMeals.find { it.actorUuid == actorUuid }
            if (existingMeal == null) {
                camping.cooking.actorMeals.push(
                    ActorMeal(
                        actorUuid = actorUuid,
                        chosenMeal = recipeName,
                    )
                )
            } else {
                existingMeal.chosenMeal = recipeName
            }
            actor.setCamping(camping)
        }
    }

    private suspend fun assignActivityTo(actorUuid: String, activityName: String) {
        actor.getCamping()?.let { camping ->
            val activity = camping.getAllActivities().find { it.name == activityName }
            val activityActor = getCampingActivityCreatureByUuid(actorUuid)
            if (activityActor == null) {
                ui.notifications.error("Only NPCs and Characters can perform camping activities")
            } else if (activity == null) {
                ui.notifications.error("Activity with name $activityName not found")
            } else if (!activityActor.satisfiesAnyActivitySkillRequirement(activity, camping.ignoreSkillRequirements)) {
                ui.notifications.error("Actor does not satisfy skill requirements to perform $activityName")
            } else if (activity.requiresACheck() && !activityActor.hasAnyActivitySkill(activity)) {
                ui.notifications.error("Actor does not have the required skills to perform $activityName")
            } else {
                camping.campingActivities =
                    camping.campingActivities.filter { it.activity != activityName }.toTypedArray()
                val skill = activityActor
                    .findCampingActivitySkills(activity, camping.ignoreSkillRequirements)
                    .filterNot { it.validateOnly }
                    .firstOrNull()
                camping.campingActivities.push(
                    CampingActivity(
                        activity = activityName,
                        actorUuid = actorUuid,
                        selectedSkill = skill?.attribute?.value,
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
                val campingActor = getCampingActorByUuid(uuid)
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
        val override = max(actor.getCamping()?.minimumTravelSpeed ?: 0, travelSpeed)
        return calculateHexplorationActivities(override)
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

    private fun advanceHours(target: HTMLElement) {
        game.time.advance(3600 * (target.dataset["hours"]?.toInt() ?: 0))
    }

    private suspend fun getRecipeContext(
        recipeActors: List<RecipeActorContext>,
        foodItems: FoodItems,
        total: FoodAmount,
        camping: CampingData,
        section: CampingSheetSection,
    ): Array<RecipeContext> {
        val actorsByChosenMeal = recipeActors.groupBy { it.chosenMeal }
        val starving = RecipeContext(
            name = "Skip Meal",
            targetRecipe = "nothing",
            icon = "icons/containers/kitchenware/bowl-clay-brown.webp",
            requiresCheck = false,
            hidden = section != CampingSheetSection.EATING,
            rations = false,
            actors = actorsByChosenMeal["nothing"]?.toTypedArray() ?: emptyArray(),
        )
        val rations = RecipeContext(
            name = "Rations",
            targetRecipe = "rationsOrSubsistence",
            icon = "icons/consumables/food/berries-ration-round-red.webp",
            requiresCheck = false,
            cost = buildFoodCost(
                FoodAmount(rations = 1),
                totalAmount = total,
                items = foodItems,
            ),
            rations = true,
            actors = actorsByChosenMeal["rationsOrSubsistence"]?.toTypedArray() ?: emptyArray(),
            hidden = section != CampingSheetSection.EATING,
        )
        return arrayOf(starving, rations) + camping.getAllRecipes()
            .sortedBy { it.level }
            .mapNotNull { recipe ->
                val item = itemFromUuid(recipe.uuid)
                val cookingCost = buildFoodCost(
                    recipe.cookingCost(),
                    totalAmount = total,
                    items = foodItems
                )
                RecipeContext(
                    name = recipe.name,
                    targetRecipe = recipe.name,
                    cost = cookingCost,
                    uuid = recipe.uuid,
                    icon = recipe.icon ?: item?.img ?: "icons/consumables/food/shank-meat-bone-glazed-brown.webp",
                    requiresCheck = true,
                    hidden = section != CampingSheetSection.EATING,
                    rations = false,
                    actors = actorsByChosenMeal[recipe.name]?.toTypedArray() ?: emptyArray(),
                )
            }
            .toTypedArray()
    }

    private fun calculateTotalFoodCost(
        actorMeals: Array<ActorMeal>,
        availableFood: FoodAmount,
        foodItems: FoodItems,
    ): FoodCost {
        val amount = actorMeals
            .map {
                if (it.chosenMeal == "rationsOrSubsistence") {
                    FoodAmount(rations = 1)
                } else if (it.chosenMeal == "nothing") {
                    FoodAmount()
                } else {
                    recipes.find { recipe -> it.chosenMeal == recipe.name }?.cookingCost() ?: FoodAmount()
                }
            }
            .sum()
        return buildFoodCost(amount, totalAmount = availableFood, items = foodItems)
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<CampingSheetContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val time = game.getPF2EWorldTime().time
        val dayPercentage = time.toSecondOfDay().toFloat() / 86400f
        val widthWithoutBorder = windowWidth - 2
        val pxTimeOffset = -((dayPercentage * widthWithoutBorder).toInt() - widthWithoutBorder / 2)
        val camping = actor.getCamping() ?: getDefaultCamping(game)
        val actorsByUuid = getCampingActorsByUuid(camping.actorUuids).associateBy(PF2EActor::uuid)
        val groupActivities = camping.groupActivities().sortedBy { it.data.name }
        val section = fromCamelCase<CampingSheetSection>(camping.section) ?: CampingSheetSection.PREPARE_CAMP
        val prepareCampSection = section == CampingSheetSection.PREPARE_CAMP
        val campingActivitiesSection = section == CampingSheetSection.CAMPING_ACTIVITIES
        val eatingSection = section == CampingSheetSection.EATING
        val activities = groupActivities.mapIndexed { index, groupedActivity ->
            val (data, result) = groupedActivity
            val actor = result.actorUuid?.let { actorsByUuid[it] }?.unsafeCast<PF2ECreature>()
            val requiresCheck = !data.doesNotRequireACheck()
            val skills = getActivitySkills(
                actor = actor,
                groupedActivity = groupedActivity,
                ignoreSkillRequirements = camping.ignoreSkillRequirements,
            )
            val hidden = camping.lockedActivities.contains(data.name)
                    || (prepareCampSection && !groupedActivity.isPrepareCamp())
                    || (campingActivitiesSection && groupedActivity.isPrepareCamp())
                    || eatingSection
                    || camping.alwaysPerformActivities.contains(data.name)
            CampingSheetActivity(
                journalUuid = data.journalUuid,
                name = data.name,
                hidden = hidden,
                requiresCheck = requiresCheck,
                skills = skills,
                actor = actor?.let { act ->
                    CampingSheetActor(
                        name = act.name,
                        uuid = act.uuid,
                        image = act.img,
                        choseActivity = true,
                        degreeOfSuccess = Select.fromEnum<DegreeOfSuccess>(
                            label = "Degree of Success",
                            hideLabel = true,
                            required = false,
                            name = "activities.degreeOfSuccess.${data.name}",
                            value = result.result?.let { fromCamelCase<DegreeOfSuccess>(it) },
                            elementClasses = listOf("km-degree-of-success"),
                        ).toContext()
                    )
                },
            )
        }.toTypedArray()
        val recipes = camping.getAllRecipes()
        val fullRestDuration = getTotalRestDuration(
            watchers = actorsByUuid.values.filter { !camping.actorUuidsNotKeepingWatch.contains(it.uuid) },
            recipes = recipes.toList(),
            gunsToClean = camping.gunsToClean,
            increaseActorsKeepingWatch = camping.increaseWatchActorNumber,
        )
        val foodItems = getCompendiumFoodItems()
        val totalFood = camping.getFoodAmount(game.party(), foodItems)
        val availableFood = buildFoodCost(totalFood, items = foodItems)
        val recipeActors = camping.cooking.actorMeals
            .mapNotNull { meal ->
                actorsByUuid[meal.actorUuid]?.let { actor ->
                    RecipeActorContext(
                        name = actor.name,
                        uuid = actor.uuid,
                        image = actor.img,
                        chosenMeal = meal.chosenMeal,
                        favoriteMeal = meal.favoriteMeal,
                    )
                }
            }
        val recipesContext = getRecipeContext(
            recipeActors = recipeActors,
            foodItems = foodItems,
            total = totalFood,
            camping = camping,
            section = section,
        )
        val currentRegion = camping.findCurrentRegion()
        val regions = camping.regionSettings.regions
        val isGM = game.user.isGM
        CampingSheetContext(
            availableFood = availableFood,
            totalFoodCost = calculateTotalFoodCost(
                actorMeals = camping.cooking.actorMeals + ActorMeal(actorUuid = "test", chosenMeal = "Haggis"),
                foodItems = foodItems,
                availableFood = totalFood,
            ),
            partId = parent.partId,
            recipes = recipesContext,
            terrain = currentRegion?.terrain ?: "plains",
            region = Select(
                label = "Region",
                value = currentRegion?.name,
                options = regions.map {
                    SelectOption(label = it.name, value = it.name)
                },
                required = true,
                name = "region",
                disabled = !isGM,
                stacked = false,
            ).toContext(),
            pxTimeOffset = pxTimeOffset,
            time = time.toDateInputString(),
            isGM = isGM,
            isDay = time.isDay(),
            prepareCamp = activities.find { it.name == "Prepare Campsite" },
            activities = activities.filter { it.name != "Prepare Campsite" }.toTypedArray(),
            actors = camping.actorUuids.mapNotNull { uuid ->
                actorsByUuid[uuid]?.let { actor ->
                    CampingSheetActor(
                        image = actor.img,
                        uuid = uuid,
                        name = actor.name,
                        choseActivity = campingActivitiesSection
                                && groupActivities.any {
                            it.isNotPrepareCamp()
                                    && it.result.actorUuid == uuid
                                    && it.done()
                        }
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
            section = section.toLabel(),
            prepareCampSection = prepareCampSection,
            campingActivitiesSection = campingActivitiesSection,
            eatingSection = eatingSection,
        )
    }

    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> = buildPromise {
        console.log(value)
        actor.getCamping()?.let { camping ->
            camping.currentRegion = value.region
            camping.campingActivities = camping.campingActivities.map {
                CampingActivity(
                    activity = it.activity,
                    actorUuid = it.actorUuid,
                    result = value.activities.degreeOfSuccess?.get(it.activity),
                    selectedSkill = value.activities.selectedSkill?.get(it.activity),
                )
            }.toTypedArray()
            actor.setCamping(camping)
        }
        undefined
    }
}


fun getActivitySkills(
    actor: PF2ECreature?,
    groupedActivity: ActivityAndData,
    ignoreSkillRequirements: Boolean,
): FormElementContext? {
    return groupedActivity.data.getCampingSkills(actor).let { skillsAndProficiencies ->
        val options = skillsAndProficiencies
            .filter {
                if (ignoreSkillRequirements || actor == null) {
                    true
                } else {
                    actor.satisfiesSkillRequirement(it)
                }
            }
            .filter { it.validateOnly != true }
            .map {
                SelectOption(
                    label = it.attribute.label,
                    value = it.attribute.value,
                    classes = listOf("km-proficiency-${it.proficiency.toCamelCase()}")
                )
            }
            .sortedBy { it.label }
        Select(
            label = "Selected Skill",
            name = "activities.selectedSkill.${groupedActivity.data.name}",
            hideLabel = true,
            options = options,
            elementClasses = listOf("km-proficiency"),
            value = groupedActivity.result.selectedSkill,
        ).toContext()
    }
}