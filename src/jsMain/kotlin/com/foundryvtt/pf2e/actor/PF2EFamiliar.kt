package com.foundryvtt.pf2e.actor

import js.objects.Record
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EFamiliarData

@JsName("CONFIG.PF2E.Actor.documentClasses.familiar")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EFamiliar : PF2EActor {
    companion object : DocumentStatic<Any>

    val skills: Record<String, PF2EAttribute>
    val system: PF2EFamiliarData
}