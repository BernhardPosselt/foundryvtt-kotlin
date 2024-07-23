package at.posselt.kingmaker.dialog

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface Option {
    val label: String
    val value: String
}


@JsPlainObject
external interface RowContext {
    val label: String
    val name: String
    val help: String?
    val value: Any?
    val select: Boolean
    val number: Boolean
    val text: Boolean
    val textArea: Boolean
    val checkbox: Boolean
    val options: Array<Option>
}

data class SelectOption(
    val label: String,
    val value: String
)

sealed interface FormRow {
    val label: String
    val name: String
    val help: String?
}

data class Select(
    override val label: String,
    override val name: String,
    val value: String? = null,
    val options: List<SelectOption>,
    override val help: String? = null,
) : FormRow

data class TextInput(
    override val label: String,
    override val name: String,
    val value: String,
    override val help: String? = null
) : FormRow

data class CheckboxInput(
    override val label: String,
    override val name: String,
    val value: Boolean = false,
    override val help: String? = null
) : FormRow

data class TextArea(
    override val label: String,
    override val name: String,
    val value: String,
    override val help: String? = null
) : FormRow

data class NumberInput(
    override val label: String,
    override val name: String,
    val value: Int = 0,
    override val help: String? = null
) : FormRow

/**
 * Custom function to build a form declaratively rather than having
 * one template for each form
 */
fun formContext(vararg rows: FormRow): Array<RowContext> =
    rows.map {
        when (it) {
            is NumberInput -> RowContext(
                label = it.label,
                name = it.name,
                help = it.help,
                value = it.value,
                select = false,
                number = true,
                text = false,
                textArea = false,
                checkbox = false,
                options = emptyArray(),
            )

            is Select -> RowContext(
                label = it.label,
                name = it.name,
                help = it.help,
                value = it.value,
                select = true,
                number = false,
                text = false,
                textArea = false,
                checkbox = false,
                options = it.options.map { opt ->
                    Option(
                        label = opt.label,
                        value = opt.value
                    )
                }.toTypedArray(),
            )

            is TextInput -> RowContext(
                label = it.label,
                name = it.name,
                help = it.help,
                value = it.value,
                select = false,
                number = false,
                text = true,
                textArea = false,
                checkbox = false,
                options = emptyArray(),
            )

            is TextArea -> RowContext(
                label = it.label,
                name = it.name,
                help = it.help,
                value = it.value,
                select = false,
                number = false,
                text = false,
                textArea = true,
                checkbox = false,
                options = emptyArray(),
            )

            is CheckboxInput -> RowContext(
                label = it.label,
                name = it.name,
                help = it.help,
                value = it.value,
                select = false,
                number = false,
                text = false,
                textArea = false,
                checkbox = true,
                options = emptyArray(),
            )
        }
    }.toTypedArray()