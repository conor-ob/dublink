package ie.dublinmapper.service.jcdecaux

import com.google.gson.annotations.SerializedName

data class StationPositionJson(
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("lng") var lng: Double? = null
)
