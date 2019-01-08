package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiRealTimeBusInformationResponseJson(
    @SerializedName("errorcode") var errorCode: String? = null,
    @SerializedName("errormessage") var errorMessage: String? = null,
    @SerializedName("numberofresults") var resultsCount: Int? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("results") var realTimeBusInformation: List<RtpiRealTimeBusInformationJson> = mutableListOf()
)

data class RtpiRealTimeBusInformationJson(
    @SerializedName("route") var route: String? = null,
    @SerializedName("operator") var operator: String? = null,
    @SerializedName("destination") var destination: String? = null,
    @SerializedName("direction") var direction: String? = null,
    @SerializedName("duetime") var duetime: String? = null,
    @SerializedName("arrivaldatetime") var expectedTime: String? = null
)
