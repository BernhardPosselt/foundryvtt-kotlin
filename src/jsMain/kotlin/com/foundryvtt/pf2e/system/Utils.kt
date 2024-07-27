package com.foundryvtt.pf2e.system

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface Value<T> {
    var value: T
}

@JsPlainObject
external interface StringArrayValue : Value<Array<String>>

@JsPlainObject
external interface StringValue : Value<String>

@JsPlainObject
external interface IntValue : Value<Int>

@JsPlainObject
external interface MaxValue : IntValue {
    var max: Int
}

@JsPlainObject
external interface MinValue : IntValue {
    var min: Int
}

@JsPlainObject
external interface MinMaxValue : MaxValue, MinValue

@JsPlainObject
external interface ItemTraits : StringArrayValue {
    val rarity: String
}