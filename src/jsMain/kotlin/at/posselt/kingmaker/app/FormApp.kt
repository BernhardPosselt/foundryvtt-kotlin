package at.posselt.kingmaker.app

import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.resolveTemplatePath
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.FormDataExtended
import com.foundryvtt.core.applications.api.ApplicationFormConfiguration
import com.foundryvtt.core.applications.api.ApplicationHeaderControlsEntry
import com.foundryvtt.core.applications.api.ApplicationPosition
import com.foundryvtt.core.applications.api.Window
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.HTMLFormElement
import kotlin.js.Promise

data class MenuControl(
    val label: String,
    val icon: String?,
    val action: String
)

abstract class FormApp<T, O>(
    title: String,
    template: String,
    isDialogForm: Boolean = true,
    submitOnChange: Boolean = true,
    closeOnSubmit: Boolean = false,
    controls: Array<MenuControl> = emptyArray(),
    width: Int? = undefined,
    id: String? = undefined,
    val debug: Boolean = false,
) : App<T>(
    HandlebarsFormApplicationOptions(
        id = id,
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
    protected var isFormValid = true

    override fun onSubmit(event: Event, form: HTMLFormElement, formData: FormDataExtended<AnyObject>): Promise<Void> =
        buildPromise {
            val value = formData.`object`
            isFormValid = form.reportValidity()
            if (debug) {
                console.log("Received ${JSON.stringify(value)}")
                console.log("Form is ${if (isFormValid) "valid" else "invalid"}")
            }
            val parsedData = parseFormData<O>(value, ::fixObject)
            if (debug) {
                console.log("Parsed object ${JSON.stringify(parsedData)}")
            }
            onParsedSubmit(parsedData).await()
            render()
            null
        }

    protected open fun fixObject(value: dynamic) {

    }

    protected abstract fun onParsedSubmit(value: O): Promise<Void>
}
