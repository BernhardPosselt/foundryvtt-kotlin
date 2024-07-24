package at.posselt.kingmaker.utils

import at.posselt.kingmaker.Config
import com.foundryvtt.core.Settings
import com.foundryvtt.core.SettingsData

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