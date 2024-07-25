package at.posselt.kingmaker.utils

import at.posselt.kingmaker.Config
import com.foundryvtt.core.ApplicationV2
import com.foundryvtt.core.Settings
import com.foundryvtt.core.SettingsData
import com.foundryvtt.core.SettingsMenuData

inline fun <reified T : Any> Settings.create(
    key: String,
    name: String,
    hint: String?,
    default: T? = null,
    hidden: Boolean = false,
    requiresReload: Boolean = false,
) {
    register<T>(
        Config.MODULE_ID,
        key,
        SettingsData<T>(
            name = name,
            hint = hint,
            config = hidden,
            default = default,
            requiresReload = requiresReload,
            type = T::class,
            scope = "world"
        )
    )
}

inline fun <reified T : ApplicationV2> Settings.createMenu(
    key: String,
    name: String,
    label: String,
    hint: String? = null,
    icon: String? = null,
    restricted: Boolean = false,
) {
    registerMenu<T>(
        Config.MODULE_ID,
        key,
        SettingsMenuData<T>(
            name = name,
            label = label,
            hint = hint,
            icon = icon,
            type = T::class.js,
            restricted = restricted,
        )
    )
}