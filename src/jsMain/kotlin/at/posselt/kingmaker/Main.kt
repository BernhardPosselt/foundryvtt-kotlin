package at.posselt

import at.posselt.kingmaker.actor.playerCharacters
import at.posselt.kingmaker.buildPromise
import at.posselt.kingmaker.macros.awardHeroPoints
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.game


fun main() {

    Hooks.on("ready") {
//        DialogV2.confirm(ConfirmOptions(content = "<b>hi</b>"))
        buildPromise {
            awardHeroPoints(game.playerCharacters())
        }
//        buildPromise {
//            game.settings.register<Boolean>(
//                "pf2e-kingmaker-tools-ng", "thingy", SettingsData(
//                    name = "Example Setting",
//                    scope = "world",
//                    config = true,
//                    default = false,
//                    requiresReload = true,
//                    type = Boolean::class.js,
//                )
//            )
//            game.settings.set("pf2e-kingmaker-tools-ng", "thingy", true).await()
//            window.alert(game.settings.get<Boolean>("pf2e-kingmaker-tools-ng", "thingy").toString())
//            console.log(game.actors?.contents?.filter { it is PF2ECharacter })
//        }
//        console.log(game.actors?.contents?.filter { it is PF2ECharacter })
    }
}