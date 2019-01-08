package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiRouteListInformationWithVariantsResponseJson(
    @SerializedName("errorcode") var errorCode: String? = null,
    @SerializedName("errormessage") var errorMessage: String? = null,
    @SerializedName("numberofresults") var resultsCount: Int? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("results") var routes: List<RtpiRouteListInformationWithVariantsJson> = mutableListOf()
)

data class RtpiRouteListInformationWithVariantsJson(
    @SerializedName("operator") var operator: String? = null,
    @SerializedName("route") var route: String? = null,
    @SerializedName("Variants") var variants: List<RtpiRouteListInformationVariantJson> = mutableListOf()
)

data class RtpiRouteListInformationVariantJson(
    @SerializedName("origin") var origin: String? = null,
    @SerializedName("destination") var destination: String? = null
)
