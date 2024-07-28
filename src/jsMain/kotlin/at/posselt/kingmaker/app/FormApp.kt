package at.posselt.kingmaker.app

import at.posselt.kingmaker.utils.resolveTemplatePath
import com.foundryvtt.core.applications.api.ApplicationFormConfiguration
import com.foundryvtt.core.applications.api.ApplicationHeaderControlsEntry
import com.foundryvtt.core.applications.api.ApplicationPosition
import com.foundryvtt.core.applications.api.Window
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent

interface Action<T : Enum<T>> {
//    abstract companion object {
//        abstract fun fromString(): T?
//    }
}

data class MenuControl(
    val label: String,
    val icon: String?,
    val action: String
)

abstract class FormApp<A : Enum<A>>(
    title: String,
    template: String,
    isDialogForm: Boolean = true,
    submitOnChange: Boolean = true,
    closeOnSubmit: Boolean = false,
    controls: Array<MenuControl> = emptyArray(),
    width: Int? = null,
) : App<HandlebarsFormApplicationOptions>(
    HandlebarsFormApplicationOptions(
        window = Window(
            title = title,
            controls = controls.map {
                ApplicationHeaderControlsEntry(
                    label = it.label,
                    icon = it.icon,
                    action = it.action
                )
            }.toTypedArray()
        ),
        position = ApplicationPosition(
            width = width,
        ),
        templatePath = resolveTemplatePath(template),
        classes = if (isDialogForm) arrayOf("km-dialog-form") else emptyArray(),
        tag = "form",
        form = ApplicationFormConfiguration(
            submitOnChange = submitOnChange,
            closeOnSubmit = closeOnSubmit,
        )
    )
) {
    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        val actionString = target.dataset["action"] as String
//        val enum = A.
    }
}
