package at.posselt.kingmaker.app

import at.posselt.kingmaker.app.CheckboxInput
import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.FormElementContext
import at.posselt.kingmaker.app.HandlebarsRenderContext
import at.posselt.kingmaker.camping.getAllRecipes
import at.posselt.kingmaker.camping.getCamping
import at.posselt.kingmaker.camping.setCamping
import at.posselt.kingmaker.utils.asSequence
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.buildUuid
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.ui.TextEditor
import com.foundryvtt.core.utils.flattenObject
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.array.toTypedArray
import js.core.Void
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise


@JsPlainObject
external interface CrudItem {
    val id: String
    val name: String
    val additionalColumns: Array<String>
    val enable: FormElementContext
    val canBeEdited: Boolean
    val canBeDeleted: Boolean
}

@JsPlainObject
external interface CrudTemplateContext : HandlebarsRenderContext {
    val items: Array<CrudItem>
    val additionalColumnHeadings: Array<String>
}

@JsPlainObject
external interface CrudData {
    val enabledIds: Array<String>
}

abstract class CrudApplication(
    title: String,
    width: Int? = undefined,
) : FormApp<CrudTemplateContext, CrudData>(
    title = title,
    template = "components/forms/crud-form.hbs",
    width = width,
    submitOnChange = false,
) {
    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "delete" -> {
                val id = target.dataset["id"]
                checkNotNull(id)
                buildPromise {
                    deleteEntry(id)
                }
            }
            "edit" -> {
                val id = target.dataset["id"]
                checkNotNull(id)
                buildPromise {
                    editEntry(id)
                }
            }
        }
    }

    protected abstract fun deleteEntry(id: String): Promise<Void>
    protected abstract fun editEntry(id: String): Promise<Void>

    override fun fixObject(value: dynamic) {
        val ids = (value["enabledIds"] ?: jso()).unsafeCast<Record<String, Boolean>>()
        value["enabledIds"] = flattenObject(ids).asSequence()
            .filter { it.component2() == true }
            .map { it.component1() }
            .toTypedArray()
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<CrudTemplateContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        CrudTemplateContext(
            partId = parent.partId,
            additionalColumnHeadings = getHeadings().await(),
            items = getItems().await()
        )
    }

    protected abstract fun getItems(): Promise<Array<CrudItem>>
    protected abstract fun getHeadings(): Promise<Array<String>>
}