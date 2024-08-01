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
import js.core.JsNumber
import js.objects.Record
import kotlinx.coroutines.await

private fun <T : DataField> Settings.registerField(
    key: String,
    name: String,
    hint: String? = undefined,
    requiresReload: Boolean = false,
    type: T,
) {
    register<T>(
        Config.moduleId,
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

fun Settings.registerInt(
    key: String,
    name: String,
    hint: String? = undefined,
    default: Int = 0,
    hidden: Boolean = false,
    requiresReload: Boolean = false,
    choices: Record<String, Int>? = undefined,
) {
    register<Int>(
        Config.moduleId,
        key,
        SettingsData<Int>(
            name = name,
            hint = hint,
            config = !hidden,
            default = default,
            requiresReload = requiresReload,
            type = JsNumber::class.js,
            scope = "world",
            choices = choices,
        )
    )
}

private inline fun <reified T : Any> Settings.registerScalar(
    key: String,
    name: String,
    hint: String? = undefined,
    default: T? = undefined,
    hidden: Boolean = false,
    requiresReload: Boolean = false,
    choices: Record<String, T>? = undefined,
) {
    register<T>(
        Config.moduleId,
        key,
        SettingsData<T>(
            name = name,
            hint = hint,
            config = !hidden,
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
    hint: String? = undefined,
    icon: String? = undefined,
    restricted: Boolean = false,
    app: JsClass<out ApplicationV2>
) {
    registerMenu<ApplicationV2>(
        Config.moduleId,
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
    get(Config.moduleId, key)

suspend fun Settings.setInt(key: String, value: Int) {
    set(Config.moduleId, key, value).await()
}

private fun Settings.getString(key: String): String =
    get(Config.moduleId, key)

suspend fun Settings.setString(key: String, value: String) {
    set(Config.moduleId, key, value).await()
}

private fun Settings.getBoolean(key: String): Boolean =
    get(Config.moduleId, key)

private suspend fun Settings.setBoolean(key: String, value: Boolean) {
    set(Config.moduleId, key, value).await()
}

private fun <T : Any> Settings.getObject(key: String): T =
    get(Config.moduleId, key)

private suspend fun Settings.setObject(key: String, value: Any) {
    set(Config.moduleId, key, value).await()
}

val Settings.kingmakerTools: KingmakerToolsSettings
    get() = KingmakerToolsSettings

object KingmakerToolsSettings {
    suspend fun setKingdomEventsTable(value: String) =
        game.settings.setString("kingdomEventsTable", value)

    fun getKingdomEventsTable(): String =
        game.settings.getString("kingdomEventsTable")

    suspend fun setKingdomCultTable(value: String) =
        game.settings.setString("kingdomCultTable", value)

    fun getKingdomCultTable(): String =
        game.settings.getString("kingdomCultTable")

    suspend fun setWeatherHazardRange(value: Int) =
        game.settings.setInt("weatherHazardRange", value)

    fun getWeatherHazardRange(): Int =
        game.settings.getInt("weatherHazardRange")

    suspend fun setWeatherRollMode(value: RollMode) =
        game.settings.setString("weatherRollMode", value.toCamelCase())

    fun getWeatherRollMode(): RollMode =
        fromCamelCase<RollMode>(game.settings.getString("weatherRollMode"))
            ?: throw IllegalStateException("Null value set for setting 'weatherRollMode'")

    suspend fun setKingdomEventRollMode(value: RollMode) =
        game.settings.setString("kingdomEventRollMode", value.toCamelCase())

    fun getKingdomEventRollMode(): RollMode =
        fromCamelCase<RollMode>(game.settings.getString("kingdomEventRollMode"))
            ?: throw IllegalStateException("Null value set for setting 'kingdomEventRollMode'")

    suspend fun setEnableWeatherSoundFx(value: Boolean) =
        game.settings.setBoolean("enableWeatherSoundFx", value)

    fun getEnableWeatherSoundFx(): Boolean =
        game.settings.getBoolean("enableWeatherSoundFx")

    suspend fun setEnableCombatTracks(value: Boolean) =
        game.settings.setBoolean("enableCombatTracks", value)

    fun getEnableCombatTracks(): Boolean =
        game.settings.getBoolean("enableCombatTracks")

    suspend fun setEnableSheltered(value: Boolean) =
        game.settings.setBoolean("enableSheltered", value)

    fun getEnableSheltered(): Boolean =
        game.settings.getBoolean("enableSheltered")

    suspend fun setAutoRollWeather(value: Boolean) =
        game.settings.setBoolean("autoRollWeather", value)

    fun getAutoRollWeather(): Boolean =
        game.settings.getBoolean("autoRollWeather")

    suspend fun setEnableWeather(value: Boolean) =
        game.settings.setBoolean("enableWeather", value)

    fun getEnableWeather(): Boolean =
        game.settings.getBoolean("enableWeather")

    suspend fun setRegionSettings(settings: RegionSettings) =
        game.settings.setObject("regionSettings", settings)

    fun getRegionSettings(): RegionSettings =
        game.settings.getObject("regionSettings")

    suspend fun setClimateSettings(settings: ClimateSettings) =
        game.settings.setObject("climateSettings", settings)

    fun getClimateSettings(): ClimateSettings =
        game.settings.getObject("climateSettings")

    suspend fun setCurrentWeatherFx(value: String) =
        game.settings.setString("currentWeatherFx", value)

    fun getCurrentWeatherFx(): String =
        game.settings.getString("currentWeatherFx")

    private object nonUserVisibleSettings {
        val booleans = mapOf(
            "enableSheltered" to false,
        )
        val strings = mapOf(
            "currentWeatherFx" to "none",
            "kingdomEventsTable" to "Random Kingdom Events",
            "kingdomCultTable" to "Random Cult Events",
        )
    }


    fun register() {
        registerSimple(game.settings, nonUserVisibleSettings.strings, hidden = true)
        registerSimple(game.settings, nonUserVisibleSettings.booleans, hidden = true)
        game.settings.registerField(
            key = "regionSettings",
            name = "Region Settings",
            type = ObjectField(
                DataFieldOptions(
                    initial = RegionSettings(
                        useStolenLands = true,
                        regions = emptyArray<RegionSetting>()
                    )
                )
            ),
        )
        game.settings.createMenu(
            key = "regionsMenu",
            label = "Customize Regions",
            name = "Regions",
            app = RegionConfiguration::class.js,
        )
        game.settings.registerField(
            key = "climateSettings",
            name = "Climate Settings",
            type = ObjectField(
                DataFieldOptions(
                    initial = ClimateSettings(
                        useStolenLands = true,
                        months = getDefaultMonths(),
                    )
                )
            )
        )
        game.settings.createMenu(
            key = "climateMenu",
            label = "Customize Climate",
            name = "Climate",
            app = ClimateConfiguration::class.js,
        )
        game.settings.registerScalar(
            name = "Enable Weather",
            key = "enableWeather",
            default = true,
        )
        game.settings.registerScalar<Boolean>(
            key = "enableWeatherSoundFx",
            name = "Enable Ambient Weather Sounds",
            hint = "If enabled, will play rain and snow tracks from the official Kingmaker module. You can override this behavior by creating playlists named like \"weather.blizzard\" or \"weather.rain\".",
            default = true,
        )
        game.settings.registerScalar<Boolean>(
            key = "autoRollWeather",
            name = "Auto Roll Weather",
            hint = "When a new day begins (00:00), automatically roll weather",
            default = true,
        )
        game.settings.registerScalar<String>(
            key = "weatherRollMode",
            name = "Weather Roll Mode",
            choices = RollMode.entries.asSequence()
                .map { it.toCamelCase() to it.label }
                .toMutableRecord(),
            default = "gmroll"
        )
        game.settings.registerInt(
            key = "weatherHazardRange",
            name = "Weather Hazard Range",
            default = 4,
            hint = "Roll weather events up and equal to Party Level plus this value"
        )
        game.settings.registerScalar<String>(
            key = "kingdomEventRollMode",
            name = "Kingdom Event Roll Mode",
            choices = RollMode.entries.asSequence()
                .map { it.toCamelCase() to it.label }
                .toMutableRecord(),
            default = "gmroll",
            hidden = true,
        )
        game.settings.registerScalar<Boolean>(
            key = "enableCombatTracks",
            name = "Enable Combat Tracks",
            hint = "If enabled, starts a combat track depending on the current region, actor or scene. Region combat tracks  overrides for the Stolen Lands have to be named after the region, e.g. \"Kingmaker.Rostland Hinterlands\"; \"Kingmaker.Default\" is played if no region playlist is found instead. If you customize regions, you can set them in the Customize Regions settings instead",
            default = true,
        )

    }
}


private inline fun <reified T : Any> registerSimple(
    settings: Settings,
    values: Map<String, T>,
    hidden: Boolean,
) {
    values.forEach { (key, value) ->
        settings.registerScalar<T>(
            key = key,
            default = value,
            name = key.deCamelCase(),
            hidden = hidden,
        )
    }
}

