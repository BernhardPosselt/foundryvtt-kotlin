package at.posselt.kingmaker.data.regions

sealed interface CombatTrack

data class KingmakerCombatTrack(
    val trackName: String,
) : CombatTrack

data class PlaylistCombatTrack(
    val playlistId: String,
    val trackId: String? = null,
) : CombatTrack

data class Zone(
    val name: String,
    val zoneDc: Int,
    val encounterDc: Int,
    val level: Int,
    val combatTrack: CombatTrack? = null,
)


val stolenLandsZones = arrayOf(
    Zone(
        name = "Brevoy",
        zoneDc = 14,
        encounterDc = 12,
        level = 0
    ),
    Zone(
        name = "Rostland Hinterlands",
        zoneDc = 15,
        encounterDc = 12,
        level = 1
    ),
    Zone(
        name = "Greenbelt",
        zoneDc = 16,
        encounterDc = 14,
        level = 2
    ),
    Zone(
        name = "Tuskwater",
        zoneDc = 18,
        encounterDc = 12,
        level = 3,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Kamelands",
        zoneDc = 19,
        encounterDc = 12,
        level = 4,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Narlmarches",
        zoneDc = 20,
        encounterDc = 14,
        level = 5,
        combatTrack = KingmakerCombatTrack("The Narlmarches")
    ),
    Zone(
        name = "Sellen Hills",
        zoneDc = 20,
        encounterDc = 12,
        level = 6,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Dunsward",
        zoneDc = 18,
        encounterDc = 12,
        level = 7,
        combatTrack = KingmakerCombatTrack("Dunsward")
    ),
    Zone(
        name = "Nomen Heights",
        zoneDc = 24,
        encounterDc = 12,
        level = 8,
        combatTrack = KingmakerCombatTrack("Dunsward")
    ),
    Zone(
        name = "Tors of Levenies",
        zoneDc = 28,
        encounterDc = 16,
        level = 9,
        combatTrack = KingmakerCombatTrack("Dunsward")
    ),
    Zone(
        name = "Hooktongue",
        zoneDc = 32,
        encounterDc = 14,
        level = 10,
        combatTrack = KingmakerCombatTrack("The Narlmarches")
    ),
    Zone(
        name = "Drelev",
        zoneDc = 28,
        encounterDc = 12,
        level = 11,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Tiger Lords",
        zoneDc = 28,
        encounterDc = 12,
        level = 12,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Rushlight",
        zoneDc = 26,
        encounterDc = 12,
        level = 13
    ),
    Zone(
        name = "Glenebon Lowlands",
        zoneDc = 30,
        encounterDc = 12,
        level = 14,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Pitax",
        zoneDc = 29,
        encounterDc = 12,
        level = 15,
        combatTrack = KingmakerCombatTrack("Capital Under Attack")
    ),
    Zone(
        name = "Glenebon Uplands",
        zoneDc = 35,
        encounterDc = 12,
        level = 16,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Numeria",
        zoneDc = 36,
        encounterDc = 12,
        level = 17,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
    Zone(
        name = "Thousand Voices",
        zoneDc = 43,
        encounterDc = 14,
        level = 18,
        combatTrack = KingmakerCombatTrack("First World")
    ),
    Zone(
        name = "Branthlend Mountains",
        zoneDc = 41,
        encounterDc = 16,
        level = 19,
        combatTrack = KingmakerCombatTrack("Glenebon")
    ),
)