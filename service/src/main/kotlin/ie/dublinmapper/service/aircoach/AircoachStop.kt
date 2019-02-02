package ie.dublinmapper.service.aircoach

import com.google.gson.annotations.SerializedName

data class AircoachStopJson(
    @SerializedName("id") val id: String,
    @SerializedName("stopId") val stopId: String,
    @SerializedName("name") val name: String,
    @SerializedName("shortName") val shortName: String,
    @SerializedName("linkName") val linkName: String,
    @SerializedName("ticketName") val ticketName: String,
    @SerializedName("place") val place: String,
    @SerializedName("stopLatitude") val stopLatitude: Double,
    @SerializedName("stopLongitude") val stopLongitude: Double,
    @SerializedName("services") val services: List<AircoachStopServiceJson> = mutableListOf()
)

data class AircoachStopServiceJson(
    @SerializedName("route") val route: String,
    @SerializedName("dir") val dir: String,
    @SerializedName("linkName") val linkName: String
)
