package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiRealTimeBusInformationResponseJson(
    @SerializedName("errorcode") val errorCode: String,
    @SerializedName("errormessage") val errorMessage: String,
//    @SerializedName("numberofresults") val numberOfResults: Int? = null,
//    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("results") val results: List<RtpiRealTimeBusInformationJson> = mutableListOf()
)

data class RtpiRealTimeBusInformationJson(
    @SerializedName("arrivaldatetime") val arrivalDateTime: String,
//    @SerializedName("duetime") val dueTime: String? = null,
//    @SerializedName("departuredatetime") val departureDateTime: String? = null,
//    @SerializedName("departureduetime") val departureDueTime: String? = null,
//    @SerializedName("scheduledarrivaldatetime") val scheduledArrivalDateTime: String? = null,
//    @SerializedName("scheduleddeparturedatetime") val scheduledDepartureDateTime: String? = null,
    @SerializedName("destination") val destination: String,
//    @SerializedName("destinationlocalized") val destinationLocalized: String? = null,
    @SerializedName("origin") val origin: String,
//    @SerializedName("originlocalized") val originLocalized: String? = null,
    @SerializedName("direction") val direction: String,
//    @SerializedName("operator") val operator: String? = null,
//    @SerializedName("operatortype") val operatorType: String? = null,
//    @SerializedName("additionalinformation") val additionalInformation: String? = null,
//    @SerializedName("lowfloorstatus") val lowFloorStatus: String? = null,
    @SerializedName("route") val route: String
//    @SerializedName("sourcetimestamp") val sourceTimestamp: String? = null,
//    @SerializedName("monitored") val monitored: String? = null
)
