package com.foundryvtt.pf2e.actor

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2ECharacterHeroPoints {
    val value: Int
}

@JsPlainObject
external interface PF2ECharacterResourcesData {
    val heroPoints: PF2ECharacterHeroPoints
}

@JsPlainObject
external interface PF2ECharacterData {
    val resources: PF2ECharacterResourcesData
}

@JsName("CONFIG.PF2E.Actor.documentClasses.character")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECharacter : PF2EActor<PF2ECharacterData>