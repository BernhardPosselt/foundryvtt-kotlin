package at.posselt.kingmaker.actor

import com.foundryvtt.pf2e.actor.PF2EActorSkill
import com.foundryvtt.pf2e.actor.PF2ECharacter


sealed interface CharacterSkill {
    val value: String
}

enum class Skill(override val value: String) : CharacterSkill {
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
    THIEVERY("thievery"),
}

class Lore(override val value: String) : CharacterSkill

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

fun resolveSkill(actor: PF2ECharacter, skill: String): PF2EActorSkill? =
    actor.skills[skill]

fun resolveSkill(actor: PF2ECharacter, skill: CharacterSkill): PF2EActorSkill? =
    actor.skills[skill.value]