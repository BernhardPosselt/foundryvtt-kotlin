package com.foundryvtt.pf2e.item

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EActionData

@JsName("CONFIG.PF2E.Item.documentClasses.action")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EAction : PF2EItem<PF2EActionData>