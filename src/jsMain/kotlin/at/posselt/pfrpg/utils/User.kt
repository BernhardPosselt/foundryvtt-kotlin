package at.posselt.pfrpg.utils

import com.foundryvtt.core.Game

fun Game.isFirstGM() =
    users.activeGM?.id == user.id