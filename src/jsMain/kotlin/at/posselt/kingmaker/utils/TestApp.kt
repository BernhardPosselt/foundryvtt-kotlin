package at.posselt.kingmaker.utils

import at.posselt.kingmaker.dialog.App
import at.posselt.kingmaker.dialog.AppArguments
import com.foundryvtt.core.ApplicationHeaderControlsEntry

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

    override fun onMenu(action: String) {
        console.log("Action: " + action)
    }

    fun log() {
        console.log("ooooooooooooooooooooooooo")
    }
}