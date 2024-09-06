package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.divideRoundingUp
import at.posselt.pfrpg.utils.buildUpdate
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import at.posselt.pfrpg.utils.fromUuidsTypeSafe
import com.foundryvtt.core.AnyObject
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2EParty
import com.foundryvtt.pf2e.item.PF2EConsumable
import com.foundryvtt.pf2e.item.PF2EConsumableData
import com.foundryvtt.pf2e.item.PF2EEffect
import js.array.push
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.js.JsPlainObject
import kotlin.js.unsafeCast
import kotlin.math.max
import kotlin.math.min

suspend fun PF2EActor.addConsumableToInventory(uuid: String, quantity: Int) {
    if (quantity > 0) {
        fromUuidTypeSafe<PF2EConsumable>(uuid)?.let { item ->
            val obj = item.toObject().unsafeCast<dynamic>()
            val system = obj.system.unsafeCast<PF2EConsumableData>()
            if (system.uses.max > 1) {
                val max = system.uses.max
                system.quantity = quantity.divideRoundingUp(max)
                system.uses.value = quantity % max
            } else {
                system.quantity = quantity
            }
            addToInventory(obj, undefined, false)
        }
    }
}

private fun getMealEffectUuids(recipe: RecipeData): List<String> {
    return listOf(
        recipe.criticalFailure.effects?.map { it.uuid },
        recipe.success.effects?.map { it.uuid },
        recipe.criticalSuccess.effects?.map { it.uuid },
        recipe.favoriteMeal?.effects?.map { it.uuid },
    )
        .filterNotNull()
        .flatMap { it }
}

suspend fun PF2EActor.clearEffectsByUuid(uuids: Set<String>) {
    val effectSlugs = fromUuidsTypeSafe<PF2EEffect>(uuids.toTypedArray())
        .map { it.slug }
        .toSet()
    val idsToRemove = itemTypes.effect
        .filter { it.slug in effectSlugs }
        .mapNotNull { it.id }
        .toTypedArray()
    deleteEmbeddedDocuments<PF2EEffect>("Item", idsToRemove).await()
}

suspend fun CampingData.clearMealEffects() = coroutineScope {
    getActorsInCamp()
        .map { async { it.clearMealEffects(getAllRecipes()) } }
        .awaitAll()
}


suspend fun PF2EActor.clearMealEffects(recipes: Array<RecipeData>) {
    clearEffectsByUuid(
        recipes
            .flatMap(::getMealEffectUuids)
            .toSet()
    )
}

suspend fun PF2EActor.addEffectsByUuid(uuids: List<String>) {
    val effects = fromUuidsTypeSafe<PF2EEffect>(uuids.toTypedArray())
    createEmbeddedDocuments("Item", effects).await()
}

suspend fun PF2EActor.applyMealEffects(outcomes: List<CookingOutcome>) {
    val uuids = outcomes
        .flatMap {
            if (it.chooseRandomly == true) {
                sequenceOf(it.effects?.randomOrNull()?.uuid)
            } else {
                it.effects?.map { it.uuid }?.asSequence() ?: emptySequence()
            }
        }
        .filterNotNull()
    addEffectsByUuid(uuids)
}

@JsPlainObject
external interface FoodCost {
    val rations: String
    val basicIngredients: String
    val specialIngredients: String
    val rationImage: String?
    val basicImage: String?
    val specialImage: String?
    val totalRations: Int?
    val totalBasicIngredients: Int?
    val totalSpecialIngredients: Int?
    val missingRations: Boolean
    val missingBasic: Boolean
    val missingSpecial: Boolean
}

fun buildFoodCost(
    amount: FoodAmount,
    totalAmount: FoodAmount? = null,
    items: FoodItems,
    capAt: Int? = 99,
) = FoodCost(
    rations = capAt(amount.rations, capAt),
    basicIngredients = capAt(amount.basicIngredients, capAt),
    specialIngredients = capAt(amount.specialIngredients, capAt),
    totalRations = totalAmount?.rations,
    totalBasicIngredients = totalAmount?.basicIngredients,
    totalSpecialIngredients = totalAmount?.specialIngredients,
    rationImage = items.ration.img,
    basicImage = items.basic.img,
    specialImage = items.special.img,
    missingBasic = totalAmount?.let { it.basicIngredients < amount.basicIngredients } == true,
    missingSpecial = totalAmount?.let { it.specialIngredients < amount.specialIngredients } == true,
    missingRations = totalAmount?.let { it.rations < amount.rations } == true,
)

fun capAt(number: Int, cap: Int? = null): String {
    return if (cap == null || number <= cap) {
        number.toString()
    } else {
        "$number+"
    }
}

data class FoodAmount(
    val basicIngredients: Int = 0,
    val specialIngredients: Int = 0,
    val rations: Int = 0,
) {
    operator fun plus(other: FoodAmount): FoodAmount =
        FoodAmount(
            basicIngredients = basicIngredients + other.basicIngredients,
            specialIngredients = specialIngredients + other.specialIngredients,
            rations = rations + other.rations,
        )

    operator fun times(amount: Int) =
        FoodAmount(
            basicIngredients = basicIngredients * amount,
            specialIngredients = specialIngredients * amount,
            rations = rations * amount,
        )

    fun isEmpty() = basicIngredients == 0 && specialIngredients == 0 && rations == 0
}

fun List<FoodAmount>.sum() =
    fold(FoodAmount()) { a, b -> a + b }

suspend fun PF2EActor.addFoodToInventory(foodAmount: FoodAmount) = coroutineScope {
    listOf(
        async { addConsumableToInventory(Config.items.specialIngredientUuid, foodAmount.specialIngredients) },
        async { addConsumableToInventory(Config.items.basicIngredientUuid, foodAmount.basicIngredients) },
        async { addConsumableToInventory(Config.items.rationUuid, foodAmount.rations) },
    ).awaitAll()
}


suspend fun PF2EActor.removeConsumableFromInventory(slug: String, quantity: Int): Int {
    val updates = arrayOf<AnyObject>()
    val deleteIds = arrayOf<String>()
    var leftOver = quantity
    consumablesBySlug(slug).forEach { consumable ->
        val id = consumable.id
        if (id != null && leftOver > 0) {
            val totalQuantity = consumable.totalQuantity()
            leftOver -= min(leftOver, totalQuantity)
            if (totalQuantity <= leftOver) {
                deleteIds.push(id)
            } else {
                val chargeUpdates = calculateCharges(
                    removeQuantity = leftOver,
                    itemQuantity = consumable.system.quantity,
                    itemUses = consumable.system.uses.value,
                    itemMaxUses = consumable.system.uses.max,
                )
                updates.push(consumable.buildUpdate<PF2EConsumable> {
                    _id = id
                    system.quantity = chargeUpdates.quantity
                    system.uses.value = chargeUpdates.charges
                })
            }
        }
    }
    updateEmbeddedDocuments<PF2EConsumable>("Item", updates).await()
    deleteEmbeddedDocuments<PF2EConsumable>("Item", deleteIds).await()
    return leftOver
}

data class ChargeUpdate(val quantity: Int, val charges: Int)

fun calculateCharges(
    removeQuantity: Int,
    itemQuantity: Int,
    itemUses: Int,
    itemMaxUses: Int
): ChargeUpdate {
    val totalQuantity = calculateMaxQuantity(uses = itemUses, maxUses = itemMaxUses, quantity = itemQuantity)
    val leftOver = max(0, totalQuantity - removeQuantity)
    val charges = leftOver % itemMaxUses
    val quantity = leftOver.divideRoundingUp(itemMaxUses)
    return ChargeUpdate(
        charges = max(0, if (charges == 0 && quantity != 0) itemMaxUses else charges),
        quantity = max(0, quantity)
    )
}

suspend fun reduceFoodBy(actors: List<PF2EActor>, foodAmount: FoodAmount, foodItems: FoodItems): FoodAmount {
    var leftOver = foodAmount
    actors.forEach { actor ->
        leftOver = actor.reduceFoodBy(foodAmount = leftOver, foodItems = foodItems)
    }
    return leftOver
}

suspend fun PF2EActor.reduceFoodBy(foodAmount: FoodAmount, foodItems: FoodItems): FoodAmount = coroutineScope {
    val leftOverSpecial = async { removeConsumableFromInventory(foodItems.special.slug, foodAmount.specialIngredients) }
    val leftOverBasic = async { removeConsumableFromInventory(foodItems.basic.slug, foodAmount.basicIngredients) }
    val leftOverProvisions = async { removeConsumableFromInventory(foodItems.provisions.slug, foodAmount.rations) }
    val leftOverRation = async { removeConsumableFromInventory(foodItems.ration.slug, leftOverProvisions.await()) }
    FoodAmount(
        basicIngredients = leftOverBasic.await(),
        specialIngredients = leftOverSpecial.await(),
        rations = leftOverRation.await(),
    )
}

private fun calculateMaxQuantity(uses: Int, quantity: Int, maxUses: Int) =
    uses + max(0, quantity - 1) * maxUses

private fun PF2EConsumable.totalQuantity() =
    calculateMaxQuantity(uses = system.uses.value, quantity = system.quantity, maxUses = system.uses.max)

private fun List<PF2EConsumable>.sumQuantity() =
    fold(0) { a, b -> a + b.totalQuantity() }

private fun PF2EActor.consumableQuantityBySlug(slug: String): Int =
    consumablesBySlug(slug).sumQuantity()

private fun PF2EActor.consumablesBySlug(slug: String) =
    itemTypes.consumable.filter { it.slug == slug }


private fun findTotalFood(
    actor: PF2EActor,
    foodItems: FoodItems,
): FoodAmount {
    return FoodAmount(
        basicIngredients = actor.consumableQuantityBySlug(foodItems.basic.slug),
        specialIngredients = actor.consumableQuantityBySlug(foodItems.special.slug),
        rations = actor.consumableQuantityBySlug(foodItems.ration.slug) +
                actor.consumableQuantityBySlug(foodItems.provisions.slug),
    )
}

data class FoodItems(
    val basic: PF2EConsumable,
    val special: PF2EConsumable,
    val ration: PF2EConsumable,
    val provisions: PF2EConsumable,
)

suspend fun getCompendiumFoodItems() = coroutineScope {
    val b = async { fromUuidTypeSafe<PF2EConsumable>(Config.items.basicIngredientUuid) }
    val s = async { fromUuidTypeSafe<PF2EConsumable>(Config.items.specialIngredientUuid) }
    val r = async { fromUuidTypeSafe<PF2EConsumable>(Config.items.rationUuid) }
    val p = async { fromUuidTypeSafe<PF2EConsumable>(Config.items.provisionsUuid) }
    val basic = b.await()
    val special = s.await()
    val ration = r.await()
    val provisions = p.await()
    checkNotNull(basic) { "Basic Ingredient UUID changed, something is very wrong" }
    checkNotNull(special) { "Basic Ingredient UUID changed, something is very wrong" }
    checkNotNull(ration) { "Basic Ingredient UUID changed, something is very wrong" }
    checkNotNull(provisions) { "Basic Ingredient UUID changed, something is very wrong" }
    FoodItems(basic = basic, special = special, ration = ration, provisions = provisions)
}

suspend fun CampingData.getFoodAmount(
    party: PF2EParty?,
    foodItems: FoodItems,
): FoodAmount = coroutineScope {
    val actors = getActorsInCamp() + (party?.let { listOf(it) } ?: emptyList())
    actors.map {
        findTotalFood(
            actor = it,
            foodItems = foodItems,
        )
    }.sum()
}