package at.posselt.kingmaker.data.regions

sealed interface CombatTrack

data class KingmakerCombatTrack(
    val trackName: String,
) : CombatTrack

data class Zone(
    val name: String,
    val zoneDc: Int,
    val encounterDc: Int,
    val level: Int,
    val combatTrackName: String,
)

val stolenLandsZones = arrayOf(
    Zone(
        name = "Brevoy",
        zoneDc = 14,
        encounterDc = 12,
        level = 0,
        combatTrackName = "The Shrike Hills",
    ),
    Zone(
        name = "Rostland Hinterlands",
        zoneDc = 15,
        encounterDc = 12,
        level = 1,
        combatTrackName = "The Shrike Hills",
    ),
    Zone(
        name = "Greenbelt",
        zoneDc = 16,
        encounterDc = 14,
        level = 2,
        combatTrackName = "The Shrike Hills",
    ),
    Zone(
        name = "Tuskwater",
        zoneDc = 18,
        encounterDc = 12,
        level = 3,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Kamelands",
        zoneDc = 19,
        encounterDc = 12,
        level = 4,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Narlmarches",
        zoneDc = 20,
        encounterDc = 14,
        level = 5,
        combatTrackName = "The Narlmarches",
    ),
    Zone(
        name = "Sellen Hills",
        zoneDc = 20,
        encounterDc = 12,
        level = 6,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Dunsward",
        zoneDc = 18,
        encounterDc = 12,
        level = 7,
        combatTrackName = "Dunsward",
    ),
    Zone(
        name = "Nomen Heights",
        zoneDc = 24,
        encounterDc = 12,
        level = 8,
        combatTrackName = "Dunsward",
    ),
    Zone(
        name = "Tors of Levenies",
        zoneDc = 28,
        encounterDc = 16,
        level = 9,
        combatTrackName = "Dunsward",
    ),
    Zone(
        name = "Hooktongue",
        zoneDc = 32,
        encounterDc = 14,
        level = 10,
        combatTrackName = "The Narlmarches",
    ),
    Zone(
        name = "Drelev",
        zoneDc = 28,
        encounterDc = 12,
        level = 11,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Tiger Lords",
        zoneDc = 28,
        encounterDc = 12,
        level = 12,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Rushlight",
        zoneDc = 26,
        encounterDc = 12,
        level = 13,
        combatTrackName = "The Shrike Hills",
    ),
    Zone(
        name = "Glenebon Lowlands",
        zoneDc = 30,
        encounterDc = 12,
        level = 14,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Pitax",
        zoneDc = 29,
        encounterDc = 12,
        level = 15,
        combatTrackName = "Capital Under Attack",
    ),
    Zone(
        name = "Glenebon Uplands",
        zoneDc = 35,
        encounterDc = 12,
        level = 16,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Numeria",
        zoneDc = 36,
        encounterDc = 12,
        level = 17,
        combatTrackName = "Glenebon",
    ),
    Zone(
        name = "Thousand Voices",
        zoneDc = 43,
        encounterDc = 14,
        level = 18,
        combatTrackName = "First World",
    ),
    Zone(
        name = "Branthlend Mountains",
        zoneDc = 41,
        encounterDc = 16,
        level = 19,
        combatTrackName = "Glenebon",
    ),
)