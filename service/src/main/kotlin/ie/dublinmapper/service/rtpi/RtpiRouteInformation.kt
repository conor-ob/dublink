package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiRouteInformationResponseJson(
    @SerializedName("errorcode") var errorCode: String? = null,
    @SerializedName("errormessage") var errorMessage: String? = null,
    @SerializedName("numberofresults") var resultsCount: Int? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("results") var results: List<RtpiRouteInformationJson> = mutableListOf()
)

data class RtpiRouteInformationJson(
    @SerializedName("origin") var origin: String? = null,
    @SerializedName("destination") var destination: String? = null,
    @SerializedName("stops") var stops: List<RtpiBusStopInformationJson> = mutableListOf()
)
