package at.posselt.kingmaker.dialog

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.expandObject
import js.objects.recordOf
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
    val hideLabel: Boolean
}

data class SelectOption(
    val label: String,
    val value: String
)

sealed interface FormRow {
    val label: String
    val name: String
    val help: String?
    val hideLabel: Boolean

    fun toContext(): RowContext
}

data class Select(
    override val label: String,
    override val name: String,
    val value: String? = null,
    val options: List<SelectOption>,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : FormRow {
    override fun toContext() = RowContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = true,
        number = false,
        text = false,
        textArea = false,
        checkbox = false,
        options = options.map { opt ->
            Option(
                label = opt.label,
                value = opt.value
            )
        }.toTypedArray(),
        hideLabel = hideLabel,
    )
}

data class TextInput(
    override val label: String,
    override val name: String,
    val value: String,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : FormRow {
    override fun toContext() = RowContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        number = false,
        text = true,
        textArea = false,
        checkbox = false,
        options = emptyArray(),
        hideLabel = hideLabel,
    )
}

data class CheckboxInput(
    override val label: String,
    override val name: String,
    val value: Boolean = false,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : FormRow {
    override fun toContext() = RowContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        number = false,
        text = false,
        textArea = false,
        checkbox = true,
        options = emptyArray(),
        hideLabel = hideLabel,
    )
}

data class TextArea(
    override val label: String,
    override val name: String,
    val value: String,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : FormRow {
    override fun toContext() = RowContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        number = false,
        text = false,
        textArea = true,
        checkbox = false,
        options = emptyArray(),
        hideLabel = hideLabel,
    )
}

data class NumberInput(
    override val label: String,
    override val name: String,
    val value: Int = 0,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : FormRow {
    override fun toContext() = RowContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        number = true,
        text = false,
        textArea = false,
        checkbox = false,
        options = emptyArray(),
        hideLabel = hideLabel,
    )
}

fun towDimensionalContext(
    heading: Array<String>,
    rows: Array<Array<FormRow>>
) = recordOf(
    "heading" to heading,
    "formRows" to rows,
)

/**
 * Custom function to build a form declaratively rather than having
 * one template for each form
 */
fun formContext(vararg rows: FormRow): Array<RowContext> =
    rows.map { it.toContext() }.toTypedArray()


/**
 * Wrapper around expandObject that allows additional
 * transformations and fixing until the object reaches its final
 * good state
 */
fun <T> expandObjectAnd(value: AnyObject, and: (AnyObject) -> Unit): T {
    val result = expandObject(value)
    and(result)
    @Suppress("UNCHECKED_CAST")
    return result as T
}