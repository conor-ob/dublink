package ie.dublinmapper.service.jcdecaux

import com.google.gson.annotations.SerializedName

data class StationJson(
    @SerializedName("number") var number: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("position") var position: StationPositionJson? = null,
    @SerializedName("banking") var banking: Boolean? = null,
    @SerializedName("bonus") var bonus: Boolean? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("contract_name") var contractName: String? = null,
    @SerializedName("bike_stands") var bikeStands: Int? = null,
    @SerializedName("available_bike_stands") var availableBikeStands: Int? = null,
    @SerializedName("available_bikes") var availableBikes: Int? = null,
    @SerializedName("last_update") var lastUpdate: Long? = null
)

data class StationPositionJson(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null
)

