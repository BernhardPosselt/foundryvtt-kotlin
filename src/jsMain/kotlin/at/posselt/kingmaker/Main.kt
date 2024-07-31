package at.posselt.kingmaker

import at.posselt.kingmaker.actor.partyMembers
import at.posselt.kingmaker.actor.playerCharacters
import at.posselt.kingmaker.macros.*
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.loadTpls
import at.posselt.kingmaker.weather.registerWeatherHooks
import at.posselt.kingmaker.weather.rollWeather
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
            game.settings.kingmakerTools.register()
        }
        registerWeatherHooks(game)
        game.pf2eKingmakerTools2 = PF2EKingmakerTools(
            macros = ToolsMacros(
                toggleWeatherMacro = { buildPromise { toggleWeatherMacro(game) } },
                toggleShelteredMacro = { buildPromise { toggleShelteredMacro(game) } },
                setCurrentWeatherMacro = { buildPromise { setWeatherMacro(game) } },
                sceneWeatherSettingsMacro = {
                    buildPromise<Unit> {
                        game.scenes.active?.let {
                            sceneWeatherSettingsMacro(it)
                        }
                    }
                },
                kingdomEventsMacro = { buildPromise { rollKingdomEventMacro(game) } },
                rollKingmakerWeatherMacro = { buildPromise { rollWeather(game) } },
                awardXpMacro = { buildPromise { awardXPMacro(game.partyMembers()) } },
                resetHeroPointsMacro = { buildPromise { resetHeroPointsMacro(game.partyMembers()) } },
                awardHeroPointsMacro = { buildPromise { awardHeroPointsMacro(game.partyMembers()) } },
                rollExplorationSkillCheck = { skill, effect ->
                    buildPromise {
                        rollExplorationSkillCheckMacro(
                            game,
                            skill,
                            effect
                        )
                    }
                },
                rollSkillDialog = { buildPromise { rollPartyCheckMacro(game.partyMembers()) } },
            )
        )
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
