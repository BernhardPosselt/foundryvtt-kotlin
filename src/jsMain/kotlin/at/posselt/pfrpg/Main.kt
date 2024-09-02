package at.posselt.pfrpg

import at.posselt.pfrpg.actor.partyMembers
import at.posselt.pfrpg.camping.CampingCommand
import at.posselt.pfrpg.camping.CampingSheet
import at.posselt.pfrpg.camping.HuntAndGatherMessage
import at.posselt.pfrpg.camping.addHuntAndGather
import at.posselt.pfrpg.camping.bindCampingChatEventListeners
import at.posselt.pfrpg.camping.checkPreActorUpdate
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.camping.openCampingSheet
import at.posselt.pfrpg.combattracks.registerCombatTrackHooks
import at.posselt.pfrpg.macros.*
import at.posselt.pfrpg.migrations.migratePfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.utils.*
import at.posselt.pfrpg.weather.registerWeatherHooks
import at.posselt.pfrpg.weather.rollWeather
import com.foundryvtt.core.*
import com.foundryvtt.pf2e.actor.PF2ECharacter
import com.foundryvtt.pf2e.rolls.DamageRoll
import js.objects.recordOf

fun main() {
    Hooks.onInit {
        buildPromise {
            // register partials
            loadTpls(
                arrayOf(
                    "campingTile" to "applications/camping/camping-tile.hbs",
                    "recipeTile" to "applications/camping/recipe-tile.hbs",
                    "formElement" to "components/forms/form-element.hbs",
                    "tabs" to "components/tabs/tabs.hbs",
                    "foodCost" to "components/food-cost/food-cost.hbs",
                    "skillPickerInput" to "components/skill-picker/skill-picker-input.hbs",
                    "activityEffectsInput" to "components/activity-effects/activity-effects-input.hbs",
                )
            )
            game.settings.pfrpg2eKingdomCampingWeather.register()

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

            Hooks.onPreUpdateActor { actor, update, _, _ ->
                when (val result = checkPreActorUpdate(actor, update)) {
                    is CampingCommand.ClearActivities -> console.log("Clear Activities")
                    is CampingCommand.DoNothing -> console.log("Do Nothing")
                    is CampingCommand.SkipActivities -> console.log("Skip Activities", result.rollRandomEncounter)
                    is CampingCommand.SyncActivities -> console.log(
                        "Sync Activities",
                        update,
                        result.rollRandomEncounter
                    )
                }
            }

            game.socket.onPfrpg2eKingdomCampingWeather { data ->
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

        game.pf2eKingmakerTools = Pfrpg2eKingdomCampingWeather(
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
                openCampingSheet = { buildPromise { openCampingSheet(game) } },
                subsistMacro = { actor ->
                    buildPromise {
                        if (actor is PF2ECharacter) {
                            subsistMacro(game, actor)
                        } else {
                            ui.notifications.error("Please select a Character")
                        }
                    }
                }
            )
        )
    }

    Hooks.onReady {
        buildPromise {
            game.migratePfrpg2eKingdomCampingWeather()

            game.getCampingActor()
                ?.let { actor -> CampingSheet(game, actor) }
                ?.launch()
        }
        DamageRoll("1d4[fire]").toMessage()
    }

    Hooks.onRenderChatLog { _, _, _ ->
        bindCampingChatEventListeners(game)
    }
}
