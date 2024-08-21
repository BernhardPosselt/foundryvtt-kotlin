package at.posselt.kingmaker

import at.posselt.kingmaker.actor.partyMembers
import at.posselt.kingmaker.camping.CampingSheet
import at.posselt.kingmaker.camping.HuntAndGatherMessage
import at.posselt.kingmaker.camping.addHuntAndGather
import at.posselt.kingmaker.camping.bindCampingChatEventListeners
import at.posselt.kingmaker.camping.getCamping
import at.posselt.kingmaker.camping.getCampingActor
import at.posselt.kingmaker.camping.openCampingSheet
import at.posselt.kingmaker.combattracks.registerCombatTrackHooks
import at.posselt.kingmaker.macros.*
import at.posselt.kingmaker.migrations.migrateKingmakerTools
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.utils.*
import at.posselt.kingmaker.weather.registerWeatherHooks
import at.posselt.kingmaker.weather.rollWeather
import com.foundryvtt.core.*
import js.objects.recordOf

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

            // load custom token mappings if kingmaker module isn't installed
            if (game.modules.get("pf2e-kingmaker")?.active != true) {
                val data = recordOf(
                    "flags" to recordOf(
                        "pf2e-kingmaker-tools" to recordOf(
                            "pf2e-art" to "modules/pf2e-kingmaker-tools/token-map.json"
                        )
                    )
                )
                game.modules.get("pf2e-kingmaker-tools")
                    ?.updateSource(data)
            }
            registerWeatherHooks(game)
            registerCombatTrackHooks(game)

            game.socket.onKingmakerTools { data ->
                buildPromise {
                    if (isJsObject(data)) {
                        val action = data["action"]
                        if (action == "openCampingSheet") {
                            game.getCampingActor()
                                ?.let { actor -> CampingSheet(game, actor) }
                                ?.launch()
                        } else if (game.isFirstGM() && action == "addHuntAndGatherResult") {
                            HuntAndGatherMessage.parse(data["data"])?.let { result ->
                                game.getCampingActor()?.getCamping()?.let { camping ->
                                    addHuntAndGather(game, camping, result)
                                }
                            }
                        }
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
                openCampingSheet = { buildPromise { openCampingSheet(game) } }
            )
        )
    }

    Hooks.onReady {
        buildPromise {
            game.migrateKingmakerTools()

            game.getCampingActor()
                ?.let { actor -> CampingSheet(game, actor) }
                ?.launch()
        }
    }

    Hooks.onRenderChatLog { _, _, _ ->
        bindCampingChatEventListeners(game)
    }
}
