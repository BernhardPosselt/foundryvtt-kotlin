package at.posselt.kingmaker

import at.posselt.kingmaker.utils.TestApp
import at.posselt.kingmaker.utils.createMenu
import com.foundryvtt.core.Settings

fun registerSettings(settings: Settings) {
    val app = TestApp()
    settings.createMenu(
        key = "regions",
        label = "bloo",
        name = "Region Configuration",
        app = app.app
    )
}