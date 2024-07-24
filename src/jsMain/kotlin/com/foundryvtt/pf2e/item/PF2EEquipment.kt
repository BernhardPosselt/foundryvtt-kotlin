package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.DatabaseGetOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EEquipmentData

@JsName("CONFIG.PF2E.Item.documentClasses.equipment")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EEquipment : PF2EItem {
    companion object : DocumentStatic<PF2EEquipment>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EEquipment>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EEquipment>

    val system: PF2EEquipmentData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EEquipment.update(data: PF2EEquipment, operation: DatabaseGetOperation = jso()): Promise<PF2EEquipment> =
    update(data as AnyObject, operation)