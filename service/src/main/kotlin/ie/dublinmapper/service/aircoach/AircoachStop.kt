package ie.dublinmapper.service.aircoach

import com.google.gson.annotations.SerializedName

data class AircoachStopJson(
    @SerializedName("id") val id: String,
    @SerializedName("stopId") val stopId: String,
    @SerializedName("name") val name: String,
    @SerializedName("ticketName") val ticketName: String,
    @SerializedName("stopLatitude") val stopLatitude: Double,
    @SerializedName("stopLongitude") val stopLongitude: Double,
    @SerializedName("routes") val routes: List<String> = mutableListOf()
)
