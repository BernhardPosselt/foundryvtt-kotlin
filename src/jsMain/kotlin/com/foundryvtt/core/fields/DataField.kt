@file:JsQualifier("foundry.data.fields")

package com.foundryvtt.core.fields


abstract external class DataField<T>(options: DataFieldOptions<T> = definedExternally) {
    val name: String
}