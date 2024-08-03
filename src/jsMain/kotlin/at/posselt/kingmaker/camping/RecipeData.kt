package at.posselt.kingmaker.camping

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface MealEffect {
    val uuid: String
    val removeAfterRest: Boolean?
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
    val specialIngredients: Int
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

@JsModule("./data/recipes.json")
external val recipes: Array<RecipeData>