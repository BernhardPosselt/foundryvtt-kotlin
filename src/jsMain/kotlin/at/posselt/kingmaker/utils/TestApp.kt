package at.posselt.kingmaker.utils

import at.posselt.kingmaker.dialog.App
import at.posselt.kingmaker.dialog.AppArguments
import com.foundryvtt.core.ApplicationHeaderControlsEntry
import kotlinx.html.org.w3c.dom.events.Event

class TestApp : App<Any>(
    AppArguments(
        title = "Testinggggggg",
        templatePath = resolveTemplatePath("applications/settings/configure-regions.hbs"),
        menuButtons = arrayOf(
            ApplicationHeaderControlsEntry(
                action = "test",
                label = "Do It"
            )
        )
    )
) {
    override fun onInit() {
        registerHook("updateWorldTime") { log() }
        on("p") {
            console.log(it.currentTarget)
        }
    }

    override fun onAction(action: String, event: Event) {
        console.log("Action: " + action, event)
    }

    fun log() {
        console.log("ooooooooooooooooooooooooo")
    }
}