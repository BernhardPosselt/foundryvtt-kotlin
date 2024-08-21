package at.posselt.kingmaker.camping

import at.posselt.kingmaker.actor.party
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.divideRoundingUp
import at.posselt.kingmaker.utils.bindChatClick
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.emitKingmakerTools
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.isFirstGM
import at.posselt.kingmaker.utils.isInt
import at.posselt.kingmaker.utils.isJsObject
import at.posselt.kingmaker.utils.onKingmakerTools
import at.posselt.kingmaker.utils.postChatTemplate
import at.posselt.kingmaker.utils.roll
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2ECreature
import com.foundryvtt.pf2e.item.PF2EConsumable
import org.w3c.dom.get
import js.objects.recordOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.min

data class FoodAmount(
    val basicIngredients: Int = 0,
    val specialIngredients: Int = 0,
    val rations: Int = 0,
)

private suspend fun getHuntAndGatherQuantities(
    degreeOfSuccess: DegreeOfSuccess,
    regionDc: Int,
    regionLevel: Int,
): FoodAmount {
    if (degreeOfSuccess == DegreeOfSuccess.CRITICAL_SUCCESS) {
        val specialIngredients = if (regionLevel >= 14) {
            12
        } else if (regionLevel >= 7) {
            8
        } else {
            4
        }
        return FoodAmount(
            basicIngredients = 2 * regionDc,
            specialIngredients = specialIngredients,
            rations = 0,
        )
    } else if (degreeOfSuccess == DegreeOfSuccess.SUCCESS) {
        val dice = if (regionLevel >= 14) {
            3
        } else if (regionLevel >= 7) {
            2
        } else {
            1
        }
        val specialIngredients = roll("${dice}d4", "Special Ingredients")
        return FoodAmount(
            basicIngredients = regionDc,
            specialIngredients = specialIngredients,
            rations = 0,
        )
    } else if (degreeOfSuccess == DegreeOfSuccess.FAILURE) {
        return FoodAmount(
            basicIngredients = regionDc,
            specialIngredients = 0,
            rations = 0,
        )
    } else {
        return FoodAmount(
            basicIngredients = min(roll("1d4", "Basic Ingredients"), regionDc),
            specialIngredients = 0,
            rations = 0,
        )
    }
}

const val specialIngredientUuid = "Compendium.pf2e.equipment-srd.Item.OCTireuX60MaPcEi";
const val basicIngredientUuid = "Compendium.pf2e.equipment-srd.Item.kKnMlymiqZLVEAtI";
const val rationUuid = "Compendium.pf2e.equipment-srd.Item.L9ZV076913otGtiB";

suspend fun PF2EActor.addConsumableToInventory(uuid: String, quantity: Int) {
    if (quantity > 0) {
        fromUuidTypeSafe<PF2EConsumable>(uuid)?.let { item ->
            val max = item.system.uses.max
            item.system.quantity = quantity.divideRoundingUp(max)
            item.system.uses.value = quantity % max
            addToInventory(item.toObject(), undefined, true)
        }
    }
}

suspend fun PF2EActor.addFoodToInventory(foodAmount: FoodAmount) = coroutineScope {
    listOf(
        async { addConsumableToInventory(specialIngredientUuid, foodAmount.specialIngredients) },
        async { addConsumableToInventory(basicIngredientUuid, foodAmount.basicIngredients) },
        async { addConsumableToInventory(rationUuid, foodAmount.rations) },
    ).awaitAll()
}

suspend fun postHuntAndGather(
    actor: PF2ECreature,
    degreeOfSuccess: DegreeOfSuccess,
    zoneDc: Int,
    regionLevel: Int,
) {
    val amount = getHuntAndGatherQuantities(
        degreeOfSuccess = degreeOfSuccess,
        regionDc = zoneDc,
        regionLevel = regionLevel,
    )
    postChatTemplate(
        templatePath = "chatmessages/hunt-and-gather.hbs",
        templateContext = recordOf(
            "actorName" to actor.name,
            "basicIngredients" to amount.basicIngredients,
            "specialIngredients" to amount.specialIngredients,
        )
    )
}

class HuntAndGatherMessage(
    val basicIngredients: Int,
    val specialIngredients: Int,
) {
    fun toMessage(): AnyObject {
        return recordOf(
            "action" to "addHuntAndGatherResult",
            "data" to recordOf(
                "basicIngredients" to basicIngredients,
                "specialIngredients" to specialIngredients,
            )
        )
    }

    companion object {
        fun parse(data: Any?): HuntAndGatherMessage? {
            if (isJsObject(data)) {
                val basicIngredients = data["basicIngredients"]
                val specialIngredients = data["specialIngredients"]
                if (isInt(basicIngredients) && isInt(specialIngredients)) {
                    return HuntAndGatherMessage(
                        basicIngredients = basicIngredients,
                        specialIngredients = specialIngredients,
                    )
                }
            }
            return null
        }
    }
}


suspend fun findHuntAndGatherTargetActor(game: Game, data: CampingData): PF2EActor? {
    val party = game.party()
    return data.huntAndGatherTargetActorUuid?.let {
        if (party != null && party.uuid == it) {
            party
        } else if (data.actorUuids.contains(it)) {
            getCampingActorByUuid(it)
        } else {
            null
        }
    }
}

suspend fun addHuntAndGather(
    game: Game,
    camping: CampingData,
    result: HuntAndGatherMessage
) = findHuntAndGatherTargetActor(game, camping)
    ?.let {
        it.addFoodToInventory(
            FoodAmount(
                basicIngredients = result.basicIngredients,
                specialIngredients = result.specialIngredients,
            )
        )
        postChatTemplate(
            templatePath = "chatmessages/add-hunt-and-gather.hbs",
            templateContext = recordOf(
                "actorName" to it.name,
                "basicIngredients" to result.basicIngredients,
                "specialIngredients" to result.specialIngredients,
            )
        )
    }