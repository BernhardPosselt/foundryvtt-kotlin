package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.pf2e.system.ItemTraits
import kotlinx.js.JsPlainObject
import kotlin.js.Promise


@JsPlainObject
external interface PF2EArmorData {
    var traits: ItemTraits
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PF2E.Item.documentClasses.armor")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EArmor : PF2EItem {
    companion object : DocumentStatic<PF2EArmor>

    override fun delete(operation: DatabaseDeleteOperation): Promise<PF2EArmor>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<PF2EArmor?>

    val system: PF2EArmorData
}