package at.posselt.kingmaker.app

import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.deCamelCase
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.toLabel
import at.posselt.kingmaker.utils.asSequence
import at.posselt.kingmaker.utils.isJsObject
import at.posselt.kingmaker.utils.toMutableRecord
import at.posselt.kingmaker.utils.toRecord
import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.documents.*
import com.foundryvtt.core.utils.expandObject
import js.array.toTypedArray
import js.objects.Object
import js.objects.ReadonlyRecord
import kotlinx.datetime.*
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.js.JsPlainObject
import kotlin.enums.enumEntries

@JsPlainObject
external interface Option {
    val label: String
    val value: String
    val selected: Boolean
    val classes: String
}

@JsPlainObject
external interface SectionsContext {
    val sections: Array<SectionContext>
}

@JsPlainObject
external interface SectionContext {
    val legend: String
    val formRows: Array<FormElementContext>
}

@JsPlainObject
external interface DocumentLinkContext {
    val uuid: String
    val img: String?
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
    val time: Boolean
    val text: Boolean
    val textArea: Boolean
    val checkbox: Boolean
    val options: Array<Option>
    val hideLabel: Boolean
    val overrideType: String?
    val isFormElement: Boolean
    val elementClasses: String
    val disabled: Boolean
    val stacked: Boolean
    val menu: Boolean
    val hidden: Boolean
    val radio: Boolean
    val link: DocumentLinkContext?
    val escapeLabel: Boolean
    val image: Boolean
}

enum class OverrideType(val value: String) {
    NUMBER("Number"),
    BOOLEAN("Boolean"),
}

data class SelectOption(
    val label: String,
    val value: String,
    val classes: List<String> = emptyList(),
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
    val overrideType: OverrideType? = null,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = true,
    val actor: Actor? = null,
    val item: Item? = null,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = true,
        required = required,
        number = false,
        text = false,
        time = false,
        textArea = false,
        checkbox = false,
        radio = false,
        image = false,
        disabled = disabled,
        stacked = stacked,
        overrideType = overrideType?.value,
        options = options.map { opt ->
            Option(
                label = opt.label,
                value = opt.value,
                selected = opt.value == value,
                classes = opt.classes.joinToString(" "),
            )
        }.toTypedArray(),
        hideLabel = hideLabel,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        link = if (actor != null) {
            DocumentLinkContext(uuid = actor.uuid, img = actor.img)
        } else if (item != null) {
            DocumentLinkContext(uuid = item.uuid, img = item.img)
        } else {
            null
        },
        escapeLabel = escapeLabel,
    )

    companion object {
        fun flatCheck(
            label: String,
            name: String,
            value: Int? = null,
            required: Boolean = true,
            help: String? = null,
            hideLabel: Boolean = false,
            disabled: Boolean = false,
            stacked: Boolean = true,
            elementClasses: List<String> = emptyList(),
        ) = Select(
            label = label,
            name = name,
            value = value.toString(),
            required = required,
            help = help,
            elementClasses = elementClasses,
            hideLabel = hideLabel,
            overrideType = OverrideType.NUMBER,
            options = generateSequence(1) { it + 1 }
                .take(20)
                .map { SelectOption(it.toString(), it.toString()) }
                .toList(),
            disabled = disabled,
            stacked = stacked,
        )

        fun dc(
            label: String = "DC",
            name: String = "dc",
            value: Int? = null,
            required: Boolean = true,
            help: String? = null,
            hideLabel: Boolean = false,
            disabled: Boolean = false,
            stacked: Boolean = true,
            elementClasses: List<String> = emptyList(),
        ) = Select(
            label = label,
            name = name,
            value = value.toString(),
            required = required,
            help = help,
            hideLabel = hideLabel,
            overrideType = OverrideType.NUMBER,
            options = generateSequence(0) { it + 1 }
                .take(61)
                .map { SelectOption(it.toString(), it.toString()) }
                .toList(),
            disabled = disabled,
            stacked = stacked,
            elementClasses = elementClasses,
        )

        fun level(
            label: String = "Level",
            name: String = "level",
            value: Int? = null,
            required: Boolean = true,
            help: String? = null,
            hideLabel: Boolean = false,
            disabled: Boolean = false,
            stacked: Boolean = true,
            elementClasses: List<String> = emptyList(),
        ) = Select(
            label = label,
            name = name,
            value = value.toString(),
            required = required,
            help = help,
            hideLabel = hideLabel,
            overrideType = OverrideType.NUMBER,
            options = generateSequence(-1) { it + 1 }
                .take(27)
                .map { SelectOption(it.toString(), it.toString()) }
                .toList(),
            disabled = disabled,
            stacked = stacked,
            elementClasses = elementClasses,
        )

        inline fun <reified T : Enum<T>> fromEnum(
            label: String,
            name: String,
            value: T? = null,
            required: Boolean = true,
            help: String? = null,
            hideLabel: Boolean = false,
            labelFunction: (T) -> String = { it.toLabel() },
            disabled: Boolean = false,
            stacked: Boolean = true,
            elementClasses: List<String> = emptyList(),
        ) = Select(
            name = name,
            label = label,
            value = value?.toCamelCase(),
            required = required,
            help = help,
            hideLabel = hideLabel,
            options = enumToOptions<T>(labelFunction),
            disabled = disabled,
            stacked = stacked,
            elementClasses = elementClasses,
        )
    }
}

data class TextInput(
    override val label: String,
    override val name: String,
    val value: String,
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val overrideType: OverrideType? = null,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = true,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        required = required,
        number = false,
        text = true,
        time = false,
        textArea = false,
        image = false,
        checkbox = false,
        disabled = disabled,
        overrideType = overrideType?.value,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        radio = false,
        escapeLabel = escapeLabel,
    )
}

data class HiddenInput(
    override val label: String = "",
    override val name: String,
    val value: String,
    override val help: String? = null,
    override val hideLabel: Boolean = true,
    val overrideType: OverrideType? = null,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = false,
        hidden = true,
        number = false,
        text = false,
        textArea = false,
        checkbox = false,
        image = false,
        radio = false,
        disabled = false,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = true,
        elementClasses = "",
        menu = false,
        escapeLabel = escapeLabel,
    )
}


data class CheckboxInput(
    override val label: String,
    override val name: String,
    val value: Boolean = false,
    val required: Boolean = false,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = false,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = required,
        number = false,
        text = false,
        textArea = false,
        checkbox = true,
        image = false,
        radio = false,
        disabled = disabled,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        escapeLabel = escapeLabel,
    )
}

data class RadioInput(
    override val label: String,
    override val name: String,
    val value: Boolean = false,
    val required: Boolean = false,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = false,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = required,
        number = false,
        text = false,
        textArea = false,
        checkbox = false,
        image = false,
        radio = true,
        disabled = disabled,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        escapeLabel = escapeLabel,
    )
}

data class ImageInput(
    override val label: String,
    override val name: String,
    val value: String,
    val required: Boolean = false,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = false,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = required,
        number = false,
        text = false,
        textArea = false,
        checkbox = false,
        radio = false,
        image = true,
        disabled = disabled,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        escapeLabel = false,
    )
}

data class TextArea(
    override val label: String,
    override val name: String,
    val value: String,
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val overrideType: OverrideType? = null,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = true,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = required,
        disabled = disabled,
        number = false,
        text = false,
        textArea = true,
        radio = false,
        image = false,
        checkbox = false,
        options = emptyArray(),
        overrideType = overrideType?.value,
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        escapeLabel = escapeLabel,
    )
}

data class NumberInput(
    override val label: String,
    override val name: String,
    val value: Int = 0,
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = true,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = required,
        disabled = disabled,
        number = true,
        text = false,
        image = false,
        textArea = false,
        checkbox = false,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        radio = false,
        escapeLabel = escapeLabel,
    )
}

data class TimeInput(
    override val label: String,
    override val name: String,
    val value: LocalTime,
    val required: Boolean = true,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = true,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value.format(LocalTime.Format {
            hour(padding = Padding.ZERO)
            char(':')
            minute(padding = Padding.ZERO)
        }),
        select = false,
        time = true,
        required = required,
        number = false,
        disabled = disabled,
        text = false,
        radio = false,
        image = false,
        textArea = false,
        checkbox = false,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = false,
        hidden = false,
        escapeLabel = escapeLabel,
    )
}

data class Menu(
    override val label: String,
    override val name: String,
    val value: String,
    override val help: String? = null,
    override val hideLabel: Boolean = false,
    val elementClasses: List<String> = emptyList(),
    val disabled: Boolean = false,
    val stacked: Boolean = true,
    val escapeLabel: Boolean = true,
) : IntoFormElementContext {
    override fun toContext() = FormElementContext(
        isFormElement = true,
        label = label,
        name = name,
        help = help,
        value = value,
        select = false,
        time = false,
        required = false,
        number = false,
        disabled = disabled,
        text = false,
        image = false,
        textArea = false,
        checkbox = false,
        radio = false,
        options = emptyArray(),
        hideLabel = hideLabel,
        stacked = stacked,
        elementClasses = elementClasses.joinToString(" "),
        menu = true,
        hidden = false,
        escapeLabel = escapeLabel,
    )
}

data class Section(
    val legend: String,
    val formRows: List<IntoFormElementContext>
) {
    fun toContext(): SectionContext = SectionContext(
        legend = legend,
        formRows = formRows.map(IntoFormElementContext::toContext).toTypedArray()
    )
}

/**
 * Custom function to build a form declaratively rather than having
 * one template for each form
 */
fun formContext(vararg rows: IntoFormElementContext): Array<FormElementContext> =
    rows.map { it.toContext() }.toTypedArray()


fun formContext(vararg rows: Section): Array<SectionContext> =
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
fun <T> normalizeArrays(obj: ReadonlyRecord<String, T>): Any {
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

fun RollTable.toOption(useUuid: Boolean = false) =
    if (useUuid) {
        SelectOption(label = name, value = uuid)
    } else {
        id?.let {
            SelectOption(label = name, value = it)
        }
    }

fun Playlist.toOption(useUuid: Boolean = false) =
    if (useUuid) {
        SelectOption(label = name, value = uuid)
    } else {
        id?.let {
            SelectOption(label = name, value = it)
        }
    }

fun PlaylistSound.toOption(useUuid: Boolean = false) =
    if (useUuid) {
        SelectOption(label = name, value = uuid)
    } else {
        id?.let {
            SelectOption(label = name, value = it)
        }
    }

fun Actor.toOption(useUuid: Boolean = false) =
    if (useUuid) {
        SelectOption(label = name, value = uuid)
    } else {
        id?.let {
            SelectOption(label = name, value = it)
        }
    }

fun Item.toOption(useUuid: Boolean = false) =
    if (useUuid) {
        name?.let {
            SelectOption(label = it, value = uuid)
        }
    } else {
        id?.let { id ->
            name?.let { name ->
                SelectOption(label = name, value = id)
            }
        }
    }

inline fun <reified T : Enum<T>> enumToOptions(labelFunction: (T) -> String = { it.toLabel() }) =
    enumEntries<T>().map {
        SelectOption(
            label = labelFunction(it),
            value = it.toCamelCase()
        )
    }

fun Perception.toOption() =
    SelectOption(
        label = value.deCamelCase(),
        value = value,
    )