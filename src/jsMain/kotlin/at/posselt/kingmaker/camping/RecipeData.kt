package at.posselt.kingmaker.camping

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface MealEffect {
    val uuid: String
    val removeAfterRest: Boolean?
    val changeRestDurationSeconds: Int?
    val doublesHealing: Boolean?
}


@JsPlainObject
external interface CookingOutcome {
    val effects: Array<MealEffect>?
    val chooseRandomly: Boolean?
}


@JsPlainObject
external interface RecipeData {
    val name: String
    val basicIngredients: Int
    val specialIngredients: Int?
    val cookingLoreDC: Int
    val survivalDC: Int
    val uuid: String
    val level: Int
    val cost: String
    val rarity: String
    val isHomebrew: Boolean?
    val criticalSuccess: CookingOutcome
    val success: CookingOutcome
    val criticalFailure: CookingOutcome
    val favoriteMeal: CookingOutcome?
}

fun RecipeData.cookingCost(): FoodAmount =
    FoodAmount(
        basicIngredients = basicIngredients * 2,
        specialIngredients = (specialIngredients ?: 0) * 2,
        rations = 2,
    )


fun RecipeData.discoverCost(): FoodAmount =
    cookingCost() * 2


@JsModule("./data/recipes.json")
external val recipes: Array<RecipeData>