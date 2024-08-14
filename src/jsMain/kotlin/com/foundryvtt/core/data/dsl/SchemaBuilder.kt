package com.foundryvtt.core.data.dsl

import at.posselt.kingmaker.utils.toRecord
import com.foundryvtt.core.data.fields.*
import js.objects.Record

open class BaseArrayConfiguration<T> {
    open var arrayOptions: ArrayFieldOptions<T>? = undefined

    open fun options(block: ArrayFieldOptions<T>.() -> Unit) {
        val opts = ArrayFieldOptions<T>()
        opts.block()
        arrayOptions = opts
    }
}

class StringArrayConfiguration : BaseArrayConfiguration<String>() {
    var stringOptions: StringFieldOptions? = undefined

    fun string(block: StringFieldOptions.() -> Unit) {
        val opts = StringFieldOptions()
        opts.block()
        stringOptions = opts
    }
}

class NumberArrayConfiguration<T : Number> : BaseArrayConfiguration<T>() {
    var numberOptions: NumberFieldOptions? = undefined

    fun int(block: NumberFieldOptions.() -> Unit) {
        val opts = NumberFieldOptions(integer = true)
        opts.block()
        numberOptions = opts
    }

    fun double(block: NumberFieldOptions.() -> Unit) {
        val opts = NumberFieldOptions()
        opts.block()
        numberOptions = opts
    }
}

class BooleanArrayConfiguration : BaseArrayConfiguration<Boolean>() {
    var booleanOptions: DataFieldOptions/*<Boolean>*/? = undefined

    fun boolean(block: DataFieldOptions/*<Boolean>*/.() -> Unit) {
        val opts = DataFieldOptions/*<Boolean>*/()
        opts.block()
        booleanOptions = opts
    }
}

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

    fun int(
        name: String,
        context: DataFieldContext<Double>? = undefined,
        block: (NumberFieldOptions.() -> Unit)? = null,
    ) {
        val options = NumberFieldOptions(integer = true)
        block?.invoke(options)
        fields[name] = NumberField(options = options, context = context)
    }

    fun double(
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

    fun stringArray(
        name: String,
        context: DataFieldContext<Array<String>>? = undefined,
        fieldContext: DataFieldContext<String>? = undefined,
        block: StringArrayConfiguration.() -> Unit,
    ) {
        val opts = StringArrayConfiguration()
        opts.block()
        val element = StringField(options = opts.stringOptions, context = fieldContext)
        fields[name] = ArrayField(element = element, options = opts.arrayOptions, context = context)
    }

    fun <T : Number> numberArray(
        name: String,
        context: DataFieldContext<Array<T>>? = undefined,
        fieldContext: DataFieldContext<T>? = undefined,
        block: NumberArrayConfiguration<T>.() -> Unit,
    ) {
        val opts = NumberArrayConfiguration<T>()
        opts.block()
        val element = NumberField(options = opts.numberOptions, context = fieldContext)
        fields[name] = ArrayField(element = element, options = opts.arrayOptions, context = context)
    }

    fun booleanArray(
        name: String,
        context: DataFieldContext<Array<Boolean>>? = undefined,
        fieldContext: DataFieldContext<Boolean>? = undefined,
        block: BooleanArrayConfiguration.() -> Unit,
    ) {
        val opts = BooleanArrayConfiguration()
        opts.block()
        val element = BooleanField(options = opts.booleanOptions, context = fieldContext)
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