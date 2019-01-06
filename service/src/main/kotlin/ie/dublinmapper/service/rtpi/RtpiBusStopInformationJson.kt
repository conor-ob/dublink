package ie.dublinmapper.service.rtpi

import com.google.gson.annotations.SerializedName

data class RtpiBusStopInformationJson(
    @SerializedName("stopid") var id: String? = null,
    @SerializedName("displaystopid") var displayId: String? = null,
    @SerializedName("shortname") var shortName: String? = null,
    @SerializedName("shortnamelocalized") var shortNameIrish: String? = null,
    @SerializedName("fullname") var fullName: String? = null,
    @SerializedName("fullnamelocalized") var fullNameIrish: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("lastupdated") var lastUpdated: String? = null,
    @SerializedName("operators") var operators: List<RtpiBusStopOperatorInformationJson>? = null
)
