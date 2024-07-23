package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.DatabaseGetOperation
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EPartyData

@JsName("CONFIG.PF2E.Actor.documentClasses.party")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EParty : PF2EActor {
    companion object : DocumentStatic<PF2EParty>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EParty>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2EParty>

    val members: Array<PF2ECharacter>
    val system: PF2EPartyData
}