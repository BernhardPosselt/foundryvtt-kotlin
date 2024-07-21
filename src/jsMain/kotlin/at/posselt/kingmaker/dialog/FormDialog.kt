package at.posselt.kingmaker.dialog

import at.posselt.kingmaker.buildPromise
import at.posselt.kingmaker.tpl
import com.foundryvtt.core.*
import js.objects.jso
import kotlinx.coroutines.await

suspend fun <T, R> prompt(
    title: String,
    buttonLabel: String = "Ok",
    templatePath: String,
    templateContext: Any = jso(),
    await: Boolean = false,
    submit: suspend (T) -> R
) {
    val content = tpl(templatePath, templateContext)
    val button = DialogV2Button(
        action = "ok",
        label = buttonLabel,
        default = true,
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
            ok = button
        )
    )
    if (await) prompt.await()
}