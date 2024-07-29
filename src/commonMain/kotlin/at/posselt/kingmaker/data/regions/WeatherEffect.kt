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