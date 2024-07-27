package at.posselt.kingmaker.macros

import at.posselt.kingmaker.SceneWeatherSettings
import at.posselt.kingmaker.app.CheckboxInput
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.prompt
import at.posselt.kingmaker.getWeatherSettings
import at.posselt.kingmaker.setWeatherSettings
import com.foundryvtt.core.documents.Scene
import js.objects.recordOf


suspend fun sceneWeatherSettingsMacro(scene: Scene) {
    val settings = scene.getWeatherSettings()
    prompt<SceneWeatherSettings, Unit>(
        title = "Scene Weather Settings",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                CheckboxInput(
                    name = "syncWeather",
                    label = "Sync Weather",
                    value = settings.syncWeather,
                    help = "If enabled, changes weather on this scene to the current value"
                ),
                CheckboxInput(
                    name = "syncWeatherPlaylist",
                    label = "Sync Weather Playlist",
                    value = settings.syncWeather,
                    help = "If enabled, plays the current weather effect playlist"
                ),
            )
        )
    ) {
        scene.setWeatherSettings(it)
    }
}