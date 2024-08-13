package com.foundryvtt.core.data.fields

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface NumberFieldOptions : DataFieldOptions/*<Double>*/ {
    var min: Double?
    var max: Double?
    var step: Double?
    var integer: Boolean?
    var positive: Boolean?
    var choices: Any?
}