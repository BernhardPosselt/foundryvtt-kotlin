package at.posselt.kingmaker

import at.posselt.kingmaker.actor.partyMembers
import at.posselt.kingmaker.actor.playerCharacters
import at.posselt.kingmaker.macros.awardHeroPointsMacro
import at.posselt.kingmaker.macros.awardXPMacro
import at.posselt.kingmaker.macros.rollPartyCheckMacro
import at.posselt.kingmaker.settings.registerRegionSettings
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
            registerRegionSettings(game)
            registerWeatherSettings(game)
        }

    }

    Hooks.onReady {
        val players = game.playerCharacters()
        buildPromise {
            rollPartyCheckMacro(game.partyMembers())
            awardHeroPointsMacro(players)
            awardXPMacro(players)
        }
    }
}
