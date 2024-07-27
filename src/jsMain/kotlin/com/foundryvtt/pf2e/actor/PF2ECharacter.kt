package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.Record
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2ECharacterHeroPoints {
    var value: Int
}

@JsPlainObject
external interface PF2ECharacterResourcesData {
    var heroPoints: PF2ECharacterHeroPoints
}

@JsPlainObject
external interface PF2ECharacterXPData {
    var value: Int
    var max: Int
}

@JsPlainObject
external interface PF2ECharacterLevelData {
    var value: Int
}


@JsPlainObject
external interface PF2ECharacterDetailsData {
    var xp: PF2ECharacterXPData
    var level: PF2ECharacterLevelData
}


@JsPlainObject
external interface PF2ECharacterData {
    var resources: PF2ECharacterResourcesData
    var details: PF2ECharacterDetailsData
    var exploration: Array<String>?
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PF2E.Actor.documentClasses.character")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECharacter : PF2EActor {
    companion object : DocumentStatic<PF2ECharacter>

    override fun delete(operation: DatabaseDeleteOperation): Promise<PF2ECharacter>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<PF2ECharacter>

    val skills: Record<String, PF2EAttribute>
    val system: PF2ECharacterData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2ECharacter.update(data: PF2ECharacter, operation: DatabaseUpdateOperation = jso()): Promise<PF2ECharacter> =
    update(data as AnyObject, operation)