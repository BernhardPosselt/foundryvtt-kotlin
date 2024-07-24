package at.posselt.kingmaker.data.actor

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
                else -> {
                    try {
                        Skill.fromString(value)
                    } catch (e: IllegalArgumentException) {
                        Lore(value)
                    }
                }
            }
        }
    }
}

enum class Skill(override val value: String) : Attribute {
    ACROBATICS("acrobatics"),
    ARCANA("arcana"),
    ATHLETICS("athletics"),
    CRAFTING("crafting"),
    DECEPTION("deception"),
    DIPLOMACY("diplomacy"),
    INTIMIDATION("intimidation"),
    MEDICINE("medicine"),
    NATURE("nature"),
    OCCULTISM("occultism"),
    PERFORMANCE("performance"),
    RELIGION("religion"),
    SOCIETY("society"),
    STEALTH("stealth"),
    SURVIVAL("survival"),
    THIEVERY("thievery");

    companion object {
        fun fromString(value: String) =
            when (value) {
                "acrobatics" -> ACROBATICS
                "arcana" -> ARCANA
                "athletics" -> ATHLETICS
                "crafting" -> CRAFTING
                "deception" -> DECEPTION
                "diplomacy" -> DIPLOMACY
                "intimidation" -> INTIMIDATION
                "medicine" -> MEDICINE
                "nature" -> NATURE
                "occultism" -> OCCULTISM
                "performance" -> PERFORMANCE
                "religion" -> RELIGION
                "society" -> SOCIETY
                "stealth" -> STEALTH
                "survival" -> SURVIVAL
                "thievery" -> THIEVERY
                else -> throw IllegalArgumentException("unknown skill: $value")
            }
    }
}

data object Perception : Attribute {
    override val value = "perception"
}

class Lore(override val value: String) : Attribute

enum class SkillRank(val value: Int) {
    UNTRAINED(0),
    TRAINED(1),
    EXPERT(2),
    MASTER(3),
    LEGENDARY(4);

    companion object {
        fun fromInt(value: Int) = when (value) {
            0 -> UNTRAINED
            1 -> TRAINED
            2 -> EXPERT
            3 -> MASTER
            4 -> LEGENDARY
            else -> throw IllegalArgumentException("unknown rank $value")
        }
    }
}