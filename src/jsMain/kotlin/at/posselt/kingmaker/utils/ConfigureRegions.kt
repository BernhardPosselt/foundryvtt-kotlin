@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package at.posselt.kingmaker.utils

import com.foundryvtt.core.*
import js.core.Void
import js.objects.recordOf
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.HTMLFormElement
import kotlin.js.Promise


@JsName("ConfigureRegions")
class ConfigureRegions : HandlebarsApplication() {
    companion object {
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        var PARTS = recordOf(
            "form" to HandlebarsTemplatePart(
                template = resolveTemplatePath("applications/settings/configure-regions.hbs")
            )
        )

        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        val DEFAULT_OPTIONS = ApplicationConfiguration(
            tag = "form",
            window = Window(
                title = "Hey Jose"
            ),
            form = ApplicationFormConfiguration(
                handler = ::sub,
                submitOnChange = false,
                closeOnSubmit = false,
            )
        )

        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun sub(event: Event, form: HTMLFormElement, formData: FormDataExtended<AnyObject>): Promise<Void> =
            buildPromise {
                console.log(event, form, formData)
                null
            }
    }
}