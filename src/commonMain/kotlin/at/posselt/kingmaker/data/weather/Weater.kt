package at.posselt.kingmaker.data.weather

enum class Season(val value: String) {
    SPRING("spring"),
    SUMMER("summer"),
    FALL("fall"),
    WINTER("winter");

    companion object {
        fun fromString(value: String): Season {
            return when (value) {
                "spring" -> SPRING
                "summer" -> SUMMER
                "fall" -> FALL
                "winter" -> WINTER
                else -> throw IllegalArgumentException("$value is not a season")
            }
        }
    }
}

enum class Month(val value: String, val index: Int) {
    ABADIUS("abadius", 1),
    CALISTRIL("calistril", 2),
    PHARAST("pharast", 3),
    GOZRAN("gozran", 4),
    DESNUS("desnus", 5),
    SARENITH("sarenith", 6),
    ERASTUS("erastus", 7),
    ARODUS("arodus", 8),
    ROVA("rova", 9),
    LAMASHAN("lamashan", 10),
    NETH("neth", 11),
    KUTHONA("kuthona", 12);

    companion object {
        fun fromString(value: String): Month {
            return when (value) {
                "abadius" -> ABADIUS
                "calistril" -> CALISTRIL
                "pharast" -> PHARAST
                "gozran" -> GOZRAN
                "desnus" -> DESNUS
                "sarenith" -> SARENITH
                "erastus" -> ERASTUS
                "arodus" -> ARODUS
                "rova" -> ROVA
                "lamashan" -> LAMASHAN
                "neth" -> NETH
                "kuthona" -> KUTHONA
                else -> throw IllegalArgumentException("$value is not a month")
            }
        }
    }
}

data class WeatherMonth(
    val coldDc: Int,
    val precipitationDc: Int,
    val season: Season
)

data class WeatherData(
    val abadius: WeatherMonth,
    val calistril: WeatherMonth,
    val pharast: WeatherMonth,
    val gozran: WeatherMonth,
    val desnus: WeatherMonth,
    val sarenith: WeatherMonth,
    val erastus: WeatherMonth,
    val arodus: WeatherMonth,
    val rova: WeatherMonth,
    val lamashan: WeatherMonth,
    val neth: WeatherMonth,
    val kuthona: WeatherMonth,
)
