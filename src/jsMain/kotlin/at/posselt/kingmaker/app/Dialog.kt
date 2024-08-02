package at.posselt.kingmaker.app

import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.tpl
import com.foundryvtt.core.*
import com.foundryvtt.core.applications.api.*
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await


suspend fun <T, R> confirm(
    title: String,
    confirmButtonLabel: String = "Ok",
    cancelButtonLabel: String = "Cancel",
    templatePath: String,
    templateContext: Record<String, Any?> = jso(),
    await: Boolean = false,
    submit: suspend (T) -> R
) {
    val content = tpl(templatePath, templateContext)
    val confirmButton = DialogV2Button(
        action = "ok",
        label = confirmButtonLabel,
        default = true,
    ) { ev, button, dialog ->
        val data = FormDataExtended<T>(button.form!!)
        buildPromise {
            submit(data.`object`)
        }
    }
    val cancelButton = DialogV2Button(
        action = "cancel",
        label = cancelButtonLabel,
        default = true,
    )
    val prompt = DialogV2.confirm(
        ConfirmOptions(
            content = content,
            classes = arrayOf("km-dialog-form"),
            window = Window(title = title),
            yes = confirmButton,
            no = cancelButton,
            rejectClose = false,
        )
    )
    if (await) prompt.await()
}

enum class PromptType(val label: String, val icon: String? = null) {
    ROLL("Roll", "fa-solid fa-dice-d20"),
    OK("Ok"),
}

suspend fun <T, R> prompt(
    title: String,
    buttonLabel: String? = null,
    templatePath: String,
    templateContext: Record<String, Any?> = jso(),
    await: Boolean = false,
    promptType: PromptType = PromptType.OK,
    submit: suspend (T) -> R,
) {
    val content = tpl(templatePath, templateContext)
    val button = DialogV2Button(
        action = "ok",
        label = buttonLabel ?: promptType.label,
        default = true,
        icon = promptType.icon,
    ) { ev, button, dialog ->
        val data = FormDataExtended<T>(button.form!!)
        buildPromise {
            submit(data.`object`)
        }
    }
    val prompt = DialogV2.prompt(
        PromptOptions(
            content = content,
            classes = arrayOf("km-dialog-form"),
            window = Window(title = title),
            ok = button,
            rejectClose = false,
        )
    )
    if (await) prompt.await()
}

data class WaitButton<T, R>(
    val label: String,
    val action: String? = null,
    val icon: String? = undefined,
    val callback: suspend (data: T, action: String) -> R,
)

suspend fun <T, R> wait(
    title: String,
    templatePath: String,
    templateContext: Record<String, Any?> = jso(),
    await: Boolean = false,
    buttons: List<WaitButton<T, R>>,
) {
    val content = tpl(templatePath, templateContext)
    val v2Buttons = buttons.mapIndexed { index, button ->
        val action = button.action ?: button.label
        DialogV2Button(
            action = action,
            label = button.label,
            icon = button.icon,
            callback = { ev, btn, dialog ->
                val data = FormDataExtended<T>(btn.form!!)
                buildPromise {
                    button.callback(data.`object`, action)
                }
            },
            default = index == buttons.size - 1,
        )
    }.toTypedArray()
    val prompt = DialogV2.wait(
        WaitOptions(
            content = content,
            classes = arrayOf("km-dialog-form"),
            window = Window(title = title),
            buttons = v2Buttons,
            rejectClose = false,
        )
    )
    if (await) prompt.await()
}