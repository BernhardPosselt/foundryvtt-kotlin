package at.posselt.kingmaker.migrations.migrations

import at.posselt.kingmaker.camping.CampingData
import at.posselt.kingmaker.camping.getDefaultCamping
import com.foundryvtt.core.Game

private val replacements = mapOf(
    "Broiled Tuskwater Oysters" to "Broiled Oysters",
    "First World Mince Pie" to "Supernatural Mince Pie",
    "Galt Ragout" to "Ragout",
    "Giant Scrambled Egg With Shambletus" to "Giant Scrambled Egg",
    "Kameberry Pie" to "Pie",
    "Whiterose Oysters" to "Oysters",
    "Owlbear Omelet" to "Omelet",
)

class Migration11 : Migration(11) {
    override suspend fun migrateCamping(game: Game, camping: CampingData) {
        camping.regionSettings = getDefaultCamping(game).regionSettings
        camping.cooking.knownRecipes = camping.cooking.knownRecipes.mapNotNull {
            if (it in replacements) {
                replacements[it]
            } else {
                it
            }
        }.toTypedArray()
    }
}