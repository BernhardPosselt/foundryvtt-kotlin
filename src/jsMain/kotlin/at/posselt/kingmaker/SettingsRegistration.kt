package at.posselt.kingmaker

import at.posselt.kingmaker.utils.ConfigureRegions
import at.posselt.kingmaker.utils.createMenu
import com.foundryvtt.core.Settings

fun registerSettings(settings: Settings) {
    settings.createMenu<ConfigureRegions>(
        key = "regions",
        label = "bloo",
        name = "Region Configuration"
    )
}