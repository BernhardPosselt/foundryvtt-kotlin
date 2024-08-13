package com.foundryvtt.core.data.dsl

import at.posselt.kingmaker.utils.toRecord
import com.foundryvtt.core.data.fields.*
import js.objects.Record


class Schema {
    val fields = mutableMapOf<String, DataField<out Any>>()

    fun string(
        name: String,
        context: DataFieldContext<String>? = undefined,
        block: (StringFieldOptions.() -> Unit)? = null,
    ) {
        val options = StringFieldOptions()
        block?.invoke(options)
        fields[name] = StringField(options = options, context = context)
    }

    fun number(
        name: String,
        context: DataFieldContext<Double>? = undefined,
        block: (NumberFieldOptions.() -> Unit)? = null,
    ) {
        val options = NumberFieldOptions()
        block?.invoke(options)
        fields[name] = NumberField(options = options, context = context)
    }

    fun boolean(
        name: String,
        context: DataFieldContext<Boolean>? = undefined,
        block: (DataFieldOptions/*<Boolean>*/.() -> Unit)? = null,
    ) {
        val options = DataFieldOptions/*<Boolean>*/()
        block?.invoke(options)
        fields[name] = BooleanField(options = options, context = context)
    }

    fun <T> array(
        name: String,
        options: ArrayFieldOptions<T>? = undefined,
        context: DataFieldContext<Array<T>>? = undefined,
        block: (Schema.() -> Unit),
    ) {
        val s = Schema()
        s.block()
        val element = SchemaField(s.build())
        fields[name] = ArrayField(element = element, options = options, context = context)
    }


    class ArrayConfiguration<T> {
        var arrayOptions: ArrayFieldOptions<T>? = undefined
        var fieldOptions: StringFieldOptions? = undefined

        fun field(block: StringFieldOptions.() -> Unit) {
            val opts = StringFieldOptions()
            opts.block()
            fieldOptions = opts
        }

        fun options(block: ArrayFieldOptions<T>.() -> Unit) {
            val opts = ArrayFieldOptions<T>()
            opts.block()
            arrayOptions = opts
        }
    }

    fun stringArray(
        name: String,
        context: DataFieldContext<Array<String>>? = undefined,
        fieldContext: DataFieldContext<String>? = undefined,
        block: ArrayConfiguration<String>.() -> Unit,
    ) {
        val opts = ArrayConfiguration<String>()
        opts.block()
        val element = StringField(options = opts.fieldOptions, context = fieldContext)
        fields[name] = ArrayField(element = element, options = opts.arrayOptions, context = context)
    }

    fun schema(
        name: String,
        options: DataFieldOptions? = undefined,
        context: DataFieldContext<Record<String, Any>>? = undefined,
        block: (Schema.() -> Unit)? = null,
    ) {
        val schema = Schema()
        block?.invoke(schema)
        fields[name] = SchemaField(
            fields = schema.build(),
            options = options,
            context = context,
        )
    }

    fun <T> build(): DataSchema<T> {
        return fields.asSequence()
            .map { it.key to it.value.asDynamic() }
            .toRecord()
    }
}

fun buildSchema(block: Schema.() -> Unit): DataSchema<out Any> {
    val schema = Schema()
    schema.block()
    return schema.build()
}

fun blubb() {
    val s = buildSchema {
        string("value") {
            required = true
            blank = false
        }
        schema("field") {
            string("name") {
                required = true
                string("test")
            }
        }
    }
    console.log(s)
}