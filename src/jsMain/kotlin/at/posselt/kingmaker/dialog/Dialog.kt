package at.posselt.kingmaker.dialog

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
    submit: suspend (T) -> R
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