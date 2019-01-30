package ie.dublinmapper.service.swordsexpress

import com.google.gson.annotations.SerializedName

data class SwordsExpressStopJson(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("direction") val direction: String,
    @SerializedName("zindex") val zindex: Int
)
