package at.posselt.kingmaker.data.regions

enum class WeatherEffect {
    NONE,
    SNOW,
    RAIN,
    SUNNY,
    LEAVES,
    RAIN_STORM,
    FOG,
    BLIZZARD;
}

enum class Season {
    SPRING,
    SUMMER,
    FALL,
    WINTER;
}

enum class Month {
    ABADIUS,
    CALISTRIL,
    PHARAST,
    GOZRAN,
    DESNUS,
    SARENITH,
    ERASTUS,
    ARODUS,
    ROVA,
    LAMASHAN,
    NETH,
    KUTHONA;
}

data class Climate(
    val coldDc: Int? = null,
    val precipitationDc: Int? = null,
    val month: Month,
    val season: Season,
    val weatherEventDc: Int? = 18
)

val stolenLandsWeather = arrayOf(
    Climate(month = Month.ABADIUS, precipitationDc = 8, coldDc = 16, season = Season.WINTER),
    Climate(month = Month.CALISTRIL, precipitationDc = 8, coldDc = 18, season = Season.WINTER),
    Climate(month = Month.PHARAST, precipitationDc = 15, season = Season.SPRING),
    Climate(month = Month.GOZRAN, precipitationDc = 15, season = Season.SPRING),
    Climate(month = Month.DESNUS, precipitationDc = 15, season = Season.SPRING),
    Climate(month = Month.SARENITH, precipitationDc = 20, season = Season.SUMMER),
    Climate(month = Month.ERASTUS, precipitationDc = 20, season = Season.SUMMER),
    Climate(month = Month.ARODUS, precipitationDc = 20, season = Season.SUMMER),
    Climate(month = Month.ROVA, precipitationDc = 15, season = Season.FALL),
    Climate(month = Month.LAMASHAN, precipitationDc = 15, season = Season.FALL),
    Climate(month = Month.NETH, precipitationDc = 15, season = Season.FALL),
    Climate(month = Month.KUTHONA, precipitationDc = 8, coldDc = 18, season = Season.WINTER),
)

enum class WeatherType {
    COLD,
    SNOWY,
    RAINY,
    SUNNY,
}

fun getMonth(index: Int): Month =
    Month.entries[index]

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
