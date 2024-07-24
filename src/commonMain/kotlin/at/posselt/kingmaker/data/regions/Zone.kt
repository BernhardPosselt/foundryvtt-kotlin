package at.posselt.kingmaker.data.regions

data class Zone(
    val name: String,
    val zoneDC: Int,
    val encounterDC: Int,
    val level: Int,
    val combatTrack: String? = null,
)

const val defaultStolenLandsCombatTrack = "The Shrike Hills"

val stolenLandsZones = arrayOf(
    Zone(name = "Brevoy", zoneDC = 14, encounterDC = 12, level = 0),
    Zone(name = "Rostland Hinterlands", zoneDC = 15, encounterDC = 12, level = 1),
    Zone(name = "Greenbelt", zoneDC = 16, encounterDC = 14, level = 2),
    Zone(name = "Tuskwater", zoneDC = 18, encounterDC = 12, level = 3, combatTrack = "Glenebon"),
    Zone(name = "Kamelands", zoneDC = 19, encounterDC = 12, level = 4, combatTrack = "Glenebon"),
    Zone(name = "Narlmarches", zoneDC = 20, encounterDC = 14, level = 5, combatTrack = "The Narlmarches"),
    Zone(name = "Sellen Hills", zoneDC = 20, encounterDC = 12, level = 6, combatTrack = "Glenebon"),
    Zone(name = "Dunsward", zoneDC = 18, encounterDC = 12, level = 7, combatTrack = "Dunsward"),
    Zone(name = "Nomen Heights", zoneDC = 24, encounterDC = 12, level = 8, combatTrack = "Dunsward"),
    Zone(name = "Tors of Levenies", zoneDC = 28, encounterDC = 16, level = 9, combatTrack = "Dunsward"),
    Zone(name = "Hooktongue", zoneDC = 32, encounterDC = 14, level = 10, combatTrack = "The Narlmarches"),
    Zone(name = "Drelev", zoneDC = 28, encounterDC = 12, level = 11, combatTrack = "Glenebon"),
    Zone(name = "Tiger Lords", zoneDC = 28, encounterDC = 12, level = 12, combatTrack = "Glenebon"),
    Zone(name = "Rushlight", zoneDC = 26, encounterDC = 12, level = 13),
    Zone(name = "Glenebon Lowlands", zoneDC = 30, encounterDC = 12, level = 14, combatTrack = "Glenebon"),
    Zone(name = "Pitax", zoneDC = 29, encounterDC = 12, level = 15, combatTrack = "Capital Under Attack"),
    Zone(name = "Glenebon Uplands", zoneDC = 35, encounterDC = 12, level = 16, combatTrack = "Glenebon"),
    Zone(name = "Numeria", zoneDC = 36, encounterDC = 12, level = 17, combatTrack = "Glenebon"),
    Zone(name = "Thousand Voices", zoneDC = 43, encounterDC = 14, level = 18, combatTrack = "First World"),
    Zone(name = "Branthlend Mountains", zoneDC = 41, encounterDC = 16, level = 19, combatTrack = "Glenebon"),
)