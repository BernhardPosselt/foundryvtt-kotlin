package at.posselt.pfrpg2e.camping

data class CombatEffect(
    val uuid: String,
    val target: String,
    val label: String,
)

suspend fun getCombatEffects(partyLevel: Int, activities: List<CampingActivity>): List<CombatEffect> {
    return activities.mapNotNull {
        when (it.activity) {
            "Enhance Weapons" -> CombatEffect(
                label = it.activity,
                target = "Allies",
                uuid = "@UUID[Compendium.pf2e-kingmaker-tools.kingmaker-tools-camping-effects.ZKJlIqyFgbKDACnG]{Enhance Weapons}"
            )

            "Set Traps" -> CombatEffect(
                label = it.activity,
                target = "Enemies",
                uuid = "@UUID[Compendium.pf2e-kingmaker-tools.kingmaker-tools-camping-effects.PSBOS7ZEl9RGWBqD]{Set Traps}"
            )

            "Undead Guardians" -> CombatEffect(
                label = it.activity,
                target = "1 Ally",
                uuid = "@UUID[Compendium.pf2e-kingmaker-tools.kingmaker-tools-camping-effects.KysTaC245mOnSnmE]{Undead Guardians}"
            )

            "Water Hazards" -> CombatEffect(
                label = it.activity,
                target = "Enemies",
                uuid = "@UUID[Compendium.pf2e-kingmaker-tools.kingmaker-tools-camping-effects.LN6mH7Muj4hgvStt]{Water Hazards}"
            )

            "Maintain Armor" -> CombatEffect(
                label = it.activity,
                target = if (partyLevel < 3) "1 Ally" else "${1 + ((partyLevel - 1) / 2)} Allies",
                uuid = "@UUID[Compendium.pf2e-kingmaker-tools.kingmaker-tools-camping-effects.Item.wojV4NiAOYsnfFby]{Maintain Armor: Armor}"
            )

            else -> null
        }
    }
}