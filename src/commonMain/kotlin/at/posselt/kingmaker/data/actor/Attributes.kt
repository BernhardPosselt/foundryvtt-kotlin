package at.posselt.kingmaker.data.actor

import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.unslugify


sealed interface Attribute {
    val value: String
    val label: String
        get() = value.unslugify()

    companion object {
        fun fromString(value: String): Attribute {
            @Suppress("SwallowException")
            return when (value) {
                "perception" -> Perception
                else -> fromCamelCase<Skill>(value) ?: Lore(value)
            }
        }
    }
}

enum class Skill : Attribute {
    ACROBATICS,
    ARCANA,
    ATHLETICS,
    CRAFTING,
    DECEPTION,
    DIPLOMACY,
    INTIMIDATION,
    MEDICINE,
    NATURE,
    OCCULTISM,
    PERFORMANCE,
    RELIGION,
    SOCIETY,
    STEALTH,
    SURVIVAL,
    THIEVERY;

    override val value: String
        get() = toCamelCase()
}

data object Perception : Attribute {
    override val value = "perception"
}

class Lore(override val value: String) : Attribute

enum class SkillRank {
    UNTRAINED,
    TRAINED,
    EXPERT,
    MASTER,
    LEGENDARY;
}