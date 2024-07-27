package at.posselt.kingmaker

fun String.unslugify(): String =
    split("-")
        .joinToString(" ")
        .replaceFirstChar(Char::uppercase)

fun String.deCamelCase(): String =
    this.split("(?=\\p{Upper})".toRegex())
        .map { it.replaceFirstChar { c -> c.uppercase() } }
        .joinToString(" ")
