package at.posselt.kingmaker.settings

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.deCamelCase
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.toMutableRecord
import com.foundryvtt.core.*
import com.foundryvtt.core.applications.api.ApplicationV2
import com.foundryvtt.core.data.fields.DataFieldOptions
import com.foundryvtt.core.data.fields.ObjectField
import js.objects.Record
import kotlinx.coroutines.await

private fun <T : DataField> Settings.registerField(
    key: String,
    name: String,
    hint: String? = null,
    requiresReload: Boolean = false,
    type: T,
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

private inline fun <reified T : Any> Settings.registerScalar(
    key: String,
    name: String,
    hint: String? = null,
    default: T? = null,
    hidden: Boolean = false,
    requiresReload: Boolean = false,
    choices: Record<String, T>? = null,
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
            type = T::class.js,
            scope = "world",
            choices = choices,
        )
    )
}

private fun Settings.createMenu(
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

private fun Settings.getInt(key: String): Int =
    get(Config.MODULE_ID, key)

suspend fun Settings.setInt(key: String, value: Int) {
    set(Config.MODULE_ID, key, value).await()
}

private fun Settings.getString(key: String): String =
    get(Config.MODULE_ID, key)

suspend fun Settings.setString(key: String, value: String) {
    set(Config.MODULE_ID, key, value).await()
}

private fun Settings.getBoolean(key: String): Boolean =
    get(Config.MODULE_ID, key)

private suspend fun Settings.setBoolean(key: String, value: Boolean) {
    set(Config.MODULE_ID, key, value).await()
}

private fun <T : Any> Settings.getObject(key: String): T =
    get(Config.MODULE_ID, key)

private suspend fun Settings.setObject(key: String, value: Any) {
    set(Config.MODULE_ID, key, value).await()
}

val Settings.kingmakerTools: KingmakerToolsSettings
    get() = KingmakerToolsSettings

object KingmakerToolsSettings {
    suspend fun setWeatherHazardRange(value: Int) =
        game.settings.setInt("weatherHazardRange", value)

    fun getWeatherHazardRange(): Int =
        game.settings.getInt("weatherHazardRange")

    suspend fun setWeatherRollMode(value: RollMode) =
        game.settings.setString("weatherRollMode", value.toCamelCase())

    fun getWeatherRollMode(): RollMode =
        fromCamelCase<RollMode>(game.settings.getString("weatherRollMode"))
            ?: throw IllegalStateException("Null value set for setting 'weatherRollMode'")

    suspend fun setEnableWeatherSoundFx(value: Boolean) =
        game.settings.setBoolean("enableWeatherSoundFx", value)

    fun getEnableWeatherSoundFx(): Boolean =
        game.settings.getBoolean("enableWeatherSoundFx")

    suspend fun setEnableSheltered(value: Boolean) =
        game.settings.setBoolean("enableSheltered", value)

    fun getEnableSheltered(): Boolean =
        game.settings.getBoolean("enableSheltered")

    suspend fun setEnableWeather(value: Boolean) =
        game.settings.setBoolean("enableWeather", value)

    fun getEnableWeather(): Boolean =
        game.settings.getBoolean("enableWeather")

    suspend fun setRegionSettings(settings: RegionSettings) =
        game.settings.setObject("regionSettings", settings)

    fun getRegionSettings(): RegionSettings =
        game.settings.getObject("regionSettings")

    suspend fun setCurrentWeatherFx(value: String) =
        game.settings.setString("currentWeatherFx", value)

    fun getCurrentWeatherFx(): String =
        game.settings.getString("currentWeatherFx")

    private val nonUserVisibleSettings = object {
        val strings = mapOf("currentWeatherFx" to "NONE")
        val booleans = mapOf(
            "enableSheltered" to false,
        )
    }

    private val userVisibleSettings = object {
        val strings = mapOf<String, String>()
        val booleans = mapOf(
            "enableWeather" to true,
            "enableWeatherSoundFx" to true,
        )
        val ints = mapOf(
            "weatherHazardRange" to 4
        )
    }

    fun register() {
        registerSimple(game.settings, nonUserVisibleSettings.strings, hidden = true)
        registerSimple(game.settings, nonUserVisibleSettings.booleans, hidden = true)
        registerSimple(game.settings, userVisibleSettings.ints, hidden = false)
        registerSimple(game.settings, userVisibleSettings.strings, hidden = false)
        registerSimple(game.settings, userVisibleSettings.booleans, hidden = false)
        registerCustom(game.settings)
    }
}


private inline fun <reified T : Any> registerSimple(
    settings: Settings,
    values: Map<String, T>,
    hidden: Boolean,
) {
    values.forEach { (key, default) ->
        settings.registerScalar<T>(
            key = key,
            default = default,
            name = key.deCamelCase(),
            hidden = hidden,
        )
    }
}

private fun registerCustom(settings: Settings) {
    settings.registerField(
        key = "regionSettings",
        name = "Region Settings",
        type = ObjectField(
            DataFieldOptions(
                initial = RegionSettings(
                    useStolenLands = true,
                    regions = emptyArray()
                )
            )
        ),
    )
    settings.createMenu(
        key = "regionsMenu",
        label = "Customize",
        name = "Regions",
        app = RegionConfiguration::class.js,
    )
    settings.registerScalar<String>(
        key = "weatherRollMode",
        name = "Weather Roll Mode",
        choices = RollMode.entries.asSequence()
            .map { it.toCamelCase() to it.label }
            .toMutableRecord(),
        default = "gmroll"
    )
}