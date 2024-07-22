package com.foundryvtt.pf2e.item

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EConsumableData

@JsName("CONFIG.PF2E.Item.documentClasses.consumable")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EConsumable : PF2EItem {
    companion object : DocumentStatic<Any>

    val system: PF2EConsumableData
}