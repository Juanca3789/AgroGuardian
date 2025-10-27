import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyData
)

@Serializable
data class HourlyData(
    @SerialName("temperature_2m")
    val temperature: List<Double>,

    @SerialName("relative_humidity_2m")
    val humidity: List<Int>,

    @SerialName("precipitation")
    val precipitation: List<Double>,

    val time: List<String>,

    @SerialName("weathercode")
    val weatherCode: List<Int>? = null
)