package com.foundryvtt.core.data.fields

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface NumberFieldOptions : DataFieldOptions<Double> {
    val min: Double?
    val max: Double?
    val step: Double?
    val integer: Boolean?
    val positive: Boolean?
    val choices: Any?
}