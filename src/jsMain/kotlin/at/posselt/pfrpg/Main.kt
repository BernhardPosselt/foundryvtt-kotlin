package at.posselt.pfrpg

import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.handlers.AddHuntAndGatherResultHandler
import at.posselt.pfrpg.actions.handlers.ClearActivitiesHandler
import at.posselt.pfrpg.actions.handlers.ClearMealEffectsHandler
import at.posselt.pfrpg.actions.handlers.OpenCampingSheetHandler
import at.posselt.pfrpg.actions.handlers.SyncActivitiesHandler
import at.posselt.pfrpg.actor.partyMembers
import at.posselt.pfrpg.camping.bindCampingChatEventListeners
import at.posselt.pfrpg.camping.openCampingSheet
import at.posselt.pfrpg.camping.registerActivityDiffingHooks
import at.posselt.pfrpg.combattracks.registerCombatTrackHooks
import at.posselt.pfrpg.macros.*
import at.posselt.pfrpg.migrations.migratePfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.settings.pfrpg2eKingdomCampingWeather
import at.posselt.pfrpg.utils.*
import at.posselt.pfrpg.weather.registerWeatherHooks
import at.posselt.pfrpg.weather.rollWeather
import com.foundryvtt.core.*
import js.objects.recordOf

fun main() {
    Hooks.onInit {
        val actionDispatcher = ActionDispatcher(
            game = game,
            handlers = listOf(
                AddHuntAndGatherResultHandler(game = game),
                OpenCampingSheetHandler(game = game),
                ClearActivitiesHandler(game = game),
                SyncActivitiesHandler(game = game),
                ClearMealEffectsHandler(game = game),
            )
        ).apply {
            listen()
        }

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
                        Config.moduleId to recordOf(
                            "pf2e-art" to "modules/${Config.moduleId}/token-map.json"
                        )
                    )
                )
                game.modules.get(Config.moduleId)
                    ?.updateSource(data)
            }
            registerWeatherHooks(game)
            registerCombatTrackHooks(game)
            registerActivityDiffingHooks(game, actionDispatcher)
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
                openCampingSheet = { buildPromise { openCampingSheet(game, actionDispatcher) } },
                subsistMacro = { actor -> buildPromise { subsistMacro(game, actor) } }
            )
        )

        Hooks.onReady {
            buildPromise {
                game.migratePfrpg2eKingdomCampingWeather()
                openCampingSheet(game, actionDispatcher)
            }
//            DamageRoll("1d4[fire]").toMessage()
        }

        Hooks.onRenderChatLog { _, _, _ ->
            bindCampingChatEventListeners(game, actionDispatcher)
        }
    }
}
