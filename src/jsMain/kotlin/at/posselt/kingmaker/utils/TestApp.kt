package at.posselt.kingmaker.utils

import at.posselt.kingmaker.dialog.App
import at.posselt.kingmaker.dialog.AppArguments

class TestApp : App(
    AppArguments(
        title = "Testinggggggg",
        templatePath = resolveTemplatePath("applications/settings/configure-regions.hbs")
    )
)