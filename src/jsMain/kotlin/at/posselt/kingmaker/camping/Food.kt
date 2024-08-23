package at.posselt.kingmaker.camping

import at.posselt.kingmaker.divideRoundingUp
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2EParty
import com.foundryvtt.pf2e.item.PF2EConsumable
import com.foundryvtt.pf2e.item.PF2EConsumableData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.js.JsPlainObject
import kotlin.js.unsafeCast
import kotlin.math.max

private const val specialIngredientUuid = "Compendium.pf2e.equipment-srd.Item.OCTireuX60MaPcEi"
private const val basicIngredientUuid = "Compendium.pf2e.equipment-srd.Item.kKnMlymiqZLVEAtI"
private const val rationUuid = "Compendium.pf2e.equipment-srd.Item.L9ZV076913otGtiB"

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

@JsPlainObject
external interface FoodCost {
    val rations: Int
    val basicIngredients: Int
    val specialIngredients: Int
    val rationImage: String?
    val basicImage: String?
    val specialImage: String?
    val totalRations: Int
    val totalBasicIngredients: Int
    val totalSpecialIngredients: Int
}

fun buildFoodCost(
    amount: FoodAmount,
    totalAmount: FoodAmount,
    items: FoodItems,
) = FoodCost(
    rations = amount.rations,
    basicIngredients = amount.basicIngredients,
    specialIngredients = amount.specialIngredients,
    totalRations = totalAmount.rations,
    totalBasicIngredients = totalAmount.basicIngredients,
    totalSpecialIngredients = totalAmount.specialIngredients,
    rationImage = items.ration.img,
    basicImage = items.basic.img,
    specialImage = items.special.img,
)


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

    fun toDescription(): String {
        return sequenceOf(
            if (basicIngredients > 0) "Basic: $basicIngredients" else null,
            if (specialIngredients > 0) "Special: $specialIngredients" else null,
            if (rations > 0) "Rations: $rations" else null,
        ).joinToString(", ")
    }
}

fun List<FoodAmount>.sum() =
    fold(FoodAmount()) { a, b -> a + b }

data class FoodAmountAndImages(
    val amount: FoodAmount,
    val basicIngredientsImg: String?,
    val specialIngredientsImg: String?,
    val rationImg: String?,
)

suspend fun PF2EActor.addFoodToInventory(foodAmount: FoodAmount) = coroutineScope {
    listOf(
        async { addConsumableToInventory(specialIngredientUuid, foodAmount.specialIngredients) },
        async { addConsumableToInventory(basicIngredientUuid, foodAmount.basicIngredients) },
        async { addConsumableToInventory(rationUuid, foodAmount.rations) },
    ).awaitAll()
}

private fun PF2EConsumable.totalQuantity() =
    system.uses.value + max(0, system.quantity - 1) * system.uses.max

private fun List<PF2EConsumable>.sumQuantity() =
    fold(0) { a, b -> a + b.totalQuantity() }

private fun PF2EActor.consumableQuantityBySlug(slug: String): Int =
    itemTypes.consumable.filter { it.slug == slug }.sumQuantity()


private fun findTotalFood(
    actor: PF2EActor,
    foodItems: FoodItems,
): FoodAmount {
    return FoodAmount(
        basicIngredients = actor.consumableQuantityBySlug(foodItems.basic.slug),
        specialIngredients = actor.consumableQuantityBySlug(foodItems.special.slug),
        rations = actor.consumableQuantityBySlug(foodItems.ration.slug),
    )
}

data class FoodItems(
    val basic: PF2EConsumable,
    val special: PF2EConsumable,
    val ration: PF2EConsumable,
)

suspend fun getCompendiumFoodItems() = coroutineScope {
    val b = async { fromUuidTypeSafe<PF2EConsumable>(basicIngredientUuid) }
    val s = async { fromUuidTypeSafe<PF2EConsumable>(specialIngredientUuid) }
    val r = async { fromUuidTypeSafe<PF2EConsumable>(rationUuid) }
    val basic = b.await()
    val special = s.await()
    val ration = r.await()
    checkNotNull(basic) { "Basic Ingredient UUID changed, something is very wrong" }
    checkNotNull(special) { "Basic Ingredient UUID changed, something is very wrong" }
    checkNotNull(ration) { "Basic Ingredient UUID changed, something is very wrong" }
    FoodItems(basic = basic, special = special, ration = ration)
}

suspend fun CampingData.getFoodAmount(
    party: PF2EParty,
    foodItems: FoodItems,
): FoodAmount = coroutineScope {
    val actors = getActorsInCamp() + party
    actors.map {
        findTotalFood(
            actor = it,
            foodItems = foodItems,
        )
    }.sum()
}