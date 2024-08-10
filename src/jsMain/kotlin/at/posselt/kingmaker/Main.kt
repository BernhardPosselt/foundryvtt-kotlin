package at.posselt.kingmaker

import at.posselt.kingmaker.actor.partyMembers
import at.posselt.kingmaker.camping.CampingSheet
import at.posselt.kingmaker.camping.getCampingActor
import at.posselt.kingmaker.combattracks.registerCombatTrackHooks
import at.posselt.kingmaker.macros.*
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.*
import at.posselt.kingmaker.weather.registerWeatherHooks
import at.posselt.kingmaker.weather.rollWeather
import com.foundryvtt.core.*

fun main() {
    Hooks.onInit {
        buildPromise {
            // register partials
            loadTpls(
                arrayOf(
                    "campingTile" to "applications/camping/camping-tile.hbs",
                    "formElement" to "components/forms/form-element.hbs",
                    "tabs" to "components/tabs/tabs.hbs",
                )
            )
            game.settings.kingmakerTools.register()
        }
        registerWeatherHooks(game)
        registerCombatTrackHooks(game)
//        Actors.registerSheet(
//            Config.moduleId, CampingSheet::class.js, RegisterSheetConfig(
//                label = "Camping Sheet",
//                types = arrayOf("npc"),
//            )
//        )

        // camping
//        Hooks.onRenderPF2ENpcSheet { application, html, data ->
//            buildPromise {
//                if (data.document.getCamping() != null) {
//                    CampingSheet(data.document, game).launch()
//                    application.close().await()
//                }
//            }
//        }
        game.socket.onKingmakerTools { data ->
            buildPromise {
                if (isJsObject(data)) {
                    if (data["action"] == "openCampingSheet") {
                        game.getCampingActor()
                            ?.let { actor -> CampingSheet(actor) }
                            ?.launch()
                    }
                }
            }
        }

        game.pf2eKingmakerTools = PF2EKingmakerTools(
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
                            attributeName = skill,
                            explorationEffectName = effect,
                        )
                    }
                },
                rollSkillDialog = { buildPromise { rollPartyCheckMacro(game.partyMembers()) } },
                setSceneCombatPlaylistDialogMacro = { actor -> buildPromise { combatTrackMacro(game, actor) } },
                toTimeOfDayMacro = { buildPromise { setTimeOfDayMacro(game) } },
                toggleCombatTracksMacro = { buildPromise { toggleCombatTracksMacro(game) } },
                realmTileDialogMacro = { buildPromise { editRealmTileMacro(game) } },
                editStructureMacro = { actor -> buildPromise { editStructureMacro(actor) } },
            )
        )
    }

    Hooks.onReady {
        buildPromise {
            game.getCampingActor()
                ?.let { actor -> CampingSheet(actor) }
                ?.launch()
        }
    }
}
