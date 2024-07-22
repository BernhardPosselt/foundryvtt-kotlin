package com.foundryvtt.pf2e.item

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EEquipmentData

@JsName("CONFIG.PF2E.Item.documentClasses.equipment")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EEquipment : PF2EItem {
    companion object : DocumentStatic<Any>

    val system: PF2EEquipmentData
}