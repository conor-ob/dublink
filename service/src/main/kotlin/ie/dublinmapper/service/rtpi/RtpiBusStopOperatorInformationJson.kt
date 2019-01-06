package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiBusStopOperatorInformationJson(
    @SerializedName("name") var name: String? = null,
    @SerializedName("operatortype") var operatorType: Int? = null,
    @SerializedName("routes") var routes: List<String>? = null
)
