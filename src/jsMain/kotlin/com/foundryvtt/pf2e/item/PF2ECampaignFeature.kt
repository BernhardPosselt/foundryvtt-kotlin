package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2ECampaignFeatureTraits {
    val value: Array<String>
}

@JsPlainObject
external interface PF2ECampaignFeatureLevel {
    val value: Int
}

@JsPlainObject
external interface PF2ECampaignFeatureData {
    val campaign: String
    val category: String
    val traits: PF2ECampaignFeatureTraits
    val level: PF2ECampaignFeatureLevel
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PF2E.Item.documentClasses.campaignFeature")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECampaignFeature : PF2EItem {
    companion object : DocumentStatic<PF2ECampaignFeature>

    override fun delete(operation: DatabaseDeleteOperation): Promise<PF2ECampaignFeature>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<PF2ECampaignFeature>

    val system: PF2ECampaignFeatureData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2ECampaignFeature.update(
    data: PF2ECampaignFeature,
    operation: DatabaseUpdateOperation = jso()
): Promise<PF2ECampaignFeature> =
    update(data as AnyObject, operation)