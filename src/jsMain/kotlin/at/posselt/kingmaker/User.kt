package at.posselt.kingmaker

import com.foundryvtt.core.Game

fun Game.isFirstGM() =
    users.activeGM?.id == user.id