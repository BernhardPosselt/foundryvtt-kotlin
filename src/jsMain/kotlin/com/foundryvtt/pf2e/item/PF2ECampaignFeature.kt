package com.foundryvtt.pf2e.item

import com.foundryvtt.core.DatabaseGetOperation
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

@JsName("CONFIG.PF2E.Item.documentClasses.campaignFeature")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECampaignFeature : PF2EItem {
    companion object : DocumentStatic<PF2ECampaignFeature>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2ECampaignFeature>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2ECampaignFeature>

    val system: PF2ECampaignFeatureData
}