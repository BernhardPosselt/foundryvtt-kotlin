package at.posselt.kingmaker.kingdom

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface RuinAmount {
    val value: Int
    val ruin: String
    val moreThanOncePerTurn: Boolean?
}

@JsPlainObject
external interface ReduceUnrestBy {
    val value: String
    val moreThanOncePerTurn: Boolean?
    val note: String
}

@JsPlainObject
external interface LeadershipActivityRule {
    val value: Int
}

@JsPlainObject
external interface SettlementEventsRule {
    val value: Int
}

@JsPlainObject
external interface ActivityBonusRule {
    val value: Int
    val activity: String
}

@JsPlainObject
external interface AvailableItemsRule {
    val value: Int
    val group: String?
    val maximumStacks: Int?
}

@JsPlainObject
external interface SkillBonusRule {
    val value: Int
    val skill: String
    val activity: String?
}

@JsPlainObject
external interface CommodityStorage {
    val ore: Int?
    val food: Int?
    val lumber: Int?
    val stone: Int?
    val luxuries: Int?
}


@JsPlainObject
external interface ConstructionSkill {
    val skill: String
    val proficiencyRank: Int?
}

@JsPlainObject
external interface Construction {
    val skills: Array<ConstructionSkill>
    val lumber: Int?
    val luxuries: Int?
    val ore: Int?
    val stone: Int?
    val rp: Int
    val dc: Int
}


@JsPlainObject
external interface StructureData {
    val name: String
    val stacksWith: String?
    val construction: Construction?
    val notes: String?
    val preventItemLevelPenalty: Boolean?
    val enableCapitalInvestment: Boolean
    val skillBonusRules: Array<SkillBonusRule>?
    val activityBonusRules: Array<ActivityBonusRule>?
    val availableItemsRules: Array<AvailableItemsRule>?
    val settlementEventRules: Array<SettlementEventsRule>?
    val leadershipActivityRules: Array<LeadershipActivityRule>?
    val storage: CommodityStorage?
    val increaseLeadershipActivities: Boolean?
    val isBridge: Boolean?
    val consumptionReduction: Int?
    val unlockActivities: Array<String>?
    val traits: Array<String>?
    val lots: Int?
    val affectsEvents: Boolean?
    val affectsDowntime: Boolean?
    val reducesUnrest: Boolean?
    val reducesRuin: Boolean?
    val level: Int?
    val upgradeFrom: Array<String>?
    val reduceUnrestBy: ReduceUnrestBy?
    val reduceRuinBy: RuinAmount?
    val gainRuin: RuinAmount?
}

@JsModule("./data/structures.json")
external val structures: Array<StructureData>