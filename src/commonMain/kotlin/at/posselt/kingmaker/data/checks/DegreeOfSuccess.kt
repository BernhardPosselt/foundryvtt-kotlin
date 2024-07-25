package at.posselt.kingmaker.data.checks

import kotlin.math.abs

enum class DegreeOfSuccess(val value: String) {
    CRITICAL_FAILURE("criticalFailure"),
    FAILURE("failure"),
    SUCCESS("success"),
    CRITICAL_SUCCESS("criticalSuccess");

    fun succeeded() = this >= SUCCESS
    fun failed() = this < SUCCESS

    fun downgrade() = when (this) {
        CRITICAL_FAILURE -> CRITICAL_FAILURE
        FAILURE -> CRITICAL_FAILURE
        SUCCESS -> FAILURE
        CRITICAL_SUCCESS -> SUCCESS
    }

    fun upgrade() = when (this) {
        CRITICAL_FAILURE -> FAILURE
        FAILURE -> SUCCESS
        SUCCESS -> CRITICAL_SUCCESS
        CRITICAL_SUCCESS -> CRITICAL_SUCCESS
    }

    companion object {
        fun fromString(value: String): DegreeOfSuccess {
            return when (value) {
                "criticalSuccess" -> CRITICAL_SUCCESS
                "success" -> SUCCESS
                "failure" -> FAILURE
                "criticalFailure" -> CRITICAL_FAILURE
                else -> throw IllegalArgumentException("Unknown DegreeOfSuccess value: $value")
            }
        }

        fun fromOrdinal(value: Int): DegreeOfSuccess = entries[value]
    }
}

fun <T> Int.repeat(initial: T, block: (T) -> T): T {
    var accumulator = initial
    kotlin.repeat(this) {
        accumulator = block(accumulator)
    }
    return accumulator
}


fun determineDegreeOfSuccess(dc: Int, total: Int, dieValue: Int): DegreeOfSuccess {
    val result = if (total >= dc) DegreeOfSuccess.SUCCESS else DegreeOfSuccess.FAILURE
    var upgrades = 0
    var downgrades = 0
    if (dieValue == 1) downgrades += 1
    if (total <= dc - 10) downgrades += 1
    if (dieValue == 20) upgrades += 1
    if (total >= dc + 10) upgrades += 1
    val difference = upgrades - downgrades
    return if (difference > 0) {
        difference.repeat(result) { it.upgrade() }
    } else if (difference < 0) {
        abs(difference).repeat(result) { it.downgrade() }
    } else {
        result
    }
}