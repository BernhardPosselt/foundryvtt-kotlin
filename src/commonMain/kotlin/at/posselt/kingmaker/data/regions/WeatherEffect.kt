package at.posselt.kingmaker.data.regions

enum class WeatherEffect(val value: String) {
    SNOW("snow"),
    RAIN("rain"),
    SUNNY("sunny"),
    LEAVES("leaves"),
    RAIN_STORM("rainStorm"),
    FOG("fog"),
    BLIZZARD("blizzard");

    companion object {
        fun fromString(value: String): WeatherEffect {
            return when (value) {
                "snow" -> SNOW
                "rain" -> RAIN
                "sunny" -> SUNNY
                "leaves" -> LEAVES
                "rainStorm" -> RAIN_STORM
                "fog" -> FOG
                "blizzard" -> BLIZZARD
                else -> throw IllegalArgumentException("$value is not a valid weather effect")
            }
        }
    }
}

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
    val coldDc: Int? = null,
    val precipitationDc: Int,
    val month: Month,
    val season: Season,
)

val stolenLandsWeather = arrayOf(
    WeatherMonth(month = Month.ABADIUS, precipitationDc = 8, coldDc = 16, season = Season.WINTER),
    WeatherMonth(month = Month.CALISTRIL, precipitationDc = 8, coldDc = 18, season = Season.WINTER),
    WeatherMonth(month = Month.PHARAST, precipitationDc = 15, season = Season.SPRING),
    WeatherMonth(month = Month.GOZRAN, precipitationDc = 15, season = Season.SPRING),
    WeatherMonth(month = Month.DESNUS, precipitationDc = 15, season = Season.SPRING),
    WeatherMonth(month = Month.SARENITH, precipitationDc = 20, season = Season.SUMMER),
    WeatherMonth(month = Month.ERASTUS, precipitationDc = 20, season = Season.SUMMER),
    WeatherMonth(month = Month.ARODUS, precipitationDc = 20, season = Season.SUMMER),
    WeatherMonth(month = Month.ROVA, precipitationDc = 15, season = Season.FALL),
    WeatherMonth(month = Month.LAMASHAN, precipitationDc = 15, season = Season.FALL),
    WeatherMonth(month = Month.NETH, precipitationDc = 15, season = Season.FALL),
    WeatherMonth(month = Month.KUTHONA, precipitationDc = 8, coldDc = 18, season = Season.WINTER),
)

enum class WeatherType {
    COLD,
    SNOWY,
    RAINY,
    SUNNY,
}

fun findWeatherType(isCold: Boolean, hasPrecipitation: Boolean) =
    if (isCold && hasPrecipitation) {
        WeatherType.SNOWY
    } else if (isCold) {
        WeatherType.COLD
    } else if (hasPrecipitation) {
        WeatherType.RAINY
    } else {
        WeatherType.SUNNY
    }