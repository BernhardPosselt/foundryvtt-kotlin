package at.posselt.kingmaker

import at.posselt.kingmaker.actor.partyMembers
import at.posselt.kingmaker.actor.playerCharacters
import at.posselt.kingmaker.macros.awardHeroPoints
import at.posselt.kingmaker.macros.awardXP
import at.posselt.kingmaker.macros.rollPartyCheck
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.loadTpls
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.game
import com.foundryvtt.core.onInit
import com.foundryvtt.core.onReady

fun main() {

    Hooks.onInit {
        buildPromise {
            // register partials
            loadTpls(
                arrayOf(
                    "formElement" to "components/forms/form-element.hbs",
                )
            )
        }

    }

    Hooks.onReady {
        val players = game.playerCharacters()
        buildPromise {
            rollPartyCheck(game.partyMembers())
            awardHeroPoints(players)
            awardXP(players)
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