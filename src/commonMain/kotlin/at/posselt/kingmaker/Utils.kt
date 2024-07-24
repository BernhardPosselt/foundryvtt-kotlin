package at.posselt.kingmaker

fun String.unslugify(): String =
    split("-")
        .joinToString(" ")
        .replaceFirstChar(Char::uppercase)
