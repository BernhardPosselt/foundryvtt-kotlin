package com.foundryvtt

import com.foundryvtt.core.data.fields.ArrayField
import com.foundryvtt.core.data.fields.DataField
import com.foundryvtt.core.data.fields.NumberField
import com.foundryvtt.core.data.fields.NumberFieldOptions
import com.foundryvtt.core.data.fields.SchemaField
import com.foundryvtt.core.data.fields.StringField
import com.foundryvtt.core.data.fields.StringFieldOptions
import com.foundryvtt.core.data.fields.TypeDataModel
import js.objects.recordOf
import kotlin.reflect.KProperty

class ExampleData : TypeDataModel {
    companion object {
        @JsStatic
        fun schema() = recordOf(
            "nullableString" to StringField(StringFieldOptions(nullable = true, initial = null, blank = false)),
            "nonNullString" to StringField(StringFieldOptions(nullable = false, required = true)),
            "stringDefault" to StringField(StringFieldOptions(nullable = false, required = true, initial = "default")),
            "stringEnum" to StringField(
                StringFieldOptions(
                    nullable = false,
                    required = true,
                    choices = arrayOf("one", "two")
                )
            ),
            "stringArray" to ArrayField(StringField(StringFieldOptions(nullable = false, required = true))),
            "integerField" to NumberField(
                NumberFieldOptions<Int>(
                    nullable = false,
                    required = true,
                    min = 0,
                    max = 1,
                    integer = true
                )
            ),
            "numberField" to NumberField(
                NumberFieldOptions<Int>(
                    nullable = false,
                    required = true,
                )
            ),
//    "filePathField" to FilePathField(),
            "schemaField" to SchemaField(
                recordOf(
                    Pair<String, NumberField<Int>>(
                        "nested",
                        NumberField(NumberFieldOptions<Int>(required = true, nullable = false))
                    )
                )
            )
        )
    }
}

