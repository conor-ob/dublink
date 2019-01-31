package ie.dublinmapper.service.aircoach

import com.google.gson.annotations.SerializedName

data class ServiceResponseJson(
    @SerializedName("name") val name: String
//    @SerializedName("services") val services: List<ServiceJson> = mutableListOf()
)

data class ServiceJson(
    @SerializedName("date") val date: String,
    @SerializedName("linkDate") val linkDate: String,
    @SerializedName("time") val time: TimeJson,
    @SerializedName("startTime") val startTime: TimestampJson,
    @SerializedName("route") val route: String,
    @SerializedName("dir") val dir: String,
    @SerializedName("colour") val colour: String,
    @SerializedName("depart") val depart: String,
    @SerializedName("arrival") val arrival: String,
    @SerializedName("journeyId") val journeyId: String,
    @SerializedName("linkName") val linkName: String,
    @SerializedName("stopType") val stopType: Int,
    @SerializedName("live") val live: String,
    @SerializedName("dups") val dups: String,
    @SerializedName("eta") val eta: String,
    @SerializedName("delayed") val delayed: Boolean
)

data class TimeJson(
    @SerializedName("arrive") val arrive: TimestampJson,
    @SerializedName("depart") val depart: TimestampJson,
    @SerializedName("layover") val layover: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("timeZone") val timeZone: String,
    @SerializedName("actualStatus") val actualStatus: Int,
    @SerializedName("duplicateCount") val duplicateCount: Int
)

data class TimestampJson(
    @SerializedName("dateTime") val dateTime: String,
    @SerializedName("hrs") val hrs: Int,
    @SerializedName("mins") val mins: Int
)
