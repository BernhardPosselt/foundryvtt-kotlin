package at.posselt.kingmaker

enum class RollMode(val value: String) {
    PUBLIC("publicroll"),
    PRIVATE("gmroll"),
    BLIND("blindroll"),
    SELF("selfroll");

    companion object {
        fun fromString(rollMode: String): RollMode {
            return when (rollMode) {
                "public" -> PUBLIC
                "private" -> PRIVATE
                "blindroll" -> BLIND
                "selfroll" -> SELF
                else -> throw IllegalArgumentException("Illegal roll mode: $rollMode")
            }
        }
    }
}