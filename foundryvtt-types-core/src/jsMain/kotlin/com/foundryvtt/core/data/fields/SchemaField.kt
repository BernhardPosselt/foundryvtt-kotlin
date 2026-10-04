@file:JsQualifier("foundry.data.fields")

package com.foundryvtt.core.data.fields

import com.foundryvtt.core.AnyObject
import js.array.Tuple2
import js.iterable.JsIterable
import js.objects.Record

external class SchemaField(
    fields: DataSchema<out Any>,
    options: DataFieldOptions<Any>? = definedExternally, /*<Record<String, Any>>*/
    context: DataFieldContext<Record<String, out Any>>? = definedExternally,
) : DataField<Record<String, out Any>>, JsIterable<SchemaField> {
    var fields: DataSchema<*>
    var unknownKeys: Array<String>
    fun keys(): Array<String>
    fun values(): Array<SchemaField>
    fun entries(): Array<Tuple2<String, DataField<Any>>>
    fun has(key: String): Boolean
    fun get(key: String): DataField<Any>
    fun <T> getField(name: String): DataField<T>
    fun <T> getField(name: Array<String>): DataField<T>
    fun migrateSource(sourceData: AnyObject, fieldData: dynamic)
}