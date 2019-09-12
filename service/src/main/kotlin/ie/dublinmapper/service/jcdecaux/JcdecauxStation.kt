package ie.dublinmapper.service.jcdecaux

import com.google.gson.annotations.SerializedName

data class StationJson(
    @SerializedName("number") val number: Int,
//    @SerializedName("name") val name: String? = null,
    @SerializedName("address") val address: String,
    @SerializedName("position") val position: StationPositionJson,
//    @SerializedName("banking") val banking: Boolean? = null,
//    @SerializedName("bonus") val bonus: Boolean? = null,
//    @SerializedName("status") val status: String? = null,
//    @SerializedName("contract_name") val contractName: String? = null,
    @SerializedName("bike_stands") val bikeStands: Int,
    @SerializedName("available_bike_stands") val availableBikeStands: Int,
    @SerializedName("available_bikes") val availableBikes: Int
//    @SerializedName("last_update") val lastUpdate: Long? = null
)

data class StationPositionJson(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

