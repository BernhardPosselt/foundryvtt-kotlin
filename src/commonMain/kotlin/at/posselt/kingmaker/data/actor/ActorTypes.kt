package at.posselt.kingmaker.data.actor

enum class ActorTypes(val type: String) {
    NPC("npc"),
    CHARACTER("character"),
    ARMY("army"),
    FAMILIAR("familiar"),
    LOOT("loot"),
    HAZARD("hazard"),
    VEHICLE("vehicle"),
    PARTY("party");

    companion object {
        fun fromString(type: String) = when (type) {
            "npc" -> NPC
            "character" -> CHARACTER
            "army" -> ARMY
            "familiar" -> FAMILIAR
            "loot" -> LOOT
            "hazard" -> HAZARD
            "vehicle" -> VEHICLE
            "party" -> PARTY
            else -> throw IllegalArgumentException("unknown actor type: $type")
        }
    }
}