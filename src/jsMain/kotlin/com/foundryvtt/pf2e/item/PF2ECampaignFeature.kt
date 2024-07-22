package com.foundryvtt.pf2e.item

import kotlinx.js.JsPlainObject

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
    companion object : DocumentStatic<Any>

    val system: PF2ECampaignFeatureData
}