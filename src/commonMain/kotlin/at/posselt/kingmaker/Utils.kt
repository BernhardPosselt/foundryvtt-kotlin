package at.posselt.kingmaker

import kotlin.enums.enumEntries

/**
 * Use this to deserialize foundry enum strings
 */
inline fun <reified T : Enum<T>> fromCamelCase(value: String): T? =
    enumEntries<T>().find { it.name == value.toEnumConstant() }

/**
 * Use this to serialize to foundry enum strings
 */
fun <T : Enum<T>> Enum<T>.toCamelCase(): String =
    name.split("_")
        .joinToString("") { it.lowercase().replaceFirstChar(Char::uppercase) }
        .replaceFirstChar(Char::lowercase)

fun <T : Enum<T>> Enum<T>.toLabel(): String =
    name.split("_")
        .joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

inline fun <reified T : Enum<T>> fromOrdinal(index: Int): T? =
    enumEntries<T>().getOrNull(index)


fun String.slugify(): String =
    split(" ")
        .filter { it.isNotBlank() }
        .joinToString("-") { it.trim().lowercase() }

fun String.unslugify(): String =
    split("-")
        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

fun String.deCamelCase(): String =
    this.split("(?=\\p{Upper})".toRegex())
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

fun String.toEnumConstant(): String =
    this.split("(?=\\p{Upper})".toRegex())
        .joinToString("_") { it.uppercase() }
