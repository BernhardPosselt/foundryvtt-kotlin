package com.foundryvtt.core.data.dsl

//
//class Schema {
//    val fields = mutableMapOf<String, DataField<out Any>>()
//
//    fun string(
//        name: String,
//        context: DataFieldContext<String>? = undefined,
//        block: StringFieldOptions.() -> Unit,
//    ) {
//        val options = StringFieldOptions()
//        options.block()
//        fields[name] = StringField(options = options, context = context)
//    }
//
//    fun schema(
//        name: String,
////        options: DataFieldOptions<T>? = undefined,
////        context: DataFieldContext<T>? = undefined,
//        block: Schema.() -> Unit
//    ) {
//        val schema = Schema()
//        schema.block()
//        fields[name] = SchemaField<Any>(
//            fields = schema.build(),
////            options = options,
////            context = context,
//        )
//    }
//
//    fun <T> build(): DataSchema<T> {
//        return fields.asSequence()
//            .map { it.key to it.value.asDynamic() }
//            .toRecord()
//    }
//}
//
//fun schema(block: Schema.() -> Unit): DataSchema<out Any> {
//    val schema = Schema()
//    schema.block()
//    return schema.build()
//}

fun blubb() {
//    val s = schema {
//        string("value") {
//            required = true
//            blank = false
//        }
//        schema("field") {
//            string("name") {}
//        }
//    }
//    console.log(s)
}