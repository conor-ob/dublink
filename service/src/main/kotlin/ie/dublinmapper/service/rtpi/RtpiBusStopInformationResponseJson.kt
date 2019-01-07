package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiBusStopInformationResponseJson(
    @SerializedName("errorcode") var errorCode: String? = null,
    @SerializedName("errormessage") var errorMessage: String? = null,
    @SerializedName("numberofresults") var resultsCount: Int? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("results") var stops: List<RtpiBusStopInformationJson> = mutableListOf()
)
