package at.posselt.kingmaker.utils

import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2EActor
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface ToolsMacros {
    val toggleWeatherMacro: () -> Unit
    val toggleShelteredMacro: () -> Unit
    val setCurrentWeatherMacro: () -> Unit
    val sceneWeatherSettingsMacro: () -> Unit
    val kingdomEventsMacro: () -> Unit
    val rollKingmakerWeatherMacro: () -> Unit
    val awardXpMacro: () -> Unit
    val resetHeroPointsMacro: () -> Unit
    val awardHeroPointsMacro: () -> Unit
    val rollExplorationSkillCheck: (String, String) -> Unit
    val rollSkillDialog: () -> Unit
    val setSceneCombatPlaylistDialogMacro: (PF2EActor?) -> Unit
    val toTimeOfDayMacro: () -> Unit
    val toggleCombatTracksMacro: () -> Unit
    //    val realmTileDialogMacro: () -> Unit
//    val structureTokenMappingMacro: () -> Unit
//    val editStructureMacro: (Actor) -> Unit
//    val openCampingSheet: () -> Unit
//    val viewKingdomMacro: () -> Unit
}


@JsPlainObject
external interface PF2EKingmakerTools {
    val macros: ToolsMacros
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline var Game.pf2eKingmakerTools: PF2EKingmakerTools
    get() = asDynamic().pf2eKingmakerTools as PF2EKingmakerTools
    set(value) {
        asDynamic().pf2eKingmakerTools = value
    }
