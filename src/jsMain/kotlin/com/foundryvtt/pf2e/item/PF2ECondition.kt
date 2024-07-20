package com.foundryvtt.pf2e.item

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EConditionData

@JsName("CONFIG.PF2E.Item.documentClasses.condition")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECondition : PF2EItem<PF2EConditionData>