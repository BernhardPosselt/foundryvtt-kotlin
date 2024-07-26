package at.posselt.kingmaker.settings

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.*
import com.foundryvtt.core.applications.api.ApplicationV2
import js.objects.jso
import kotlinx.coroutines.await

fun <T : DataField> Settings.registerField(
    key: String,
    name: String,
    hint: String? = null,
    requiresReload: Boolean = false,
    type: T
) {
    register<T>(
        Config.MODULE_ID,
        key,
        SettingsData<T>(
            name = name,
            hint = hint,
            config = false,
            requiresReload = requiresReload,
            type = type,
            scope = "world"
        )
    )
}

inline fun <reified T : Any> Settings.registerScalar(
    key: String,
    name: String,
    hint: String? = null,
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

fun Settings.createMenu(
    key: String,
    name: String,
    label: String,
    hint: String? = null,
    icon: String? = null,
    restricted: Boolean = false,
    app: JsClass<out ApplicationV2>
) {
    registerMenu<ApplicationV2>(
        Config.MODULE_ID,
        key,
        SettingsMenuData<ApplicationV2>(
            name = name,
            label = label,
            hint = hint,
            icon = icon,
            type = app,
            restricted = restricted,
        )
    )
}

fun Settings.getBoolean(key: String): Boolean = get(Config.MODULE_ID, key)

suspend fun Settings.setBoolean(key: String, value: Boolean) = buildPromise {
    set(Config.MODULE_ID, key, value).await()
}

fun <T> Settings.getObject(key: String): T = get(Config.MODULE_ID, key) ?: jso()

suspend fun Settings.setObject(key: String, value: Any) = buildPromise {
    set(Config.MODULE_ID, key, value).await()
}