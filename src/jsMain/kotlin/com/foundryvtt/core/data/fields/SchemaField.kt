package com.foundryvtt.core.data.fields

import com.foundryvtt.core.AnyObject
import js.array.JsTuple2
import js.iterable.JsIterable
import js.objects.Record

typealias DataSchema<T> = Record<String, DataField<T>>

external class SchemaField(
    fields: DataSchema<*>,
    options: DataFieldOptions<Record<String, Any>>,
    context: DataFieldContext<Record<String, Any>>,
) : DataField<Record<String, Any>>, JsIterable<SchemaField> {
    var fields: DataSchema<*>
    var unkownKeys: Array<String>
    fun keys(): Array<String>
    fun values(): Array<SchemaField>
    fun entries(): Array<JsTuple2<String, DataField<Any>>>
    fun has(key: String): Boolean
    fun get(key: String): DataField<Any>
    fun <T> getField(name: String): DataField<T>
    fun <T> getField(name: Array<String>): DataField<T>
    fun migrateSource(sourceData: AnyObject, fieldData: dynamic)
}