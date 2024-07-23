package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.DatabaseGetOperation
import js.objects.Record
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

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
    companion object : DocumentStatic<PF2ECharacter>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2ECharacter>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2ECharacter>

    val skills: Record<String, PF2EAttribute>
    val system: PF2ECharacterData
}