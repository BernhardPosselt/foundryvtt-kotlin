package com.foundryvtt.core.data.fields

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface NumberFieldOptions<T: Number> : DataFieldOptions<T> {
    var min: Number?
    var max: Number?
    var step: Number?
    var integer: Boolean?
    var positive: Boolean?
    var choices: Any?
}