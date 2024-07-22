package com.foundryvtt.pf2e.actor

import js.objects.Record
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
external interface PF2ECharacterXPData {
    val value: Int
    val max: Int
}

@JsPlainObject
external interface PF2ECharacterLevelData {
    val value: Int
}


@JsPlainObject
external interface PF2ECharacterDetailsData {
    val xp: PF2ECharacterXPData
    val level: PF2ECharacterLevelData
}


@JsPlainObject
external interface PF2ECharacterData {
    val resources: PF2ECharacterResourcesData
    val details: PF2ECharacterDetailsData
    val exploration: Array<String>?
}

@JsName("CONFIG.PF2E.Actor.documentClasses.character")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECharacter : PF2EActor {
    companion object : DocumentStatic<Any>

    val skills: Record<String, PF2EAttribute>
    val system: PF2ECharacterData
}