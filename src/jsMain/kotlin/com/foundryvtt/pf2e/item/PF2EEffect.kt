package com.foundryvtt.pf2e.item

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EEffectData

@JsName("CONFIG.PF2E.Item.documentClasses.effect")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EEffect : PF2EItem<PF2EEffectData>