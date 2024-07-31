package at.posselt.kingmaker

import com.foundryvtt.core.Game
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
//    val realmTileDialogMacro: () -> Unit
//    val structureTokenMappingMacro: () -> Unit
//    val editStructureMacro: (Actor) -> Unit
//    val openCampingSheet: () -> Unit
//    val viewKingdomMacro: () -> Unit
//    val toggleCombatTracksMacro: () -> Unit
//    val setSceneCombatPlaylistDialogMacro: (Actor?) -> Unit
//    val toTimeOfDayMacro: () -> Unit
}


@JsPlainObject
external interface PF2EKingmakerTools {
    val macros: ToolsMacros
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline var Game.pf2eKingmakerTools2: PF2EKingmakerTools
    get() = asDynamic().pf2eKingmakerTools2 as PF2EKingmakerTools
    set(value) {
        asDynamic().pf2eKingmakerTools2 = value
    }
