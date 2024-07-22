package com.foundryvtt.pf2e.actor

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EPartyData

@JsName("CONFIG.PF2E.Actor.documentClasses.party")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EParty : PF2EActor {
    companion object : DocumentStatic<Any>

    val members: Array<PF2ECharacter>
    val system: PF2EPartyData
}