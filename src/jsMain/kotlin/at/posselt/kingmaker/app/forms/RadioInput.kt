package at.posselt.kingmaker.app.forms

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
    val labelElement: String = "label",
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
        component = false,
        labelElement = labelElement,
    )
}

