package at.posselt.kingmaker.app

import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.toLabel
import at.posselt.kingmaker.utils.asSequence
import at.posselt.kingmaker.utils.isJsObject
import at.posselt.kingmaker.utils.toMutableRecord
import at.posselt.kingmaker.utils.toRecord
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.documents.Playlist
import com.foundryvtt.core.documents.PlaylistSound
import com.foundryvtt.core.documents.RollTable
import com.foundryvtt.core.utils.expandObject
import js.array.toTypedArray
import js.objects.Object
import js.objects.Record
import js.objects.recordOf
import kotlinx.js.JsPlainObject
import kotlin.enums.enumEntries

@JsPlainObject
external interface Option {
    val label: String
    val value: String
}


@JsPlainObject
external interface FormElementContext {
    val label: String
    val name: String
    val help: String?
    val value: Any?
    val select: Boolean
    val required: Boolean
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

sealed interface IntoFormElementContext {
    val label: String
    val name: String
    val help: String?
    val hideLabel: Boolean

    fun toContext(): FormElementContext
}

data class Select(
    override val label: String,
    override val name: String,
    val value: String? = null,
    val options: List<SelectOption>,
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = true,
        required = required,
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
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        required = required,
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
    val required: Boolean = false,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        required = required,
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
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        required = required,
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
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        required = required,
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
    rows: Array<Array<IntoFormElementContext>>
) = recordOf(
    "heading" to heading,
    "formRows" to rows,
)

/**
 * Custom function to build a form declaratively rather than having
 * one template for each form
 */
fun formContext(vararg rows: IntoFormElementContext): Array<FormElementContext> =
    rows.map { it.toContext() }.toTypedArray()


/**
 * Wrapper around expandObject that allows additional
 * transformations and fixing until the object reaches its final
 * good state
 */
fun <T> parseFormData(value: AnyObject, and: (dynamic) -> Unit): T {
    val filteredBlanks = value.asSequence()
        .filter {
            val rhs = it.component2()
            if (rhs is String) rhs.isNotEmpty() else true
        }
        .toMutableRecord()
    val expanded = expandObject(filteredBlanks)
    val result = normalizeArrays(expanded)
    and(result)
    @Suppress("UNCHECKED_CAST")
    return result as T
}


/**
 * This utility is needed to dynamically and recursively convert nested objects
 * with integer keys into arrays since that's how Foundry handles forms
 *
 * @return either a Record or an array if the top level object was an array
 */
@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun <T> normalizeArrays(obj: Record<String, T>): Any {
    if (Object.hasOwn(obj, 0)) {
        return obj.asSequence()
            .sortedBy { it.component1().toInt() }
            .map {
                val value = it.component2()
                if (isJsObject(value)) {
                    normalizeArrays(value as AnyObject)
                } else {
                    value
                }
            }
            .toTypedArray()
    } else {
        return obj.asSequence()
            .map {
                val value = it.component2()
                val normalizedValue = if (isJsObject(value)) {
                    normalizeArrays(value as AnyObject)
                } else {
                    value
                }
                it.component1() to normalizedValue
            }
            .toRecord()
    }
}

fun RollTable.toOption() = id?.let {
    SelectOption(label = name, value = it)
}

fun Playlist.toOption() = id?.let {
    SelectOption(label = name, value = it)
}

fun PlaylistSound.toOption() = id?.let {
    SelectOption(label = name, value = it)
}

inline fun <reified T : Enum<T>> Enum<T>.toSelect(
    label: String,
    name: String,
    value: T? = null,
    required: Boolean = true,
    help: String? = null,
    hideLabel: Boolean = false,
) =
    Select(
        name = name,
        label = label,
        value = value?.toCamelCase(),
        required = required,
        help = help,
        hideLabel = hideLabel,
        options = enumEntries<T>().map {
            it.toOption()
        }
    )

fun <T : Enum<T>> Enum<T>.toOption() =
    SelectOption(label = toLabel(), value = toCamelCase())
