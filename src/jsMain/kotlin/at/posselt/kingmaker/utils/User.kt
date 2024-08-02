package at.posselt.kingmaker.utils

import com.foundryvtt.core.Game

fun Game.isFirstGM() =
    users.activeGM?.id == user.id