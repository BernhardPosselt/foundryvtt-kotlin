package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.CheckboxInput
import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.FormElementContext
import at.posselt.kingmaker.app.HandlebarsRenderContext
import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.toOption
import at.posselt.kingmaker.camping.ActivityEffect
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.data.dsl.buildSchema
import com.foundryvtt.pf2e.item.PF2EEffect
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@OptIn(ExperimentalJsExport::class)
@JsExport
class ActivityEffectDataModel(value: AnyObject) : DataModel(value) {
    companion object {
        @Suppress("unused")
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun defineSchema() = buildSchema {
            string("uuid")
            string("target") {
                choices = arrayOf("all", "self", "allies")
            }
            boolean("doublesHealing")
        }
    }
}

@JsPlainObject
external interface ActivityEffectContext : HandlebarsRenderContext {
    val formRows: Array<FormElementContext>
}

enum class ActivityEffectTarget {
    ALLIES,
    ALL,
    SELF
}

@JsPlainObject
external interface ActivityEffectSubmitData {
    val uuid: String
    val target: String
    val doublesHealing: Boolean
}


@OptIn(ExperimentalJsExport::class)
@JsExport
class ActivityEffectApplication(
    private val game: Game,
    data: ActivityEffect? = null,
    private val afterSubmit: (ActivityEffect) -> Unit,
) : FormApp<ActivityEffectContext, ActivityEffectSubmitData>(
    title = if (data == null) "Add Effect" else "Edit Effect",
    template = "components/forms/application-form.hbs",
    debug = true,
    dataModel = ActivityEffectDataModel::class.js,
) {
    var currentActivityEffect: ActivityEffect? = data

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "openDocumentLink" -> buildPromise {
                event.preventDefault()
                event.stopPropagation()
                target.dataset["uuid"]?.let { fromUuidTypeSafe<PF2EEffect>(it)?.sheet?.launch() }
            }

            "save" -> save()
        }
    }

    fun save(): Promise<Void> = buildPromise {
        val effect = currentActivityEffect
        if (isValid() && effect != null) {
            close().await()
            afterSubmit(effect)
        }
        undefined
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<ActivityEffectContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val effects = game.items.contents
            .filterIsInstance<PF2EEffect>()
        val item = currentActivityEffect?.uuid?.let { fromUuidTypeSafe<PF2EEffect>(it) }
            ?: effects.firstOrNull()
        ActivityEffectContext(
            partId = parent.partId,
            formRows = formContext(
                Select(
                    label = "Effect",
                    name = "uuid",
                    stacked = false,
                    options = effects.mapNotNull { it.toOption(useUuid = true) },
                    item = item,
                    value = item?.uuid,
                ),
                Select.fromEnum<ActivityEffectTarget>(
                    label = "Target",
                    name = "target",
                    stacked = false,
                ),
                CheckboxInput(
                    name = "doublesHealing",
                    label = "Doubles Healing",
                    stacked = false,
                )
            )
        )
    }

    override fun onParsedSubmit(value: ActivityEffectSubmitData): Promise<Void> = buildPromise {
        currentActivityEffect = ActivityEffect(
            uuid = value.uuid,
            target = value.target,
            doublesHealing = value.doublesHealing,
        )
        undefined
    }

}