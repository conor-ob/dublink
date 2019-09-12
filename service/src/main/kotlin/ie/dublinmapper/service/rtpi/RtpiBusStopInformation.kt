package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiBusStopInformationResponseJson(
    @SerializedName("errorcode") val errorCode: String,
    @SerializedName("errormessage") val errorMessage: String,
//    @SerializedName("numberofresults") val numberOfResults: Int? = null,
//    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("results") val results: List<RtpiBusStopInformationJson> = mutableListOf()
)

data class RtpiBusStopInformationJson(
    @SerializedName("stopid") val stopId: String,
//    @SerializedName("displaystopid") val displayId: String? = null,
//    @SerializedName("shortname") val shortName: String? = null,
//    @SerializedName("shortnamelocalized") val shortNameLocalized: String? = null,
    @SerializedName("fullname") val fullName: String,
//    @SerializedName("fullnamelocalized") val fullNameLocalized: String? = null,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
//    @SerializedName("lastupdated") val lastUpdated: String? = null,
    @SerializedName("operators") val operators: List<RtpiBusStopOperatorInformationJson> = mutableListOf()
)

data class RtpiBusStopOperatorInformationJson(
    @SerializedName("name") val name: String,
//    @SerializedName("operatortype") val operatorType: Int? = null,
    @SerializedName("routes") val routes: List<String> = mutableListOf()
)
