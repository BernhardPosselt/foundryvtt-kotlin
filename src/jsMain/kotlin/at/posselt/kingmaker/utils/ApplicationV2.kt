package at.posselt.kingmaker.utils

import com.foundryvtt.core.applications.api.ApplicationRenderOptions
import com.foundryvtt.core.applications.api.ApplicationV2

fun ApplicationV2.launch() {
    render(ApplicationRenderOptions(force = true))
}