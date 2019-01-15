package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiRouteListInformationWithVariantsResponseJson(
    @SerializedName("errorcode") val errorCode: String,
    @SerializedName("errormessage") val errorMessage: String,
//    @SerializedName("numberofresults") val resultsCount: Int? = null,
//    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("results") val results: List<RtpiRouteListInformationWithVariantsJson> = mutableListOf()
)

data class RtpiRouteListInformationWithVariantsJson(
    @SerializedName("operator") val operator: String,
//    @SerializedName("operatortype") val operatorType: String? = null,
    @SerializedName("route") val route: String,
    @SerializedName("Variants") val variants: List<RtpiRouteListInformationVariantJson> = mutableListOf()
)

data class RtpiRouteListInformationVariantJson(
    @SerializedName("origin") val origin: String,
//    @SerializedName("originlocalized") val originLocalized: String? = null,
    @SerializedName("destination") val destination: String
//    @SerializedName("destinationlocalized") val destinationLocalized: String? = null,
)
